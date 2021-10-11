package io.udash
package rest

import com.avsystem.commons._
import com.avsystem.commons.annotation.explicitGenerics
import io.udash.rest.raw._
import sttp.client3._
import monix.eval.Task
import sttp.model.Uri.QuerySegment.KeyValue
import sttp.model.Uri.{PathSegmentEncoding, QuerySegmentEncoding}
import sttp.model.{HeaderNames, Method, Uri, Header => SttpHeader}

import scala.concurrent.Future

object SttpRestClient {
  final val CookieHeader = "Cookie"

  def defaultBackend(): SttpBackend[Future, Any] = DefaultSttpBackend()

  final val DefaultRequestOptions = RequestOptions(
    followRedirects = true,
    readTimeout = DefaultReadTimeout,
    maxRedirects = 32, //FollowRedirectsBackend.MaxRedirects
    redirectToGet = false
  )

  /**
   * Creates a client instance of some REST API trait which translates method calls into HTTP requests
   * to given URI using STTP.
   */
  @explicitGenerics def apply[RestApi: RawRest.AsRealRpc : RestMetadata](
    baseUri: String,
    options: RequestOptions = DefaultRequestOptions
  )(implicit backend: SttpBackend[Future, Any]): RestApi =
    RawRest.fromHandleRequest[RestApi](asHandleRequest(baseUri, options))

  /**
   * Creates a [[io.udash.rest.raw.RawRest.HandleRequest HandleRequest]] function which sends REST requests to
   * a specified base URI using default HTTP client implementation (sttp).
   */
  def asHandleRequest(baseUri: String, options: RequestOptions = DefaultRequestOptions)(
    implicit backend: SttpBackend[Future, Any]
  ): RawRest.HandleRequest =
    asHandleRequest(baseUri, options)

  private def toSttpRequest(
    baseUri: String,
    request: RestRequest,
    options: RequestOptions
  ): Request[Array[Byte], Any] = {
    val querySegments = request.parameters.query.entries.iterator.map {
      case (k, PlainValue(v)) => KeyValue(k, v, QuerySegmentEncoding.All, QuerySegmentEncoding.All)
    }

    val uri = querySegments.foldLeft(
      uri"$baseUri".addPathSegments(request.parameters.path.map(pv => Uri.Segment(pv.value, PathSegmentEncoding.Standard)))
    )(_.addQuerySegment(_))

    val contentHeaders = request.body match {
      case HttpBody.Empty =>
        Map.empty[String, String]
      case neBody: HttpBody.NonEmpty =>
        Map(HeaderNames.ContentType -> neBody.contentType)
    }

    val paramHeaders = request.parameters.headers.entries.iterator
      .map { case (n, PlainValue(v)) => SttpHeader.unsafeApply(n, v) }.toList

    val cookieHeaders = List(request.parameters.cookies).filter(_.nonEmpty)
      .map(cookies => SttpHeader.unsafeApply(CookieHeader, PlainValue.encodeCookies(cookies)))

    val paramsRequest =
      basicRequest.method(Method(request.method.name), uri)
        .headers(contentHeaders)
        .headers(paramHeaders: _*)
        .headers(cookieHeaders: _*)
        .copy(options = options)

    val bodyRequest = request.body match {
      case HttpBody.Empty => paramsRequest
      case HttpBody.Textual(content, _, charset) => paramsRequest.body(content, charset)
      case HttpBody.Binary(bytes, _) => paramsRequest.body(bytes)
    }

    bodyRequest.response(ResponseAsByteArray)
  }

  private def fromSttpResponse(sttpResp: Response[Array[Byte]]): RestResponse =
    RestResponse(
      sttpResp.code.code,
      IMapping(sttpResp.headers.map { case SttpHeader(n, v) => (n, PlainValue(v)) }),
      sttpResp.contentType.fold(HttpBody.empty) { contentType =>
        val mediaType = HttpBody.mediaTypeOf(contentType)
        HttpBody.charsetOf(contentType) match {
          case Opt(charset) =>
            // TODO: uncool that we have to go through byte array for textual body
            val text = new String(sttpResp.body, charset)
            HttpBody.textual(text, mediaType, charset)
          case _ =>
            // unsafeBody should be safe because error body should be recognized as textual
            HttpBody.binary(sttpResp.body, contentType)
        }
      }
    )

  private def asHandleRequest(baseUri: String, options: RequestOptions)(
    implicit backend: SttpBackend[Future, Any]
  ): RawRest.HandleRequest = request =>
    Task.deferFuture(toSttpRequest(baseUri, request, options).send()).map(fromSttpResponse)
}
