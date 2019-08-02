package io.udash
package rest
package raw

import com.avsystem.commons._
import com.avsystem.commons.meta._
import com.avsystem.commons.misc.{AbstractValueEnum, AbstractValueEnumCompanion, EnumCtx}
import com.avsystem.commons.rpc._

import scala.util.control.NoStackTrace

/**
  * Enum representing HTTP methods.
  */
final class HttpMethod(implicit enumCtx: EnumCtx) extends AbstractValueEnum
object HttpMethod extends AbstractValueEnumCompanion[HttpMethod] {
  final val GET, HEAD, POST, PUT, DELETE, CONNECT, OPTIONS, TRACE, PATCH: Value = new HttpMethod
}

/**
  * Aggregates serialized path, header and query parameters of an HTTP request.
  */
final case class RestParameters(
  @multi @tagged[Path] path: List[PlainValue] = Nil,
  @multi @tagged[Header] headers: IMapping[PlainValue] = IMapping.empty,
  @multi @tagged[Query] query: Mapping[RawQueryValue] = Mapping.empty,
  @multi @tagged[Cookie] cookies: Mapping[PlainValue] = Mapping.empty
) {

  /**
    * Appends path and query parameters to given base URI. Base URI must not contain query parameters.
    */
  def toUri(baseUri: String): String = {
    val builder = new StringBuilder(baseUri.stripSuffix("/"))
    PlainValue.encodePath(path, builder)
    if (query.nonEmpty) {
      builder.append("?")
    }
    RawQueryValue.encodeQuery(query, builder)
    builder.result()
  }

  def append(method: RestMethodMetadata[_], otherParameters: RestParameters): RestParameters =
    RestParameters(
      path ::: method.applyPathParams(otherParameters.path),
      headers ++ otherParameters.headers,
      query ++ otherParameters.query,
      cookies ++ otherParameters.cookies
    )

  def path(values: String*): RestParameters =
    copy(path = path ++ values.iterator.map(PlainValue(_)))

  def header(name: String, value: String): RestParameters =
    copy(headers = headers.append(name, PlainValue(value)))

  def query(name: String, value: String): RestParameters =
    copy(query = query.append(name, RawQueryValue.plain(value)))

  def cookie(name: String, value: String): RestParameters =
    copy(cookies = cookies.append(name, PlainValue(value)))
}
object RestParameters {
  final val Empty = RestParameters()
}

final case class HttpErrorException(code: Int, payload: OptArg[String] = OptArg.Empty, cause: Throwable = null)
  extends RuntimeException(s"HTTP ERROR $code${payload.fold("")(p => s": $p")}", cause) with NoStackTrace {
  def toResponse: RestResponse = RestResponse.plain(code, payload)
}

final case class RestRequest(method: HttpMethod, parameters: RestParameters, body: HttpBody) {
  def path(values: String*): RestRequest =
    copy(parameters = parameters.path(values: _*))

  def header(name: String, value: String): RestRequest =
    copy(parameters = parameters.header(name, value))

  def query(name: String, value: String): RestRequest =
    copy(parameters = parameters.query(name, value))

  def cookie(name: String, value: String): RestRequest =
    copy(parameters = parameters.cookie(name, value))
}
