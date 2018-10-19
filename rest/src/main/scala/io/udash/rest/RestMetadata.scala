package io.udash
package rest

import com.avsystem.commons._
import com.avsystem.commons.meta._
import com.avsystem.commons.rpc._

import scala.annotation.implicitNotFound

@implicitNotFound("RestMetadata for ${T} not found, does it have a correctly defined companion?")
@methodTag[RestMethodTag]
case class RestMetadata[T](
  @multi @tagged[Prefix](whenUntagged = new Prefix)
  @paramTag[RestParamTag](defaultTag = new Path)
  @rpcMethodMetadata prefixMethods: Mapping[PrefixMetadata[_]],

  @multi @tagged[GET]
  @paramTag[RestParamTag](defaultTag = new Query)
  @rpcMethodMetadata httpGetMethods: Mapping[HttpMethodMetadata[_]],

  @multi @tagged[BodyMethodTag](whenUntagged = new POST)
  @paramTag[RestParamTag](defaultTag = new BodyField)
  @rpcMethodMetadata httpBodyMethods: Mapping[HttpMethodMetadata[_]]
) {
  val httpMethods: Mapping[HttpMethodMetadata[_]] =
    httpGetMethods ++ httpBodyMethods

  private[this] lazy val valid: Unit = {
    ensureUnambiguousPaths()
    ensureUniqueParams(Nil)
  }

  def ensureValid(): Unit = valid

  private def ensureUniqueParams(prefixes: List[(String, PrefixMetadata[_])]): Unit = {
    def ensureUniqueParams(methodName: String, method: RestMethodMetadata[_]): Unit = {
      for {
        (prefixName, prefix) <- prefixes
        headerParam <- method.parametersMetadata.headers.keys
        if prefix.parametersMetadata.headers.contains(headerParam)
      } throw new InvalidRestApiException(
        s"Header parameter $headerParam of $methodName collides with header parameter of the same name in prefix $prefixName")

      for {
        (prefixName, prefix) <- prefixes
        queryParam <- method.parametersMetadata.query.keys
        if prefix.parametersMetadata.query.contains(queryParam)
      } throw new InvalidRestApiException(
        s"Query parameter $queryParam of $methodName collides with query parameter of the same name in prefix $prefixName")
    }

    prefixMethods.foreach {
      case (name, prefix) =>
        ensureUniqueParams(name, prefix)
        prefix.result.value.ensureUniqueParams((name, prefix) :: prefixes)
    }
    (httpGetMethods ++ httpBodyMethods).foreach {
      case (name, method) => ensureUniqueParams(name, method)
    }
  }

  private def ensureUnambiguousPaths(): Unit = {
    val trie = new RestMetadata.Trie
    trie.fillWith(this)
    trie.mergeWildcardToNamed()
    val ambiguities = new MListBuffer[(String, List[String])]
    trie.collectAmbiguousCalls(ambiguities)
    if (ambiguities.nonEmpty) {
      val problems = ambiguities.map { case (path, chains) =>
        s"$path may result from multiple calls:\n  ${chains.mkString("\n  ")}"
      }
      throw new InvalidRestApiException(s"REST API has ambiguous paths:\n${problems.mkString("\n")}")
    }
  }

  def resolvePath(path: List[PathValue]): List[ResolvedCall] = {
    def resolve(metadata: RestMetadata[_], path: List[PathValue]): Iterator[ResolvedCall] = {
      val asFinalCall = for {
        (rpcName, methodMeta) <- metadata.httpMethods.iterator
        (pathParams, Nil) <- methodMeta.extractPathParams(path)
      } yield ResolvedCall(this, Nil, HttpCall(rpcName, pathParams, methodMeta))

      val usingPrefix = for {
        (rpcName, prefix) <- metadata.prefixMethods.iterator
        (pathParams, pathTail) <- prefix.extractPathParams(path).iterator
        suffixPath <- resolve(prefix.result.value, pathTail)
      } yield suffixPath.copy(prefixes = PrefixCall(rpcName, pathParams, prefix) :: suffixPath.prefixes)

      asFinalCall ++ usingPrefix
    }
    resolve(this, path).toList
  }
}
object RestMetadata extends RpcMetadataCompanion[RestMetadata] {
  private class Trie {
    val rpcChains: Map[HttpMethod, MBuffer[String]] =
      HttpMethod.values.mkMap(identity, _ => new MArrayBuffer[String])

    val byName: MMap[String, Trie] = new MHashMap
    var wildcard: Opt[Trie] = Opt.Empty

    def forPattern(pattern: List[PathPatternElement]): Trie = pattern match {
      case Nil => this
      case PathName(PathValue(pathName)) :: tail =>
        byName.getOrElseUpdate(pathName, new Trie).forPattern(tail)
      case PathParam(_) :: tail =>
        wildcard.getOrElse(new Trie().setup(t => wildcard = Opt(t))).forPattern(tail)
    }

    def fillWith(metadata: RestMetadata[_], prefixStack: List[(String, PrefixMetadata[_])] = Nil): Unit = {
      def prefixChain: String =
        prefixStack.reverseIterator.map({ case (k, _) => k }).mkStringOrEmpty("", "->", "->")

      metadata.prefixMethods.foreach { case entry@(rpcName, pm) =>
        if (prefixStack.contains(entry)) {
          throw new InvalidRestApiException(
            s"call chain $prefixChain$rpcName is recursive, recursively defined server APIs are forbidden")
        }
        forPattern(pm.pathPattern).fillWith(pm.result.value, entry :: prefixStack)
      }
      metadata.httpMethods.foreach { case (rpcName, hm) =>
        forPattern(hm.pathPattern).rpcChains(hm.method) += s"$prefixChain$rpcName"
      }
    }

    private def merge(other: Trie): Unit = {
      HttpMethod.values.foreach { meth =>
        rpcChains(meth) ++= other.rpcChains(meth)
      }
      for (w <- wildcard; ow <- other.wildcard) w.merge(ow)
      wildcard = wildcard orElse other.wildcard
      other.byName.foreach { case (name, trie) =>
        byName.getOrElseUpdate(name, new Trie).merge(trie)
      }
    }

    def mergeWildcardToNamed(): Unit = wildcard.foreach { wc =>
      wc.mergeWildcardToNamed()
      byName.values.foreach { trie =>
        trie.merge(wc)
        trie.mergeWildcardToNamed()
      }
    }

    def collectAmbiguousCalls(ambiguities: MBuffer[(String, List[String])], pathPrefix: List[String] = Nil): Unit = {
      rpcChains.foreach { case (method, chains) =>
        if (chains.size > 1) {
          val path = pathPrefix.reverse.mkString(s"$method /", "/", "")
          ambiguities += ((path, chains.toList))
        }
      }
      wildcard.foreach(_.collectAmbiguousCalls(ambiguities, "*" :: pathPrefix))
      byName.foreach { case (name, trie) =>
        trie.collectAmbiguousCalls(ambiguities, name :: pathPrefix)
      }
    }
  }
}

sealed trait PathPatternElement
case class PathName(value: PathValue) extends PathPatternElement
case class PathParam(param: PathParamMetadata[_]) extends PathPatternElement

sealed abstract class RestMethodMetadata[T] extends TypedMetadata[T] {
  def methodPath: List[PathValue]
  def parametersMetadata: RestParametersMetadata

  val pathPattern: List[PathPatternElement] = methodPath.map(PathName) ++
    parametersMetadata.path.flatMap(pp => PathParam(pp) :: pp.pathSuffix.map(PathName))

  def applyPathParams(params: List[PathValue]): List[PathValue] = {
    def loop(params: List[PathValue], pattern: List[PathPatternElement]): List[PathValue] =
      (params, pattern) match {
        case (Nil, Nil) => Nil
        case (_, PathName(patternHead) :: patternTail) => patternHead :: loop(params, patternTail)
        case (param :: paramsTail, PathParam(_) :: patternTail) => param :: loop(paramsTail, patternTail)
        case _ => throw new IllegalArgumentException(
          s"got ${params.size} path params, expected ${parametersMetadata.path.size}")
      }
    loop(params, pathPattern)
  }

  def extractPathParams(path: List[PathValue]): Opt[(List[PathValue], List[PathValue])] = {
    def loop(path: List[PathValue], pattern: List[PathPatternElement]): Opt[(List[PathValue], List[PathValue])] =
      (path, pattern) match {
        case (pathTail, Nil) => Opt((Nil, pathTail))
        case (param :: pathTail, PathParam(_) :: patternTail) =>
          loop(pathTail, patternTail).map { case (params, tail) => (param :: params, tail) }
        case (pathHead :: pathTail, PathName(patternHead) :: patternTail) if pathHead == patternHead =>
          loop(pathTail, patternTail)
        case _ => Opt.Empty
      }
    loop(path, pathPattern)
  }
}

case class PrefixMetadata[T](
  @reifyAnnot methodTag: Prefix,
  @composite parametersMetadata: RestParametersMetadata,
  @infer @checked result: RestMetadata.Lazy[T]
) extends RestMethodMetadata[T] {
  def methodPath: List[PathValue] = PathValue.splitDecode(methodTag.path)
}

case class HttpMethodMetadata[T](
  @reifyAnnot methodTag: HttpMethodTag,
  @composite parametersMetadata: RestParametersMetadata,
  @multi @tagged[BodyField] @rpcParamMetadata bodyFields: Mapping[ParamMetadata[_]],
  @optional @encoded @tagged[Body] @rpcParamMetadata singleBodyParam: Opt[ParamMetadata[_]],
  @isAnnotated[FormBody] formBody: Boolean,
  @infer @checked responseType: HttpResponseType[T]
) extends RestMethodMetadata[T] {
  val method: HttpMethod = methodTag.method
  val singleBody: Boolean = singleBodyParam.isDefined
  def methodPath: List[PathValue] = PathValue.splitDecode(methodTag.path)
}

/**
  * Typeclass used during [[RestMetadata]] materialization to determine whether a real method is a valid HTTP
  * method. Usually this means that the result must be a type wrapped into something that captures asynchronous
  * computation, e.g. `Future`. Because REST framework core tries to be agnostic about this
  * asynchronous wrapper (not everyone likes `Future`s), there are no default implicits provided for [[HttpResponseType]].
  * They must be provided externally.
  *
  * For example, [[FutureRestImplicits]] introduces an instance of [[HttpResponseType]] for `Future[T]`,
  * for arbitrary type `T`. For [[RestMetadata]] materialization this means that every method which returns a
  * `Future` is considered a valid HTTP method. [[FutureRestImplicits]] is injected into materialization of
  * [[RestMetadata]] through one of the base companion classes, e.g. [[DefaultRestApiCompanion]].
  * See [[com.avsystem.commons.meta.MacroInstances MacroInstances]] for more information on injection of implicits.
  */
@implicitNotFound("${T} is not a valid result type of HTTP REST method")
case class HttpResponseType[T]()

case class RestParametersMetadata(
  @multi @tagged[Path] @rpcParamMetadata path: List[PathParamMetadata[_]],
  @multi @tagged[Header] @rpcParamMetadata headers: Mapping[ParamMetadata[_]],
  @multi @tagged[Query] @rpcParamMetadata query: Mapping[ParamMetadata[_]]
)

case class ParamMetadata[T]() extends TypedMetadata[T]
case class PathParamMetadata[T](
  @reifyName(useRawName = true) name: String,
  @reifyAnnot pathAnnot: Path
) extends TypedMetadata[T] {
  val pathSuffix: List[PathValue] = PathValue.splitDecode(pathAnnot.pathSuffix)
}

class InvalidRestApiException(msg: String) extends RestException(msg)
