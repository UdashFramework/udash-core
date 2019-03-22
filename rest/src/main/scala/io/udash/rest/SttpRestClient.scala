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

  private final val ContentEncoding = "Content-Encoding"

  private def toSttpRequest(baseUri: Uri, request: RestRequest): Request[Array[Byte], Nothing] = {
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
        val contentTypeHeader = (HeaderNames.ContentType, neBody.contentType)
        val contentEncodingHeader = neBody.contentEncoding match {
          case Nil => Opt.Empty
          case encodings => Opt((HeaderNames.ContentEncoding, encodings.mkString(",")))
        }
        contentTypeHeader :: contentEncodingHeader.toList
    }

    val paramHeaders = request.parameters.headers.entries.iterator.map {
      case (n, PlainValue(v)) => (n, v)
    }.toList


    sttp.copy[Id, Array[Byte], Nothing](
      method = Method(request.method.name),
      uri = uri,
      headers = contentHeaders ++ paramHeaders,
      body = request.body match {
        case HttpBody.Empty => NoBody
        case HttpBody.Textual(content, _, charset) => StringBody(content, charset, None)
        case HttpBody.Binary(bytes, _, _) => ByteArrayBody(bytes, None)
      },
      response = ResponseAsByteArray
    )
  }

  private def fromSttpResponse(sttpResp: Response[Array[Byte]]): RestResponse =
    RestResponse(
      sttpResp.code,
      IMapping(sttpResp.headers.iterator.map { case (n, v) => (n, PlainValue(v)) }.toList),
      sttpResp.contentType.fold(HttpBody.empty) { contentType =>
        val mediaType = HttpBody.mediaTypeOf(contentType)
        val contentEncoding = sttpResp.header(ContentEncoding).toOpt
          .map(_.split(",").iterator.map(_.trim).toList).getOrElse(Nil)
        (HttpBody.charsetOf(contentType), contentEncoding) match {
          case (Opt(charset), Nil) =>
            // TODO: uncool that we have to go through byte array for textual body
            val text = sttpResp.body.fold(identity, new String(_, charset))
            HttpBody.textual(text, mediaType, charset)
          case _ =>
            // unsafeBody should be safe because error body should be recognized as textual
            HttpBody.binary(sttpResp.unsafeBody, contentType, contentEncoding)
        }
      }
    )

  private def asHandleRequest(baseUri: Uri)(implicit backend: SttpBackend[Future, Nothing]): RawRest.HandleRequest =
    RawRest.safeHandle(request => {
      val sttpReq = toSttpRequest(baseUri, request)
      callback =>
        sttpReq.send().onCompleteNow(respTry => callback(respTry.map(fromSttpResponse)))
    })
}
