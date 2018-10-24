package io.udash
package rest
package raw

import com.avsystem.commons._
import com.avsystem.commons.meta._
import com.avsystem.commons.rpc._
import io.udash.rest.raw.RestMetadata.ResolutionTrie

import scala.annotation.implicitNotFound

@implicitNotFound("RestMetadata for ${T} not found, does it have a correctly defined companion object, " +
  "e.g. one that extends DefaultRestApiCompanion or other companion base?")
@methodTag[RestMethodTag]
case class RestMetadata[T](
  @multi @tagged[Prefix](whenUntagged = new Prefix)
  @paramTag[RestParamTag](defaultTag = new Path)
  @rpcMethodMetadata prefixMethods: List[PrefixMetadata[_]],

  @multi @tagged[GET]
  @paramTag[RestParamTag](defaultTag = new Query)
  @rpcMethodMetadata httpGetMethods: List[HttpMethodMetadata[_]],

  @multi @tagged[BodyMethodTag](whenUntagged = new POST)
  @paramTag[RestParamTag](defaultTag = new BodyField)
  @rpcMethodMetadata httpBodyMethods: List[HttpMethodMetadata[_]]
) {
  val httpMethods: List[HttpMethodMetadata[_]] =
    httpGetMethods ++ httpBodyMethods

  val prefixesByName: Map[String, PrefixMetadata[_]] =
    prefixMethods.toMapBy(_.name)

  val httpMethodsByName: Map[String, HttpMethodMetadata[_]] =
    httpMethods.toMapBy(_.name)

  private lazy val resolutionTrie = {
    val allMethods = (prefixMethods.iterator: Iterator[RestMethodMetadata[_]]) ++ httpMethods.iterator
    new ResolutionTrie(allMethods.map(m => (m.pathPattern, m)).toList)
  }

  private[this] lazy val valid: Unit = {
    ensureUnambiguousPaths()
    ensureUniqueParams(Nil)
  }

  def ensureValid(): Unit = valid

  private def ensureUniqueParams(prefixes: List[PrefixMetadata[_]]): Unit = {
    def ensureUniqueParams(method: RestMethodMetadata[_]): Unit = {
      for {
        prefix <- prefixes
        prefixHeaders = new Mapping(prefix.parametersMetadata.headers, caseInsensitive = true)
        headerParam <- method.parametersMetadata.headers.keys if prefixHeaders.contains(headerParam)
      } throw new InvalidRestApiException(
        s"Header parameter $headerParam of ${method.name} collides with header parameter of the same " +
          s"(case insensitive) name in prefix ${prefix.name}")

      for {
        prefix <- prefixes
        queryParam <- method.parametersMetadata.query.keys
        if prefix.parametersMetadata.query.contains(queryParam)
      } throw new InvalidRestApiException(
        s"Query parameter $queryParam of ${method.name} collides with query parameter of the same " +
          s"name in prefix ${prefix.name}")
    }

    prefixMethods.foreach {
      prefix =>
        ensureUniqueParams(prefix)
        prefix.result.value.ensureUniqueParams(prefix :: prefixes)
    }
    httpMethods.foreach(ensureUniqueParams)
  }

  private def ensureUnambiguousPaths(): Unit = {
    val trie = new RestMetadata.ValidationTrie
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

  def resolvePath(path: List[PathValue]): List[ResolvedCall] =
    resolutionTrie.resolvePath(this, Nil, Nil, path).toList
}
object RestMetadata extends RpcMetadataCompanion[RestMetadata] {
  private class ResolutionTrie(methods: List[(List[PathPatternElement], RestMethodMetadata[_])]) {
    private val named: Map[PathValue, ResolutionTrie] = methods.iterator
      .collect { case (PathName(pv) :: tail, method) => (pv, (tail, method)) }
      .groupToMap(_._1, _._2).iterator
      .map { case (pv, submethods) => (pv, new ResolutionTrie(submethods.toList)) }
      .toMap

    private val wildcard: Opt[ResolutionTrie] =
      methods.collect { case (PathParam(_) :: tail, method) => (tail, method) }
        .opt.filter(_.nonEmpty).map(new ResolutionTrie(_))

    private val httpMethods: List[HttpMethodMetadata[_]] = methods
      .collect { case (Nil, hmm: HttpMethodMetadata[_]) => hmm }

    private val prefixes: List[PrefixMetadata[_]] = methods
      .collect { case (Nil, pm: PrefixMetadata[_]) => pm }

    def resolvePath(
      root: RestMetadata[_], prefixCalls: List[PrefixCall], pathParams: List[PathValue], path: List[PathValue]
    ): Iterator[ResolvedCall] = {

      val fromPrefixes = prefixes.iterator.flatMap { prefix =>
        val prefixCall = PrefixCall(pathParams.reverse, prefix)
        prefix.result.value.resolutionTrie.resolvePath(root, prefixCall :: prefixCalls, Nil, path)
      }

      val fromSelf = path match {
        case Nil =>
          httpMethods.iterator.map(hm => ResolvedCall(root, prefixCalls.reverse, HttpCall(pathParams.reverse, hm)))
        case head :: tail =>
          val fromWildcard =
            wildcard.iterator.flatMap(_.resolvePath(root, prefixCalls, head :: pathParams, tail))
          val fromNamed =
            named.getOpt(head).iterator.flatMap(_.resolvePath(root, prefixCalls, pathParams, tail))
          fromWildcard ++ fromNamed
      }

      fromPrefixes ++ fromSelf
    }
  }

  private class ValidationTrie {
    val rpcChains: Map[HttpMethod, MBuffer[String]] =
      HttpMethod.values.mkMap(identity, _ => new MArrayBuffer[String])

    val byName: MMap[String, ValidationTrie] = new MHashMap
    var wildcard: Opt[ValidationTrie] = Opt.Empty

    def forPattern(pattern: List[PathPatternElement]): ValidationTrie = pattern match {
      case Nil => this
      case PathName(PathValue(pathName)) :: tail =>
        byName.getOrElseUpdate(pathName, new ValidationTrie).forPattern(tail)
      case PathParam(_) :: tail =>
        wildcard.getOrElse(new ValidationTrie().setup(t => wildcard = Opt(t))).forPattern(tail)
    }

    def fillWith(metadata: RestMetadata[_], prefixStack: List[PrefixMetadata[_]] = Nil): Unit = {
      def prefixChain: String =
        prefixStack.reverseIterator.map(_.name).mkStringOrEmpty("", "->", "->")

      metadata.prefixMethods.foreach { pm =>
        if (prefixStack.contains(pm)) {
          throw new InvalidRestApiException(
            s"call chain $prefixChain${pm.name} is recursive, recursively defined server APIs are forbidden")
        }
        forPattern(pm.pathPattern).fillWith(pm.result.value, pm :: prefixStack)
      }
      metadata.httpMethods.foreach { hm =>
        forPattern(hm.pathPattern).rpcChains(hm.method) += s"$prefixChain${hm.name}"
      }
    }

    private def merge(other: ValidationTrie): Unit = {
      HttpMethod.values.foreach { meth =>
        rpcChains(meth) ++= other.rpcChains(meth)
      }
      for (w <- wildcard; ow <- other.wildcard) w.merge(ow)
      wildcard = wildcard orElse other.wildcard
      other.byName.foreach { case (name, trie) =>
        byName.getOrElseUpdate(name, new ValidationTrie).merge(trie)
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
  def name: String
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
  @reifyName(useRawName = true) name: String,
  @reifyAnnot methodTag: Prefix,
  @composite parametersMetadata: RestParametersMetadata,
  @infer @checked result: RestMetadata.Lazy[T]
) extends RestMethodMetadata[T] {
  def methodPath: List[PathValue] = PathValue.splitDecode(methodTag.path)
}

case class HttpMethodMetadata[T](
  @reifyName(useRawName = true) name: String,
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
