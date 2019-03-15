package io.udash
package rest
package raw

import java.util.concurrent.atomic.AtomicBoolean

import com.avsystem.commons._
import com.avsystem.commons.meta._
import com.avsystem.commons.misc.ImplicitNotFound
import com.avsystem.commons.rpc._

import scala.annotation.implicitNotFound

sealed abstract class RestMethodCall {
  val pathParams: List[PlainValue]
  val metadata: RestMethodMetadata[_]
  def rpcName: String = metadata.name
}
case class PrefixCall(pathParams: List[PlainValue], metadata: PrefixMetadata[_]) extends RestMethodCall
case class HttpCall(pathParams: List[PlainValue], metadata: HttpMethodMetadata[_]) extends RestMethodCall

case class ResolvedCall(root: RestMetadata[_], prefixes: List[PrefixCall], finalCall: HttpCall) {
  lazy val pathPattern: List[PathPatternElement] =
    if (prefixes.isEmpty) finalCall.metadata.pathPattern
    else (prefixes.iterator.flatMap(_.metadata.pathPattern.iterator) ++
      finalCall.metadata.pathPattern.iterator).toList

  def method: HttpMethod = finalCall.metadata.method

  def rpcChainRepr: String =
    prefixes.iterator.map(_.rpcName).mkString("", "->", s"->${finalCall.rpcName}")

  def adjustResponse(response: RawRest.Async[RestResponse]): RawRest.Async[RestResponse] =
    prefixes.foldRight(finalCall.metadata.adjustResponse(response))(_.metadata.adjustResponse(_))
}

@methodTag[RestMethodTag]
@methodTag[BodyTypeTag]
trait RawRest {

  import RawRest._

  // declaration order of raw methods matters - it determines their priority!

  @multi @tried
  @tagged[Prefix](whenUntagged = new Prefix)
  @tagged[NoBody](whenUntagged = new NoBody)
  @paramTag[RestParamTag](defaultTag = new Path)
  @unmatched(RawRest.NotValidPrefixMethod)
  @unmatchedParam[Body](RawRest.PrefixMethodBodyParam)
  def prefix(
    @methodName name: String,
    @composite parameters: RestParameters
  ): Try[RawRest]

  @multi @tried
  @tagged[GET]
  @tagged[NoBody](whenUntagged = new NoBody)
  @paramTag[RestParamTag](defaultTag = new Query)
  @unmatched(RawRest.NotValidGetMethod)
  @unmatchedParam[Body](RawRest.GetMethodBodyParam)
  def get(
    @methodName name: String,
    @composite parameters: RestParameters
  ): Async[RestResponse]

  @multi @tried
  @tagged[BodyMethodTag](whenUntagged = new POST)
  @tagged[FormBody]
  @paramTag[RestParamTag](defaultTag = new Body)
  @unmatched(RawRest.NotValidFormBodyMethod)
  def handleForm(
    @methodName name: String,
    @composite parameters: RestParameters,
    @multi @tagged[Body] body: Mapping[PlainValue]
  ): Async[RestResponse]

  @multi @tried
  @tagged[BodyMethodTag](whenUntagged = new POST)
  @tagged[JsonBody](whenUntagged = new JsonBody)
  @paramTag[RestParamTag](defaultTag = new Body)
  @unmatched(RawRest.NotValidHttpMethod)
  def handleJson(
    @methodName name: String,
    @composite parameters: RestParameters,
    @multi @tagged[Body] body: Mapping[JsonValue]
  ): Async[RestResponse]

  @multi @tried
  @tagged[BodyMethodTag](whenUntagged = new POST)
  @tagged[CustomBody]
  @paramTag[RestParamTag](defaultTag = new Body)
  @unmatched(RawRest.NotValidCustomBodyMethod)
  @unmatchedParam[Body](RawRest.SuperfluousBodyParam)
  def handleCustom(
    @methodName name: String,
    @composite parameters: RestParameters,
    @encoded @tagged[Body] @unmatched(RawRest.MissingBodyParam) body: HttpBody
  ): Async[RestResponse]

  def asHandleRequest(metadata: RestMetadata[_]): HandleRequest =
    RawRest.resolveAndHandle(metadata)(handleResolved)

  def handleResolved(request: RestRequest, resolved: ResolvedCall): Async[RestResponse] = {
    val RestRequest(method, parameters, body) = request
    val ResolvedCall(_, prefixes, finalCall) = resolved
    val HttpCall(finalPathParams, finalMetadata) = finalCall

    def handleBadBody[T](expr: => T): T = try expr catch {
      case NonFatal(cause) => throw new InvalidRpcCall(s"Invalid HTTP body: ${cause.getMessage}", cause)
    }

    def resolveCall(rawRest: RawRest, prefixes: List[PrefixCall]): Async[RestResponse] = prefixes match {
      case PrefixCall(pathParams, pm) :: tail =>
        rawRest.prefix(pm.name, parameters.copy(path = pathParams)) match {
          case Success(nextRawRest) => resolveCall(nextRawRest, tail)
          case Failure(e: HttpErrorException) => RawRest.successfulAsync(e.toResponse)
          case Failure(cause) => RawRest.failingAsync(cause)
        }
      case Nil =>
        val finalParameters = parameters.copy(path = finalPathParams)
        if (method == HttpMethod.GET)
          rawRest.get(finalMetadata.name, finalParameters)
        else if (finalMetadata.customBody)
          rawRest.handleCustom(finalMetadata.name, finalParameters, body)
        else if (finalMetadata.formBody)
          rawRest.handleForm(finalMetadata.name, finalParameters, handleBadBody(HttpBody.parseFormBody(body)))
        else
          rawRest.handleJson(finalMetadata.name, finalParameters, handleBadBody(HttpBody.parseJsonBody(body)))
    }
    try resolved.adjustResponse(resolveCall(this, prefixes)) catch {
      case e: InvalidRpcCall =>
        RawRest.successfulAsync(RestResponse.plain(400, e.getMessage))
    }
  }
}

object RawRest extends RawRpcCompanion[RawRest] {
  /**
    * A callback that gets notified when value of type `T` gets computed or when computation of that value fails.
    * Callbacks should never throw exceptions. Preferably, they should be simple notifiers that delegate the real
    * work somewhere else, e.g. schedule some handling code on a separate executor
    * (e.g. [[scala.concurrent.ExecutionContext ExecutionContext]]).
    */
  type Callback[T] = Try[T] => Unit

  /**
    * The most low-level, raw type representing an asynchronous, possibly side-effecting operation that yields a
    * value of type `T` as a result.
    * `Async` is a consumer of a callback. When a callback is passed to `Async`, it should start the operation
    * and ultimately notify the callback about the result. Each time the callback is passed, the
    * entire operation should be repeated, involving all possible side effects. Operation should never be started
    * without the callback being passed (i.e. there should be no observable side effects before a callback is passed).
    * Implementation of `Async` should also be prepared to accept a callback before the previous one was notified
    * about the result (i.e. it should support concurrent execution).
    */
  type Async[T] = Callback[T] => Unit

  /**
    * Raw type of an operation that executes a [[RestRequest]]. The operation should be run every time the
    * resulting `Async` value is passed a callback. It should not be run before that. Each run may involve side
    * effects, network communication, etc. Runs may be concurrent.
    * Request handlers should never throw exceptions but rather convert them into failing implementation of
    * `Async`. One way to do this is by wrapping the handler with [[io.udash.rest.raw.RawRest.safeHandle safeHandle]].
    */
  type HandleRequest = RestRequest => Async[RestResponse]

  /**
    * Similar to [[io.udash.rest.raw.RawRest.HandleRequest HandleRequest]] but accepts already resolved path as a second argument.
    */
  type HandleResolvedRequest = (RestRequest, ResolvedCall) => Async[RestResponse]

  /**
    * Ensures that all possible exceptions thrown by a request handler are not propagated but converted into
    * an instance of `Async` that notifies its callbacks about the failure.
    */
  def safeHandle(handleRequest: HandleRequest): HandleRequest =
    request => safeAsync(handleRequest(request))

  private def guardedAsync[T](async: Async[T]): Async[T] = callback => {
    val called = new AtomicBoolean
    val guardedCallback: Callback[T] = result =>
      if (!called.getAndSet(true)) {
        callback(result) // may possibly throw but better let it fly rather than catch and ignore
      }
    try async(guardedCallback) catch {
      case NonFatal(t) =>
        // if callback was already called then we can't do much with the failure, rethrow it
        if (!called.getAndSet(true)) callback(Failure(t)) else throw t
    }
  }

  def safeAsync[T](async: => Async[T]): Async[T] =
    try guardedAsync(async) catch {
      case NonFatal(t) => failingAsync(t)
    }

  def readyAsync[T](result: Try[T]): Async[T] =
    callback => callback(result)

  def successfulAsync[T](value: T): Async[T] =
    readyAsync(Success(value))

  def failingAsync[T](cause: Throwable): Async[T] =
    readyAsync(Failure(cause))

  def transformAsync[A, B](async: Async[A])(f: Try[A] => Try[B]): Async[B] =
    cb => async(contraTransformCallback(cb)(f))

  def mapAsync[A, B](async: Async[A])(f: A => B): Async[B] =
    transformAsync(async)(_.map(f))

  def contraTransformCallback[A, B](callback: Callback[B])(f: Try[A] => Try[B]): Callback[A] =
    ta => callback(try f(ta) catch {
      case NonFatal(cause) => Failure(cause)
    })

  def contramapCallback[A, B](callback: Callback[B])(f: A => B): Callback[A] =
    contraTransformCallback(callback)(_.map(f))

  /**
    * Typeclass which captures the fact that some effect type constructor represents asynchronous computation and
    * can be converted to [[RawRest.Async]].
    */
  @implicitNotFound("${F} is not a valid asynchronous effect, AsyncEffect instance is missing")
  trait AsyncEffect[F[_]] {
    def toAsync[A](fa: F[A]): Async[A]
    def fromAsync[A](async: Async[A]): F[A]
  }

  final val NotValidPrefixMethod =
    "it cannot be translated into a prefix method"
  final val PrefixMethodBodyParam =
    "prefix methods cannot take @Body parameters"
  final val NotValidGetMethod =
    "it cannot be translated into a HTTP GET method"
  final val GetMethodBodyParam =
    "GET methods cannot take @Body parameters"
  final val NotValidHttpMethod =
    "it cannot be translated into a HTTP method"
  final val NotValidFormBodyMethod =
    "it cannot be translated into a HTTP method with form body"
  final val NotValidCustomBodyMethod =
    "it cannot be translated into a HTTP method with custom body"
  final val MissingBodyParam =
    "expected exactly one @Body parameter but more than one was found"
  final val SuperfluousBodyParam =
    "expected exactly one @Body parameter but none was found"
  final val InvalidTraitMessage =
    "result type ${T} is not a valid REST API trait, does it have a properly defined companion object?"

  @implicitNotFound(InvalidTraitMessage)
  implicit def rawRestAsRealNotFound[T]: ImplicitNotFound[AsReal[RawRest, T]] = ImplicitNotFound()

  @implicitNotFound(InvalidTraitMessage)
  implicit def rawRestAsRawNotFound[T]: ImplicitNotFound[AsRaw[RawRest, T]] = ImplicitNotFound()

  def fromHandleRequest[Real: AsRealRpc : RestMetadata](handleRequest: HandleRequest): Real =
    RawRest.asReal(new DefaultRawRest(Nil, RestMetadata[Real], RestParameters.Empty, handleRequest))

  def asHandleRequest[Real: AsRawRpc : RestMetadata](real: Real): HandleRequest =
    RawRest.asRaw(real).asHandleRequest(RestMetadata[Real])

  def resolveAndHandle(metadata: RestMetadata[_])(handleResolved: HandleResolvedRequest): HandleRequest = {
    metadata.ensureValid()

    RawRest.safeHandle { request =>
      val path = request.parameters.path
      metadata.resolvePath(path) match {
        case Nil =>
          val message = s"path ${PlainValue.encodePath(path)} not found"
          RawRest.successfulAsync(RestResponse.plain(404, message))
        case calls => request.method match {
          case HttpMethod.OPTIONS =>
            val meths = calls.iterator.map(_.method).flatMap {
              case HttpMethod.GET => List(HttpMethod.GET, HttpMethod.HEAD)
              case m => List(m)
            } ++ Iterator(HttpMethod.OPTIONS)
            val response = RestResponse(200, IMapping("Allow" -> PlainValue(meths.mkString(","))), HttpBody.Empty)
            RawRest.successfulAsync(response)
          case wireMethod =>
            val head = wireMethod == HttpMethod.HEAD
            val req = if (head) request.copy(method = HttpMethod.GET) else request
            calls.find(_.method == req.method) match {
              case Some(call) =>
                val resp = handleResolved(req, call)
                if (head) RawRest.mapAsync(resp)(_.copy(body = HttpBody.empty)) else resp
              case None =>
                val message = s"$wireMethod not allowed on path ${PlainValue.encodePath(path)}"
                RawRest.successfulAsync(RestResponse.plain(405, message))
            }
        }
      }
    }
  }

  private final class DefaultRawRest(
    prefixMetas: List[PrefixMetadata[_]],
    metadata: RestMetadata[_],
    prefixParams: RestParameters,
    handleRequest: HandleRequest
  ) extends RawRest {

    def prefix(name: String, parameters: RestParameters): Try[RawRest] =
      metadata.prefixesByName.get(name).map { prefixMeta =>
        val newHeaders = prefixParams.append(prefixMeta, parameters)
        Success(new DefaultRawRest(prefixMeta :: prefixMetas, prefixMeta.result.value, newHeaders, handleRequest))
      } getOrElse Failure(new UnknownRpc(name, "prefix"))

    def get(name: String, parameters: RestParameters): Async[RestResponse] =
      doHandle("get", name, parameters, HttpBody.Empty)

    def handleJson(name: String, parameters: RestParameters, body: Mapping[JsonValue]): Async[RestResponse] =
      doHandle("handle", name, parameters, HttpBody.createJsonBody(body))

    def handleForm(name: String, parameters: RestParameters, body: Mapping[PlainValue]): Async[RestResponse] =
      doHandle("handleForm", name, parameters, HttpBody.createFormBody(body))

    def handleCustom(name: String, parameters: RestParameters, body: HttpBody): Async[RestResponse] =
      doHandle("handleSingle", name, parameters, body)

    private def doHandle(rawName: String, name: String, parameters: RestParameters, body: HttpBody): Async[RestResponse] =
      metadata.httpMethodsByName.get(name).map { methodMeta =>
        val newHeaders = prefixParams.append(methodMeta, parameters)
        val baseRequest = RestRequest(methodMeta.method, newHeaders, body)
        val request = prefixMetas.foldLeft(methodMeta.adjustRequest(baseRequest))((req, meta) => meta.adjustRequest(req))
        handleRequest(request)
      } getOrElse RawRest.failingAsync(new UnknownRpc(name, rawName))
  }
}
