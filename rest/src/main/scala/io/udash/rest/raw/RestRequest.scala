package io.udash
package rest
package raw

import com.avsystem.commons.meta.*
import com.avsystem.commons.misc.{AbstractValueEnum, AbstractValueEnumCompanion, EnumCtx}
import com.avsystem.commons.rpc.*

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
  @multi @tagged[Header] @allowOptional headers: IMapping[PlainValue] = IMapping.empty,
  @multi @tagged[Query] @allowOptional query: Mapping[PlainValue] = Mapping.empty,
  @multi @tagged[Cookie] @allowOptional cookies: Mapping[PlainValue] = Mapping.empty
) {
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
    copy(query = query.append(name, PlainValue(value)))

  def cookie(name: String, value: String): RestParameters =
    copy(query = cookies.append(name, PlainValue(value)))
}
object RestParameters {
  final val Empty = RestParameters()
}

case class HttpErrorException(code: Int, payload: HttpBody = HttpBody.Empty, cause: Throwable = null)
  extends RuntimeException(s"HTTP ERROR $code${payload.textualContentOpt.fold("")(p => s": $p")}", cause) with NoStackTrace {
  def toResponse: RestResponse = RestResponse(code, IMapping.empty, payload)
}
object HttpErrorException {
  def plain(code: Int, message: String, cause: Throwable = null): HttpErrorException =
    HttpErrorException(code, HttpBody.plain(message), cause)
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
