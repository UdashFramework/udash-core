package io.udash
package rest.jetty

import com.avsystem.commons.*
import com.avsystem.commons.annotation.explicitGenerics
import io.udash.rest.raw.*
import io.udash.utils.URLEncoder
import monix.eval.Task
import monix.execution.Callback
import org.eclipse.jetty.client.*
import org.eclipse.jetty.http.{HttpCookie, HttpHeader, MimeTypes}

import java.nio.charset.Charset
import scala.concurrent.CancellationException
import scala.concurrent.duration.*

object JettyRestClient {
  final val DefaultMaxResponseLength = 2 * 1024 * 1024
  final val DefaultTimeout = 10.seconds

  @explicitGenerics def apply[RestApi: RawRest.AsRealRpc : RestMetadata](
    client: HttpClient,
    baseUri: String,
    maxResponseLength: Int = DefaultMaxResponseLength,
    timeout: Duration = DefaultTimeout
  ): RestApi =
    RawRest.fromHandleRequest[RestApi](asHandleRequest(client, baseUri, maxResponseLength, timeout))

  def asHandleRequest(
    client: HttpClient,
    baseUrl: String,
    maxResponseLength: Int = DefaultMaxResponseLength,
    timeout: Duration = DefaultTimeout
  ): RawRest.HandleRequest =
    request => Task(client.newRequest(baseUrl).method(request.method.name)).flatMap { httpReq =>
      Task.async { (callback: Callback[Throwable, RestResponse]) =>
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
                val headers = httpResp.getHeaders.asScala.iterator.map(h => (h.getName, PlainValue(h.getValue))).toList
                val response = RestResponse(httpResp.getStatus, IMapping(headers), body)
                callback(Success(response))
              } else {
                callback(Failure(result.getFailure))
              }
          })
        }
        .doOnCancel(Task(httpReq.abort(new CancellationException("Request cancelled"))))
    }
}
