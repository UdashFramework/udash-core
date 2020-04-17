package io.udash
package rest

import com.avsystem.commons._
import com.avsystem.commons.annotation.explicitGenerics
import com.softwaremill.sttp.Uri.QueryFragment.KeyValue
import com.softwaremill.sttp.Uri.QueryFragmentEncoding
import com.softwaremill.sttp._
import io.udash.rest.raw._

import scala.concurrent.Future

object SttpRestClient {
  def defaultBackend(): SttpBackend[Future, Nothing] = DefaultSttpBackend()

  /**
    * Creates a client instance of some REST API trait which translates method calls into HTTP requests
    * to given URI using STTP.
    */
  @explicitGenerics def apply[RestApi: RawRest.AsRealRpc : RestMetadata](
    baseUri: String,
    options: RequestOptions = RequestOptions(
      followRedirects = true,
      readTimeout = DefaultReadTimeout,
      maxRedirects = 32, //FollowRedirectsBackend.MaxRedirects
      m => StatusCodes.isSuccess(m.code),
      redirectToGet = false
    )
  )(implicit backend: SttpBackend[Future, Nothing]): RestApi =
    RawRest.fromHandleRequest[RestApi](asHandleRequest(baseUri, options.opt))

  /**
    * Creates a [[io.udash.rest.raw.RawRest.HandleRequest HandleRequest]] function which sends REST requests to
    * a specified base URI using default HTTP client implementation (sttp).
    */
  def asHandleRequest(baseUri: String, options: Opt[RequestOptions] = Opt.Empty)(
    implicit backend: SttpBackend[Future, Nothing]
  ): RawRest.HandleRequest =
    asHandleRequest(uri"$baseUri", options)

  private def toSttpRequest(baseUri: Uri, request: RestRequest, options: Opt[RequestOptions]): Request[Array[Byte], Nothing] = {
    val uri = baseUri |>
      (u => u.copy(path = u.path ++
        request.parameters.path.map(_.value))) |>
      (u => u.copy(queryFragments = u.queryFragments ++
        request.parameters.query.entries.iterator.map {
          case (k, PlainValue(v)) => KeyValue(k, v, QueryFragmentEncoding.All, QueryFragmentEncoding.All)
        }.toList
      ))

    val contentHeaders = request.body match {
      case HttpBody.Empty => Nil
      case neBody: HttpBody.NonEmpty =>
        List((HeaderNames.ContentType, neBody.contentType))
    }

    val paramHeaders = request.parameters.headers.entries.iterator.map {
      case (n, PlainValue(v)) => (n, v)
    }.toList

    val cookieHeaders = List(request.parameters.cookies.entries).filter(_.nonEmpty).map { cookies =>
      "Cookie" -> cookies.iterator.map {
        case (n, PlainValue(v)) =>
          require(!v.contains(";"), s"invalid cookie: $n=$v")
          s"$n=$v"
      }.mkString(";")
    }

    val paramsRequest =
      sttp.method(Method(request.method.name), uri)
        .headers(contentHeaders: _*)
        .headers(paramHeaders: _*)
        .headers(cookieHeaders: _*) |> (req => options.mapOr(req, opts => req.copy(options = opts)))

    val bodyRequest = request.body match {
      case HttpBody.Empty => paramsRequest
      case HttpBody.Textual(content, _, charset) => paramsRequest.body(content, charset)
      case HttpBody.Binary(bytes, _) => paramsRequest.body(bytes)
    }

    bodyRequest.response(ResponseAsByteArray)
  }

  private def fromSttpResponse(sttpResp: Response[Array[Byte]]): RestResponse =
    RestResponse(
      sttpResp.code,
      IMapping(sttpResp.headers.iterator.map { case (n, v) => (n, PlainValue(v)) }.toList),
      sttpResp.contentType.fold(HttpBody.empty) { contentType =>
        val mediaType = HttpBody.mediaTypeOf(contentType)
        HttpBody.charsetOf(contentType) match {
          case Opt(charset) =>
            // TODO: uncool that we have to go through byte array for textual body
            val text = sttpResp.body.fold(identity, new String(_, charset))
            HttpBody.textual(text, mediaType, charset)
          case _ =>
            // unsafeBody should be safe because error body should be recognized as textual
            HttpBody.binary(sttpResp.unsafeBody, contentType)
        }
      }
    )

  private def asHandleRequest(baseUri: Uri, options: Opt[RequestOptions])(
    implicit backend: SttpBackend[Future, Nothing]
  ): RawRest.HandleRequest =
    RawRest.safeHandle(request => {
      val sttpReq = toSttpRequest(baseUri, request, options)
      callback =>
        sttpReq.send().onCompleteNow(respTry => callback(respTry.map(fromSttpResponse)))
    })
}
