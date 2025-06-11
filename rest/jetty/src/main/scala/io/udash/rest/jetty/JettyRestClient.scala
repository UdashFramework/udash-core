package io.udash
package rest.jetty

import com.avsystem.commons.*
import com.avsystem.commons.annotation.explicitGenerics
import com.avsystem.commons.serialization.json.{JsonReader, JsonStringInput}
import com.typesafe.scalalogging.LazyLogging
import io.udash.rest.raw.*
import io.udash.rest.util.Utils
import io.udash.utils.URLEncoder
import monix.eval.Task
import monix.execution.{Ack, Callback, Scheduler}
import monix.reactive.Observable
import monix.reactive.OverflowStrategy.Unbounded
import monix.reactive.subjects.{ConcurrentSubject, PublishToOneSubject}
import org.eclipse.jetty.client.*
import org.eclipse.jetty.http.{HttpCookie, HttpHeader, MimeTypes}
import org.eclipse.jetty.io.Content

import java.nio.charset.Charset
import scala.concurrent.CancellationException
import scala.concurrent.duration.*

/**
 * A REST client implementation based on the Eclipse Jetty HTTP client library.
 * Supports both standard request/response interactions and handling of streamed responses.
 *
 * Streaming responses allow processing large amounts of data without buffering the entire
 * response body in memory. This client activates streaming mode *only* when the server's
 * response headers *do not* include a `Content-Length`.
 *
 * @param client                   The configured Jetty `HttpClient` instance.
 * @param defaultMaxResponseLength Default maximum size (in bytes) for buffering non-streamed responses.
 * @param defaultTimeout           Default timeout for requests.
 */
final class JettyRestClient(
  client: HttpClient,
  defaultMaxResponseLength: Int = JettyRestClient.DefaultMaxResponseLength,
  defaultTimeout: Duration = JettyRestClient.DefaultTimeout,
) extends LazyLogging {

  @explicitGenerics
  def create[RestApi: RawRest.AsRealRpc : RestMetadata](
    baseUri: String,
    customMaxResponseLength: OptArg[Int] = OptArg.Empty,
    customTimeout: OptArg[Duration] = OptArg.Empty,
  ): RestApi =
    RawRest.fromHandleRequestWithStreaming[RestApi](
      asHandleRequestWithStreaming(baseUri, customMaxResponseLength, customTimeout)
    )

  /**
   * Creates a request handler with streaming support that can be used to make REST calls.
   * The handler supports both regular responses and streaming responses, allowing for
   * incremental processing of large payloads through Observable streams.
   *
   * @param baseUrl                 Base URL for the REST service
   * @param customMaxResponseLength Optional maximum response length override for non-streamed responses
   * @param customTimeout           Optional timeout override
   * @return A handler that can process REST requests with streaming capabilities
   */
  def asHandleRequestWithStreaming(
    baseUrl: String,
    customMaxResponseLength: OptArg[Int] = OptArg.Empty,
    customTimeout: OptArg[Duration] = OptArg.Empty,
  ): RawRest.RestRequestHandler = new RawRest.RestRequestHandler {
    private val timeout = customTimeout.getOrElse(defaultTimeout)
    private val maxResponseLength = customMaxResponseLength.getOrElse(defaultMaxResponseLength)

    override def handleRequest(request: RestRequest): Task[RestResponse] =
      prepareRequest(baseUrl, timeout, request).flatMap(sendRequest(_, maxResponseLength))

    override def handleRequestStream(request: RestRequest): Task[StreamedRestResponse] =
      prepareRequest(baseUrl, timeout, request).flatMap { httpReq =>
        def cancelRequest: Task[Unit] =
          Task(httpReq.abort(new CancellationException("Request cancelled")).discard)

        Task.cancelable0 { (scheduler: Scheduler, callback: Callback[Throwable, StreamedRestResponse]) =>
          val listener = new BufferingResponseListener(maxResponseLength) {
            private var collectToBuffer: Boolean = true
            private lazy val publishSubject = PublishToOneSubject[Array[Byte]]()
            private lazy val rawContentSubject = ConcurrentSubject.from(publishSubject, Unbounded)(scheduler)

            override def onHeaders(response: Response): Unit = {
              super.onHeaders(response)
              // When Content-Length is not provided (-1), process the response as a stream
              // since we can't determine the full size in advance. This enables handling
              // chunked transfer encoding and streaming responses.
              val contentLength = response.getHeaders.getLongField(HttpHeader.CONTENT_LENGTH)
              if (contentLength == -1) {
                val contentTypeOpt = response.getHeaders.get(HttpHeader.CONTENT_TYPE).opt
                val mediaTypeOpt = contentTypeOpt.map(MimeTypes.getContentTypeWithoutCharset)
                val charsetOpt = contentTypeOpt.map(MimeTypes.getCharsetFromContentType)
                val bodyOpt = (mediaTypeOpt, charsetOpt) matchOpt {
                  case (Opt(HttpBody.JsonType), Opt(charset)) =>
                    // suboptimal - maybe "online" parsing is possible using Jackson / other lib without waiting for full content ?
                    StreamedBody.JsonList(
                      elements = Observable
                        .fromTask(Utils.mergeArrays(rawContentSubject))
                        .map(raw => new String(raw, charset))
                        .flatMap { jsonStr =>
                          val input = new JsonStringInput(new JsonReader(jsonStr))
                          Observable
                            .fromIterator(Task.eval(input.readList().iterator(_.asInstanceOf[JsonStringInput].readRawJson())))
                            .map(JsonValue(_))
                        }
                        .doOnSubscriptionCancel(cancelRequest)
                        .onErrorFallbackTo(Observable.raiseError(JettyRestClient.Streaming)),
                      charset = charset,
                    )
                  case (Opt(mediaType), _) =>
                    StreamedBody.binary(
                      content = rawContentSubject.doOnSubscriptionCancel(cancelRequest),
                      contentType = contentTypeOpt.getOrElse(mediaType),
                    )
                }
                bodyOpt.mapOr(
                  {
                    callback(Failure(JettyRestClient.unsupportedContentTypeError(contentTypeOpt)))
                  },
                  body => {
                    this.collectToBuffer = false
                    val restResponse = StreamedRestResponse(
                      code = response.getStatus,
                      headers = parseHeaders(response),
                      body = body,
                    )
                    callback(Success(restResponse))
                  }
                )
              }
            }

            override def onContent(response: Response, chunk: Content.Chunk, demander: Runnable): Unit =
              if (collectToBuffer)
                super.onContent(response, chunk, demander)
              else if (chunk == Content.Chunk.EOF) {
                rawContentSubject.onComplete()
              } else {
                val buf = chunk.getByteBuffer
                val arr = new Array[Byte](buf.remaining)
                buf.get(arr)
                publishSubject.subscription // wait for subscription
                  .flatMapNow(_ => rawContentSubject.onNext(arr))
                  .mapNow {
                    case Ack.Continue => demander.run()
                    case Ack.Stop     => ()
                  }
                  .onCompleteNow {
                    case Failure(ex) =>
                      logger.error("Unexpected error while processing streamed response chunk", ex)
                    case Success(_) =>
                  }
              }

            override def onComplete(result: Result): Unit =
              if (result.isSucceeded) {
                val httpResp = result.getResponse
                val contentLength = httpResp.getHeaders.getLongField(HttpHeader.CONTENT_LENGTH)
                if (contentLength != -1) {
                  // For responses with known content length, we handle them as regular (non-streamed) responses
                  // Any errors will be propagated through the callback's Failure channel
                  val restResponse = StreamedRestResponse(
                    code = httpResp.getStatus,
                    headers = parseHeaders(httpResp),
                    body = StreamedBody.fromHttpBody(parseHttpBody(httpResp, this)),
                  )
                  callback(Success(restResponse))
                } else {
                  rawContentSubject.onComplete()
                }
              } else {
                callback(Failure(result.getFailure))
              }
          }
          httpReq.send(listener)

          cancelRequest // see cats.effect#CancelToken
        }
      }
  }

  /**
   * Creates a [[RawRest.HandleRequest]] which handles standard REST requests by buffering the entire response.
   * This does <b>not</b> support streaming responses.
   *
   * @param baseUrl                 The base URL for the REST service.
   * @param customMaxResponseLength Optional override for the maximum response length.
   * @param customTimeout           Optional override for the request timeout.
   * @return A `RawRest.HandleRequest` that buffers responses.
   */
  def asHandleRequest(
    baseUrl: String,
    customMaxResponseLength: OptArg[Int] = OptArg.Empty,
    customTimeout: OptArg[Duration] = OptArg.Empty,
  ): RawRest.HandleRequest = {
    val timeout = customTimeout.getOrElse(defaultTimeout)
    val maxResponseLength = customMaxResponseLength.getOrElse(defaultMaxResponseLength)
    request => prepareRequest(baseUrl, timeout, request).flatMap(sendRequest(_, maxResponseLength))
  }

  private def prepareRequest(
    baseUrl: String,
    timeout: Duration,
    request: RestRequest,
  ): Task[Request] =
    Task(client.newRequest(baseUrl).method(request.method.name)).map { httpReq =>
      val path = baseUrl + PlainValue.encodePath(request.parameters.path)

      httpReq.path(path)
      request.parameters.query.entries.foreach {
        case (name, PlainValue(value)) => httpReq.param(name, value)
      }
      request.parameters.headers.entries.foreach {
        case (name, PlainValue(value)) => httpReq.headers(headers => headers.add(name, value))
      }
      request.parameters.cookies.entries.foreach {
        case (name, PlainValue(value)) => httpReq.cookie(HttpCookie.build(
          URLEncoder.encode(name, spaceAsPlus = true), URLEncoder.encode(value, spaceAsPlus = true)).build())
      }

      request.body match {
        case HttpBody.Empty =>
        case tb: HttpBody.Textual =>
          httpReq.body(new StringRequestContent(tb.contentType, tb.content, Charset.forName(tb.charset)))
        case bb: HttpBody.Binary =>
          httpReq.body(new BytesRequestContent(bb.contentType, bb.bytes))
      }

      timeout match {
        case fd: FiniteDuration => httpReq.timeout(fd.length, fd.unit)
        case _ =>
      }
      httpReq
    }

  private def sendRequest(httpReq: Request, maxResponseLength: Int): Task[RestResponse] =
    Task.async { (callback: Callback[Throwable, RestResponse]) =>
        httpReq.send(new BufferingResponseListener(maxResponseLength) {
          override def onComplete(result: Result): Unit =
            if (result.isSucceeded) {
              val httpResp = result.getResponse
              val response = RestResponse(
                code = httpResp.getStatus,
                headers = parseHeaders(httpResp),
                body = parseHttpBody(httpResp, this),
              )
              callback(Success(response))
            } else {
              callback(Failure(result.getFailure))
            }
        })
      }
      .doOnCancel(Task(httpReq.abort(new CancellationException("Request cancelled"))))

  private def parseHttpBody(httpResp: Response, listener: BufferingResponseListener): HttpBody = {
    val contentTypeOpt = httpResp.getHeaders.get(HttpHeader.CONTENT_TYPE).opt
    val charsetOpt = contentTypeOpt.map(MimeTypes.getCharsetFromContentType)
    (contentTypeOpt, charsetOpt) match {
      case (Opt(contentType), Opt(charset)) =>
        HttpBody.textual(
          content = listener.getContentAsString,
          mediaType = MimeTypes.getContentTypeWithoutCharset(contentType),
          charset = charset,
        )
      case (Opt(contentType), Opt.Empty) =>
        HttpBody.binary(listener.getContent, contentType)
      case _ =>
        HttpBody.Empty
    }
  }

  private def parseHeaders(httpResp: Response): IMapping[PlainValue] =
    IMapping(httpResp.getHeaders.asScala.iterator.map(h => (h.getName, PlainValue(h.getValue))).toList)
}

object JettyRestClient {
  final val DefaultMaxResponseLength = 2 * 1024 * 1024
  final val DefaultTimeout = 10.seconds
  final val Streaming = HttpErrorException.plain(400, "HTTP stream failure")

  private def unsupportedContentTypeError(contentType: Opt[String]): HttpErrorException =
    HttpErrorException.plain(
      code = 400,
      message = s"Unsupported streaming Content-Type${contentType.mapOr("", c => s" = $c")}",
      cause = new UnsupportedOperationException,
    )

  @explicitGenerics
  def apply[RestApi: RawRest.AsRealRpc : RestMetadata](
    client: HttpClient,
    baseUri: String,
    maxResponseLength: Int = DefaultMaxResponseLength,
    timeout: Duration = DefaultTimeout,
  ): RestApi =
    new JettyRestClient(client, maxResponseLength, timeout).create[RestApi](baseUri)

  def asHandleRequest(
    client: HttpClient,
    baseUrl: String,
    maxResponseLength: Int = DefaultMaxResponseLength,
    timeout: Duration = DefaultTimeout,
  ): RawRest.HandleRequest =
    new JettyRestClient(client, maxResponseLength, timeout).asHandleRequest(baseUrl)

  def asHandleRequestWithStreaming(
    client: HttpClient,
    baseUrl: String,
    maxResponseLength: Int = DefaultMaxResponseLength,
    timeout: Duration = DefaultTimeout,
  ): RawRest.RestRequestHandler =
    new JettyRestClient(client, maxResponseLength, timeout).asHandleRequestWithStreaming(baseUrl)
}
