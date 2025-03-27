package io.udash
package rest
package raw

import com.avsystem.commons._
import com.avsystem.commons.meta._
import com.avsystem.commons.misc.ImplicitNotFound
import com.avsystem.commons.rpc._
import monix.eval.{Task, TaskLike}

import scala.annotation.{implicitNotFound, tailrec}

sealed abstract class RestMethodCall {
  val pathParams: List[PlainValue]
  val metadata: RestMethodMetadata[_]
  def rpcName: String = metadata.name
}
final case class PrefixCall(pathParams: List[PlainValue], metadata: PrefixMetadata[_]) extends RestMethodCall
final case class HttpCall(pathParams: List[PlainValue], metadata: HttpMethodMetadata[_]) extends RestMethodCall

final case class ResolvedCall(root: RestMetadata[_], prefixes: List[PrefixCall], finalCall: HttpCall) {
  lazy val pathPattern: List[PathPatternElement] =
    if (prefixes.isEmpty) finalCall.metadata.pathPattern
    else (prefixes.iterator.flatMap(_.metadata.pathPattern.iterator) ++
      finalCall.metadata.pathPattern.iterator).toList

  def method: HttpMethod = finalCall.metadata.method

  def rpcChainRepr: String =
    if (prefixes.isEmpty) finalCall.rpcName
    else prefixes.iterator.map(_.rpcName).mkString("", "->", s"->${finalCall.rpcName}")

  def adjustResponse(response: Task[RestResponse]): Task[RestResponse] =
    prefixes.foldRight(finalCall.metadata.adjustResponse(response))(_.metadata.adjustResponse(_))

  def adjustResponseWithStreaming(response: Task[AbstractRestResponse]): Task[AbstractRestResponse] =
    prefixes.foldRight(finalCall.metadata.adjustResponseWithStreaming(response))(_.metadata.adjustResponseWithStreaming(_))
}

@methodTag[RestMethodTag]
@methodTag[BodyTypeTag]
trait RawRest {

  import RawRest.*

  // declaration order of raw methods matters - it determines their priority!

  @multi @tried
  @tagged[Prefix](whenUntagged = new Prefix)
  @tagged[NoBody](whenUntagged = new NoBody)
  @paramTag[RestParamTag](defaultTag = new Path)
  @unmatched(RawRest.NotValidPrefixMethod)
  @unmatchedParam[Body](RawRest.PrefixMethodBodyParam)
  def prefix(
    @methodName name: String,
    @composite parameters: RestParameters,
  ): Try[RawRest]

  @multi @tried
  @tagged[GET]
  @tagged[NoBody](whenUntagged = new NoBody)
  @paramTag[RestParamTag](defaultTag = new Query)
  @unmatched(RawRest.NotValidGetMethod)
  @unmatchedParam[Body](RawRest.GetMethodBodyParam)
  def get(
    @methodName name: String,
    @composite parameters: RestParameters,
  ): Task[RestResponse]

  @multi @tried
  @tagged[GET]
  @tagged[NoBody](whenUntagged = new NoBody)
  @paramTag[RestParamTag](defaultTag = new Query)
  @unmatched(RawRest.NotValidGetStreamMethod)
  @unmatchedParam[Body](RawRest.GetMethodBodyParam)
  def getStream(
    @methodName name: String,
    @composite parameters: RestParameters,
  ): Task[StreamedRestResponse]

  @multi @tried
  @tagged[BodyMethodTag](whenUntagged = new POST)
  @tagged[FormBody]
  @paramTag[RestParamTag](defaultTag = new Body)
  @unmatched(RawRest.NotValidFormBodyMethod)
  def handleForm(
    @methodName name: String,
    @composite parameters: RestParameters,
    @multi @tagged[Body] body: Mapping[PlainValue],
  ): Task[RestResponse]

  @multi @tried
  @tagged[BodyMethodTag](whenUntagged = new POST)
  @tagged[JsonBody](whenUntagged = new JsonBody)
  @paramTag[RestParamTag](defaultTag = new Body)
  @unmatched(RawRest.NotValidHttpMethod)
  def handleJson(
    @methodName name: String,
    @composite parameters: RestParameters,
    @multi @tagged[Body] body: Mapping[JsonValue],
  ): Task[RestResponse]

  @multi @tried
  @tagged[BodyMethodTag](whenUntagged = new POST)
  @tagged[JsonBody](whenUntagged = new JsonBody)
  @paramTag[RestParamTag](defaultTag = new Body)
  @unmatched(RawRest.NotValidHttpMethodStream)
  def handleJsonStream(
    @methodName name: String,
    @composite parameters: RestParameters,
    @multi @tagged[Body] body: Mapping[JsonValue],
  ): Task[StreamedRestResponse]

  @multi @tried
  @tagged[BodyMethodTag](whenUntagged = new POST)
  @tagged[CustomBody]
  @paramTag[RestParamTag](defaultTag = new Body)
  @unmatched(RawRest.NotValidCustomBodyMethod)
  @unmatchedParam[Body](RawRest.SuperfluousBodyParam)
  def handleCustom(
    @methodName name: String,
    @composite parameters: RestParameters,
    @encoded @tagged[Body] @unmatched(RawRest.MissingBodyParam) body: HttpBody,
  ): Task[RestResponse]

  @multi @tried
  @tagged[BodyMethodTag](whenUntagged = new POST)
  @tagged[CustomBody]
  @paramTag[RestParamTag](defaultTag = new Body)
  @unmatched(RawRest.NotValidCustomBodyStreamMethod)
  @unmatchedParam[Body](RawRest.SuperfluousBodyParam)
  def handleCustomStream(
    @methodName name: String,
    @composite parameters: RestParameters,
    @encoded @tagged[Body] @unmatched(RawRest.MissingBodyParam) body: HttpBody,
  ): Task[StreamedRestResponse]

  def asHandleRequest(metadata: RestMetadata[_]): HandleRequest =
    RawRest.resolveAndHandle(metadata)(handleResolved).andThen(StreamedRestResponse.fallbackToRestResponse)

  def asHandleRequestWithStreaming(metadata: RestMetadata[_]): HandleRequestWithStreaming =
    RawRest.resolveAndHandle(metadata)(handleResolvedWithStreaming)

  // TODO doc for compatibility
  def handleResolved(request: RestRequest, resolved: ResolvedCall): Task[RestResponse] =
    StreamedRestResponse.fallbackToRestResponse(handleResolvedWithStreaming(request, resolved))

  def handleResolvedWithStreaming(request: RestRequest, resolved: ResolvedCall): Task[AbstractRestResponse] = {
    val RestRequest(method, parameters, body) = request
    val ResolvedCall(_, prefixes, finalCall) = resolved
    val HttpCall(finalPathParams, finalMetadata) = finalCall

    def handleBadBody[T](expr: => T): T = try expr catch {
      case NonFatal(cause) => throw new InvalidRpcCall(s"Invalid HTTP body: ${cause.getMessage}", cause)
    }

    @tailrec
    def resolveCall(rawRest: RawRest, prefixes: List[PrefixCall]): Task[AbstractRestResponse] = prefixes match {
      case PrefixCall(pathParams, pm) :: tail =>
        rawRest.prefix(pm.name, parameters.copy(path = pathParams)) match {
          case Success(nextRawRest) => resolveCall(nextRawRest, tail)
          case Failure(e: HttpErrorException) => Task.now(e.toResponse)
          case Failure(cause) => Task.raiseError(cause)
        }
      case Nil =>
        val finalParameters = parameters.copy(path = finalPathParams)
        if (method == HttpMethod.GET) {
          if (!finalMetadata.streamedResponse) rawRest.get(finalMetadata.name, finalParameters)
          else rawRest.getStream(finalMetadata.name, finalParameters)
        } else if (finalMetadata.customBody) {
          if (!finalMetadata.streamedResponse) rawRest.handleCustom(finalMetadata.name, finalParameters, body)
          else rawRest.handleCustomStream(finalMetadata.name, finalParameters, body)
        } else if (finalMetadata.formBody) {
          rawRest.handleForm(finalMetadata.name, finalParameters, handleBadBody(HttpBody.parseFormBody(body)))
        } else {
          if (!finalMetadata.streamedResponse)
            rawRest.handleJson(finalMetadata.name, finalParameters, handleBadBody(HttpBody.parseJsonBody(body)))
          else
            rawRest.handleJsonStream(finalMetadata.name, finalParameters, handleBadBody(HttpBody.parseJsonBody(body)))
        }
    }
    try resolved.adjustResponseWithStreaming(resolveCall(this, prefixes)) catch {
      case e: InvalidRpcCall =>
        Task.now(extractHttpException(e).map(_.toResponse).getOrElse(RestResponse.plain(400, e.getMessage)))
    }
  }

  @tailrec private def extractHttpException(e: Throwable): Opt[HttpErrorException] = e match {
    case null => Opt.Empty
    case e: HttpErrorException => Opt(e)
    case _ => extractHttpException(e.getCause)
  }
}

object RawRest extends RawRpcCompanion[RawRest] {
  type HandleRequest = RestRequest => Task[RestResponse]
  type HandleRequestWithStreaming = RestRequest => Task[AbstractRestResponse]

  trait RestRequestHandler {
    def handleRequest(request: RestRequest): Task[RestResponse]
    def handleRequestStream(request: RestRequest): Task[StreamedRestResponse]
  }

  /**
   * Similar to [[io.udash.rest.raw.RawRest.HandleRequest HandleRequest]] but accepts already resolved path as a second argument.
   */
  type HandleResolvedRequest = (RestRequest, ResolvedCall) => Task[RestResponse]
  type HandleResolvedRequestWithStreaming = (RestRequest, ResolvedCall) => Task[AbstractRestResponse]

  type AsTask[F[_]] = TaskLike[F]
  trait FromTask[F[_]] {
    def fromTask[A](task: Task[A]): F[A]
  }

  implicit val taskFromTask: FromTask[Task] =
    new FromTask[Task] {
      override def fromTask[A](task: Task[A]): Task[A] = task
    }

  final val NotValidPrefixMethod =
    "it cannot be translated into a prefix method"
  final val PrefixMethodBodyParam =
    "prefix methods cannot take @Body parameters"
  final val NotValidGetMethod =
    "it cannot be translated into an HTTP GET method"
  final val NotValidGetStreamMethod =
    "it cannot be translated into an HTTP GET stream method"
  final val GetMethodBodyParam =
    "GET methods cannot take @Body parameters"
  final val NotValidHttpMethod =
    "it cannot be translated into an HTTP method"
  final val NotValidHttpMethodStream =
    "it cannot be translated into an HTTP stream method"
  final val NotValidFormBodyMethod =
    "it cannot be translated into an HTTP method with form body"
  final val NotValidCustomBodyMethod =
    "it cannot be translated into an HTTP method with custom body"
  final val NotValidCustomBodyStreamMethod =
    "it cannot be translated into an HTTP stream method with custom body"
  final val MissingBodyParam =
    "expected exactly one @Body parameter but none was found"
  final val SuperfluousBodyParam =
    "expected exactly one @Body parameter but more than one was found"
  final val InvalidTraitMessage =
    "result type ${T} is not a valid REST API trait, does it have a properly defined companion object?"

  @implicitNotFound(InvalidTraitMessage)
  implicit def rawRestAsRealNotFound[T]: ImplicitNotFound[AsReal[RawRest, T]] = ImplicitNotFound()

  @implicitNotFound(InvalidTraitMessage)
  implicit def rawRestAsRawNotFound[T]: ImplicitNotFound[AsRaw[RawRest, T]] = ImplicitNotFound()

  // client side
  def fromHandleRequest[Real: AsRealRpc : RestMetadata](handle: HandleRequest): Real =
    RawRest.asReal(new DefaultRawRest(Nil, RestMetadata[Real], RestParameters.Empty, new RawRest.RestRequestHandler {
      override def handleRequest(request: RestRequest): Task[RestResponse] = handle(request)
      override def handleRequestStream(request: RestRequest): Task[StreamedRestResponse] =
        Task.raiseError(new UnsupportedOperationException("Streaming unsupported by the client"))
    }))

  // client side with response streaming support
  def fromHandleRequestWithStreaming[Real: AsRealRpc : RestMetadata](handleRequest: RawRest.RestRequestHandler): Real =
    RawRest.asReal(new DefaultRawRest(Nil, RestMetadata[Real], RestParameters.Empty, handleRequest))

  // server side
  def asHandleRequest[Real: AsRawRpc : RestMetadata](real: Real): HandleRequest =
    RawRest.asRaw(real).asHandleRequest(RestMetadata[Real])

  // server side with response streaming support
  def asHandleRequestWithStreaming[Real: AsRawRpc : RestMetadata](real: Real): HandleRequestWithStreaming =
     RawRest.asRaw(real).asHandleRequestWithStreaming(RestMetadata[Real])

  def resolveAndHandle(metadata: RestMetadata[_])(handleResolved: HandleResolvedRequestWithStreaming): HandleRequestWithStreaming = {
    metadata.ensureValid()

    request => {
      val path = request.parameters.path
      metadata.resolvePath(path) match {
        case Nil =>
          val message = s"path ${PlainValue.encodePath(path)} not found"
          Task.now(RestResponse.plain(404, message))
        case calls => request.method match {
          case HttpMethod.OPTIONS =>
            val meths = calls.iterator.map(_.method).flatMap {
              case HttpMethod.GET => List(HttpMethod.GET, HttpMethod.HEAD)
              case m => List(m)
            } ++ Iterator(HttpMethod.OPTIONS)
            Task.now(RestResponse(200, IMapping.create("Allow" -> PlainValue(meths.mkString(","))), HttpBody.Empty))
          case wireMethod =>
            val head = wireMethod == HttpMethod.HEAD
            val req = if (head) request.copy(method = HttpMethod.GET) else request
            calls.find(_.method == req.method) match {
              case Some(call) =>
                val resp = handleResolved(req, call)
                if (head)
                  resp.map {
                    case resp: RestResponse => resp.copy(body = HttpBody.empty)
                    case stream: StreamedRestResponse => stream.copy(body = StreamedBody.empty)
                  }
                else
                  resp
              case None =>
                val message = s"$wireMethod not allowed on path ${PlainValue.encodePath(path)}"
                Task.now(RestResponse.plain(405, message))
            }
        }
      }
    }
  }

  private final class DefaultRawRest(
    prefixMetas: List[PrefixMetadata[_]], //in reverse invocation order!
    metadata: RestMetadata[_],
    prefixParams: RestParameters,
    handleRequest: RawRest.RestRequestHandler,
  ) extends RawRest {

    def prefix(name: String, parameters: RestParameters): Try[RawRest] =
      metadata.prefixesByName.get(name).map { prefixMeta =>
        val newHeaders = prefixParams.append(prefixMeta, parameters)
        Success(new DefaultRawRest(prefixMeta :: prefixMetas, prefixMeta.result.value, newHeaders, handleRequest))
      } getOrElse Failure(new UnknownRpc(name, "prefix"))

    def get(name: String, parameters: RestParameters): Task[RestResponse] =
      doHandle("get", name, parameters, HttpBody.Empty)

    def getStream(name: String, parameters: RestParameters): Task[StreamedRestResponse] =
      doHandleStream("getStream", name, parameters, HttpBody.Empty)

    def handleJson(name: String, parameters: RestParameters, body: Mapping[JsonValue]): Task[RestResponse] =
      doHandle("handle", name, parameters, HttpBody.createJsonBody(body))

    def handleJsonStream(name: String, parameters: RestParameters, body: Mapping[JsonValue]): Task[StreamedRestResponse] =
      doHandleStream("handleStream", name, parameters, HttpBody.Empty)

    def handleForm(name: String, parameters: RestParameters, body: Mapping[PlainValue]): Task[RestResponse] =
      doHandle("handleForm", name, parameters, HttpBody.createFormBody(body))

    def handleCustom(name: String, parameters: RestParameters, body: HttpBody): Task[RestResponse] =
      doHandle("handleSingle", name, parameters, body)

    def handleCustomStream(name: String, parameters: RestParameters, body: HttpBody): Task[StreamedRestResponse] =
      doHandleStream("handleSingleStream", name, parameters, body)

    private def doHandle(rawName: String, name: String, parameters: RestParameters, body: HttpBody): Task[RestResponse] =
      metadata.httpMethodsByName.getOpt(name)
        .collect { case methodMeta if !methodMeta.streamedResponse =>
          handleRequest.handleRequest(resolveRequest(parameters, body, methodMeta))
        }
        .getOrElse(Task.raiseError(new UnknownRpc(name, rawName)))

    private def doHandleStream(
      rawName: String,
      name: String,
      parameters: RestParameters,
      body: HttpBody,
    ): Task[StreamedRestResponse] =
      metadata.httpMethodsByName.getOpt(name)
        .collect { case methodMeta if methodMeta.streamedResponse =>
          handleRequest.handleRequestStream(resolveRequest(parameters, body, methodMeta))
        }
        .getOrElse(Task.raiseError(new UnknownRpc(name, rawName)))

    private def resolveRequest(
      parameters: RestParameters,
      body: HttpBody,
      methodMeta: HttpMethodMetadata[_],
    ): RestRequest = {
      val newHeaders = prefixParams.append(methodMeta, parameters)
      val baseRequest = RestRequest(methodMeta.method, newHeaders, body)
      val request = prefixMetas.foldLeft(methodMeta.adjustRequest(baseRequest))((req, meta) => meta.adjustRequest(req))
      request
    }
  }
}
