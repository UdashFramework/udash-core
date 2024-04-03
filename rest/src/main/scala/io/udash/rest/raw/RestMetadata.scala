package io.udash
package rest
package raw

import com.avsystem.commons._
import com.avsystem.commons.meta._
import com.avsystem.commons.rpc._
import io.udash.macros.RestMacros
import io.udash.rest.raw.RestMetadata.ResolutionTrie
import monix.eval.{Task, TaskLike}

import scala.annotation.implicitNotFound

@implicitNotFound("RestMetadata for ${T} not found, " +
  "is it a valid REST API trait with properly defined companion object?")
@methodTag[RestMethodTag]
@methodTag[BodyTypeTag]
final case class RestMetadata[T](
  @multi
  @tagged[Prefix](whenUntagged = new Prefix)
  @tagged[NoBody](whenUntagged = new NoBody)
  @paramTag[RestParamTag](defaultTag = new Path)
  @unmatched(RawRest.NotValidPrefixMethod)
  @unmatchedParam[Body](RawRest.PrefixMethodBodyParam)
  @rpcMethodMetadata prefixMethods: List[PrefixMetadata[_]],

  @multi
  @tagged[GET]
  @tagged[NoBody](whenUntagged = new NoBody)
  @paramTag[RestParamTag](defaultTag = new Query)
  @unmatched(RawRest.NotValidGetMethod)
  @unmatchedParam[Body](RawRest.GetMethodBodyParam)
  @rpcMethodMetadata httpGetMethods: List[HttpMethodMetadata[_]],

  @multi
  @tagged[BodyMethodTag](whenUntagged = new POST)
  @tagged[SomeBodyTag](whenUntagged = new JsonBody)
  @paramTag[RestParamTag](defaultTag = new Body)
  @unmatched(RawRest.NotValidHttpMethod)
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
        headerParam <- method.parametersMetadata.headerParams
        if prefix.parametersMetadata.headerParamsMap.contains(headerParam.name.toLowerCase)
      } throw new InvalidRestApiException(
        s"Header parameter ${headerParam.name} of ${method.name} collides with header parameter of the same " +
          s"(case insensitive) name in prefix ${prefix.name}")

      for {
        prefix <- prefixes
        queryParam <- method.parametersMetadata.queryParams
        if prefix.parametersMetadata.queryParamsMap.contains(queryParam.name)
      } throw new InvalidRestApiException(
        s"Query parameter ${queryParam.name} of ${method.name} collides with query parameter of the same " +
          s"name in prefix ${prefix.name}")

      for {
        prefix <- prefixes
        cookieParam <- method.parametersMetadata.cookieParams
        if prefix.parametersMetadata.cookieParamsMap.contains(cookieParam.name)
      } throw new InvalidRestApiException(
        s"Cookie parameter ${cookieParam.name} of ${method.name} collides with cookie parameter of the same " +
          s"name in prefix ${prefix.name}")
    }

    prefixMethods.foreach { prefix =>
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

  def resolvePath(path: List[PlainValue]): List[ResolvedCall] =
    resolutionTrie.resolvePath(this, Nil, Nil, path).toList
}
object RestMetadata extends RpcMetadataCompanion[RestMetadata] {
  /**
   * Materializes [[RestMetadata]] for an arbitrary type rather than a trait.
   * Scans all public methods instead of just abstract methods.
   */
  def materializeForImpl[Real]: RestMetadata[Real] = macro RestMacros.materializeImplMetadata[Real]

  private class ResolutionTrie(methods: List[(List[PathPatternElement], RestMethodMetadata[_])]) {
    private val named: Map[PlainValue, ResolutionTrie] = methods.iterator
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
      root: RestMetadata[_], prefixCalls: List[PrefixCall], pathParams: List[PlainValue], path: List[PlainValue]
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
      case PathName(PlainValue(pathName)) :: tail =>
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
final case class PathName(value: PlainValue) extends PathPatternElement
final case class PathParam(param: PathParamMetadata[_]) extends PathPatternElement

sealed abstract class RestMethodMetadata[T] extends TypedMetadata[T] {
  def name: String
  def methodPath: List[PlainValue]
  def parametersMetadata: RestParametersMetadata
  def requestAdjusters: List[RequestAdjuster]
  def responseAdjusters: List[ResponseAdjuster]

  val pathPattern: List[PathPatternElement] = methodPath.map(PathName.apply) ++
    parametersMetadata.pathParams.flatMap(pp => PathParam(pp) :: pp.pathSuffix.map(PathName.apply))

  def applyPathParams(params: List[PlainValue]): List[PlainValue] = {
    def loop(params: List[PlainValue], pattern: List[PathPatternElement]): List[PlainValue] =
      (params, pattern) match {
        case (Nil, Nil) => Nil
        case (_, PathName(patternHead) :: patternTail) => patternHead :: loop(params, patternTail)
        case (param :: paramsTail, PathParam(_) :: patternTail) => param :: loop(paramsTail, patternTail)
        case _ => throw new IllegalArgumentException(
          s"got ${params.size} path params, expected ${parametersMetadata.pathParams.size}")
      }
    loop(params, pathPattern)
  }

  def extractPathParams(path: List[PlainValue]): Opt[(List[PlainValue], List[PlainValue])] = {
    def loop(path: List[PlainValue], pattern: List[PathPatternElement]): Opt[(List[PlainValue], List[PlainValue])] =
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

  def adjustRequest(request: RestRequest): RestRequest =
    requestAdjusters.foldRight(request)(_ adjustRequest _)

  def adjustResponse(asyncResponse: Task[RestResponse]): Task[RestResponse] =
    if (responseAdjusters.isEmpty) asyncResponse
    else asyncResponse.map(resp => responseAdjusters.foldRight(resp)(_ adjustResponse _))
}

final case class PrefixMetadata[T](
  @reifyName(useRawName = true) name: String,
  @reifyAnnot methodTag: Prefix,
  @composite parametersMetadata: RestParametersMetadata,
  @multi @reifyAnnot requestAdjusters: List[RequestAdjuster],
  @multi @reifyAnnot responseAdjusters: List[ResponseAdjuster],
  @infer @checked result: RestMetadata.Lazy[T]
) extends RestMethodMetadata[T] {
  def methodPath: List[PlainValue] = PlainValue.decodePath(methodTag.path)
}

final case class HttpMethodMetadata[T](
  @reifyName(useRawName = true) name: String,
  @reifyAnnot methodTag: HttpMethodTag,
  @reifyAnnot bodyTypeTag: BodyTypeTag,
  @composite parametersMetadata: RestParametersMetadata,
  @multi @tagged[Body] @rpcParamMetadata @allowOptional bodyParams: List[ParamMetadata[_]],
  @isAnnotated[FormBody] formBody: Boolean,
  @multi @reifyAnnot requestAdjusters: List[RequestAdjuster],
  @multi @reifyAnnot responseAdjusters: List[ResponseAdjuster],
  @infer @checked responseType: HttpResponseType[T]
) extends RestMethodMetadata[T] {
  val method: HttpMethod = methodTag.method

  val customBody: Boolean = bodyTypeTag match {
    case _: CustomBody => true
    case _ => false
  }

  def singleBodyParam: Opt[ParamMetadata[_]] =
    if (customBody) bodyParams.headOpt else Opt.Empty

  def methodPath: List[PlainValue] =
    PlainValue.decodePath(methodTag.path)
}

/**
 * Typeclass used during [[io.udash.rest.raw.RestMetadata RestMetadata]] materialization to determine whether a real method is a valid HTTP
 * method. Usually this means that the result must be a type wrapped into something that captures asynchronous
 * computation, e.g. `Future`. Because REST framework core tries to be agnostic about this
 * asynchronous wrapper (not everyone likes `Future`s), there are no default implicits provided for [[io.udash.rest.raw.HttpResponseType HttpResponseType]].
 * They must be provided externally.
 *
 * For example, [[io.udash.rest.FutureRestImplicits FutureRestImplicits]] introduces an instance of [[io.udash.rest.raw.HttpResponseType HttpResponseType]] for `Future[T]`,
 * for arbitrary type `T`. For [[io.udash.rest.raw.RestMetadata RestMetadata]] materialization this means that every method which returns a
 * `Future` is considered a valid HTTP method. [[io.udash.rest.FutureRestImplicits FutureRestImplicits]] is injected into materialization of
 * [[io.udash.rest.raw.RestMetadata RestMetadata]] through one of the base companion classes, e.g. [[io.udash.rest.DefaultRestApiCompanion DefaultRestApiCompanion]].
 * See `MacroInstances` for more information on injection of implicits.
 */
@implicitNotFound("${T} is not a valid result type of HTTP REST method")
final case class HttpResponseType[T]()
object HttpResponseType {
  implicit def asyncEffectResponseType[F[_] : TaskLike, T]: HttpResponseType[F[T]] =
    HttpResponseType()
}

final case class RestParametersMetadata(
  @multi @tagged[Path] @rpcParamMetadata pathParams: List[PathParamMetadata[_]],
  @multi @tagged[Header] @rpcParamMetadata @allowOptional headerParams: List[ParamMetadata[_]],
  @multi @tagged[Query] @rpcParamMetadata @allowOptional queryParams: List[ParamMetadata[_]],
  @multi @tagged[Cookie] @rpcParamMetadata @allowOptional cookieParams: List[ParamMetadata[_]]
) {
  lazy val headerParamsMap: Map[String, ParamMetadata[_]] =
    headerParams.toMapBy(_.name.toLowerCase)
  lazy val queryParamsMap: Map[String, ParamMetadata[_]] =
    queryParams.toMapBy(_.name)
  lazy val cookieParamsMap: Map[String, ParamMetadata[_]] =
    cookieParams.toMapBy(_.name)
}

final case class ParamMetadata[T](
  @reifyName(useRawName = true) name: String
) extends TypedMetadata[T]

final case class PathParamMetadata[T](
  @reifyName(useRawName = true) name: String,
  @reifyAnnot pathAnnot: Path
) extends TypedMetadata[T] {
  val pathSuffix: List[PlainValue] = PlainValue.decodePath(pathAnnot.pathSuffix)
}
