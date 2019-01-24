package io.udash
package rest

import com.avsystem.commons._
import com.avsystem.commons.annotation.explicitGenerics
import com.avsystem.commons.meta.Mapping
import com.softwaremill.sttp.Uri.QueryFragment.KeyValue
import com.softwaremill.sttp.Uri.QueryFragmentEncoding
import com.softwaremill.sttp._
import io.udash.rest.raw._

import scala.concurrent.Future

object SttpRestClient {
  def defaultBackend(): SttpBackend[Future, Nothing] = SttpRestClient.defaultBackend()

  @explicitGenerics def apply[RestApi: RawRest.AsRealRpc : RestMetadata](baseUri: String)(
    implicit backend: SttpBackend[Future, Nothing]
  ): RestApi =
    RawRest.fromHandleRequest[RestApi](asHandleRequest(baseUri))

  /**
    * Creates a [[io.udash.rest.raw.RawRest.HandleRequest HandleRequest]] function which sends REST requests to
    * a specified base URI using default HTTP client implementation (sttp).
    */
  def asHandleRequest(baseUri: String)(implicit backend: SttpBackend[Future, Nothing]): RawRest.HandleRequest =
    asHandleRequest(uri"$baseUri")

  private def toSttpRequest(baseUri: Uri, request: RestRequest): Request[String, Nothing] = {
    val uri = baseUri |>
      (u => u.copy(path = u.path ++
        request.parameters.path.map(_.value))) |>
      (u => u.copy(queryFragments = u.queryFragments ++
        request.parameters.query.iterator.map {
          case (k, QueryValue(v)) => KeyValue(k, v, QueryFragmentEncoding.All, QueryFragmentEncoding.All)
        }.toList
      ))

    val contentTypeHeader = request.body.mimeTypeOpt.map {
      mimeType => (HeaderNames.ContentType, s"$mimeType;charset=utf-8")
    }
    val paramHeaders = request.parameters.headers.iterator.map {
      case (n, HeaderValue(v)) => (n, v)
    }.toList

    sttp.copy[Id, String, Nothing](
      method = Method(request.method.name),
      uri = uri,
      headers = contentTypeHeader.toList ++ paramHeaders,
      body = request.body.contentOpt.map(StringBody(_, "utf-8")).getOrElse(NoBody)
    )
  }

  private def fromSttpResponse(sttpResp: Response[String]): RestResponse =
    RestResponse(
      sttpResp.code,
      Mapping(
        sttpResp.headers.iterator.map {
          case (n, v) => (n, HeaderValue(v))
        }.toList,
        caseInsensitive = true
      ),
      sttpResp.contentType.fold(HttpBody.empty) { contentType =>
        val mimeType = contentType.split(";", 2).head
        HttpBody(sttpResp.body.fold(identity, identity), mimeType)
      }
    )

  private def asHandleRequest(baseUri: Uri)(implicit backend: SttpBackend[Future, Nothing]): RawRest.HandleRequest =
    RawRest.safeHandle(request => {
      val sttpReq = toSttpRequest(baseUri, request)
      callback =>
        sttpReq.send().onCompleteNow(respTry => callback(respTry.map(fromSttpResponse)))
    })
}
