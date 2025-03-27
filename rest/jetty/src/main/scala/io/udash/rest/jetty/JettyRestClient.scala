package io.udash
package rest.jetty

import com.avsystem.commons.*
import com.avsystem.commons.annotation.explicitGenerics
import com.avsystem.commons.serialization.json.{JsonReader, JsonStringInput}
import io.udash.rest.raw.*
import io.udash.rest.util.Utils
import io.udash.utils.URLEncoder
import monix.eval.Task
import monix.execution.Callback
import monix.reactive.Observable
import org.eclipse.jetty.client.*
import org.eclipse.jetty.http.{HttpCookie, HttpHeader, MimeTypes}

import java.nio.charset.Charset
import scala.concurrent.CancellationException
import scala.concurrent.duration.*

/** TODO streaming doc */
final class JettyRestClient(
  client: HttpClient,
  defaultMaxResponseLength: Int = JettyRestClient.DefaultMaxResponseLength,
  defaultTimeout: Duration = JettyRestClient.DefaultTimeout,
) {

  @explicitGenerics
  def create[RestApi: RawRest.AsRealRpc : RestMetadata](
    baseUri: String,
    customMaxResponseLength: OptArg[Int] = OptArg.Empty,
    customTimeout: OptArg[Duration] = OptArg.Empty,
  ): RestApi =
    RawRest.fromHandleRequestWithStreaming[RestApi](
      asHandleRequestWithStreaming(baseUri, customMaxResponseLength, customTimeout)
    )

  /** TODO streaming doc */
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
        Task.async { (callback: Callback[Throwable, StreamedRestResponse]) =>
          val listener = new InputStreamResponseListener {
            override def onHeaders(response: Response): Unit = {
              super.onHeaders(response)
              // TODO streaming document content length behaviour
              val contentLength = response.getHeaders.getLongField(HttpHeader.CONTENT_LENGTH)
              if (contentLength == -1) {
                val contentTypeOpt = response.getHeaders.get(HttpHeader.CONTENT_TYPE).opt
                val mediaTypeOpt = contentTypeOpt.map(MimeTypes.getContentTypeWithoutCharset)
                val charsetOpt = contentTypeOpt.map(MimeTypes.getCharsetFromContentType)
                // TODO streaming error handling client-side ???
                val bodyOpt = mediaTypeOpt matchOpt {
                  case Opt(HttpBody.OctetStreamType) =>
                    // TODO streaming configure chunk size ???
                    StreamedBody.RawBinary(Observable.fromInputStream(Task.eval(getInputStream)))
                  case Opt(HttpBody.JsonType) =>
                    val charset = charsetOpt.getOrElse(HttpBody.Utf8Charset)
                    // suboptimal - maybe "online" parsing is possible using Jackson / other lib without waiting for full content ?
                    val elements: Observable[JsonValue] =
                      Observable
                        .fromTask(Utils.mergeArrays(Observable.fromInputStream(Task.eval(getInputStream))))
                        .map(raw => new String(raw, charset))
                        .flatMap { jsonStr =>
                          val input = new JsonStringInput(new JsonReader(jsonStr))
                          Observable
                            .fromIterator(Task.eval(input.readList().iterator(_.asInstanceOf[JsonStringInput].readRawJson())))
                            .map(JsonValue(_))
                        }
                    StreamedBody.JsonList(
                      elements = elements,
                      charset = charset,
                    )
                }
                bodyOpt.mapOr(
                  {
                    // TODO streaming error handling client-side
                    callback(Failure(new Exception(s"Unsupported content type $contentTypeOpt")))
                  },
                  body => {
                    val restResponse = StreamedRestResponse(
                      code = response.getStatus,
                      headers = parseHeaders(response),
                      body = body,
                      batchSize = 1,
                    )
                    callback(Success(restResponse))
                  }
                )
              }
            }

            override def onFailure(response: Response, failure: Throwable): Unit = {
              super.onFailure(response, failure)
              // TODO streaming error handling client-side ???
            }

            override def onComplete(result: Result): Unit = {
              super.onComplete(result)
              val httpResp = result.getResponse
              val contentLength = httpResp.getHeaders.getLongField(HttpHeader.CONTENT_LENGTH)
              if (contentLength != -1) {
                val contentTypeOpt = httpResp.getHeaders.get(HttpHeader.CONTENT_TYPE).opt
                val charsetOpt = contentTypeOpt.map(MimeTypes.getCharsetFromContentType)
                // TODO streaming client-side handle errors ?
                val rawBody = getInputStream.readAllBytes()
                val body = (contentTypeOpt, charsetOpt) match {
                  case (Opt(contentType), Opt(charset)) =>
                    StreamedBody.fromHttpBody(
                      HttpBody.textual(
                        content = new String(rawBody, charset),
                        mediaType = MimeTypes.getContentTypeWithoutCharset(contentType),
                        charset = charset,
                      )
                    )
                  case (Opt(contentType), Opt.Empty) =>
                    StreamedBody.fromHttpBody(HttpBody.binary(rawBody, contentType))
                  case _ =>
                    StreamedBody.Empty
                }
                val restResponse = StreamedRestResponse(
                  code = httpResp.getStatus,
                  headers = parseHeaders(httpResp),
                  body = body,
                  batchSize = 1,
                )
                callback(Success(restResponse))
              }
            }
          }
          httpReq.send(listener)
        }.doOnCancel(Task(httpReq.abort(new CancellationException("Request cancelled"))))
      }
  }

  /** TODO streaming doc */
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
              val contentTypeOpt = httpResp.getHeaders.get(HttpHeader.CONTENT_TYPE).opt
              val charsetOpt = contentTypeOpt.map(MimeTypes.getCharsetFromContentType)
              val body = (contentTypeOpt, charsetOpt) match {
                case (Opt(contentType), Opt(charset)) =>
                  HttpBody.textual(getContentAsString, MimeTypes.getContentTypeWithoutCharset(contentType), charset)
                case (Opt(contentType), Opt.Empty) =>
                  HttpBody.binary(getContent, contentType)
                case _ =>
                  HttpBody.Empty
              }
              val response = RestResponse(httpResp.getStatus, parseHeaders(httpResp), body)
              callback(Success(response))
            } else {
              callback(Failure(result.getFailure))
            }
        })
      }
      .doOnCancel(Task(httpReq.abort(new CancellationException("Request cancelled"))))

  private def parseHeaders(httpResp: Response): IMapping[PlainValue] =
    IMapping(httpResp.getHeaders.asScala.iterator.map(h => (h.getName, PlainValue(h.getValue))).toList)
}

object JettyRestClient {
  final val DefaultMaxResponseLength = 2 * 1024 * 1024
  final val DefaultTimeout = 10.seconds

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
