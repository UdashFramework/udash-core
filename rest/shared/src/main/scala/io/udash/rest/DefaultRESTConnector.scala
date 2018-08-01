package io.udash.rest

import com.avsystem.commons.concurrent.RunNowEC
import com.avsystem.commons.misc.{AbstractValueEnum, AbstractValueEnumCompanion, EnumCtx, Opt}
import com.softwaremill.sttp.Uri.QueryFragment.KeyValue
import io.udash.rest.internal.RESTConnector
import io.udash.rest.internal.RESTConnector.HttpMethod

import scala.concurrent.Future

final class Protocol(val defaultPort: Int)(implicit enumCtx: EnumCtx) extends AbstractValueEnum

object Protocol extends AbstractValueEnumCompanion[Protocol] {
  final val http: Value = new Protocol(80)
  final val https: Value = new Protocol(443)
}

/** Default implementation of [[io.udash.rest.internal.RESTConnector]] for Udash REST. */
class DefaultRESTConnector(val protocol: Protocol, val host: String, val port: Int, val pathPrefix: String) extends RESTConnector {

  override def send(url: String, method: HttpMethod, queryArguments: Map[String, String], headers: Map[String, String], body: String): Future[String] = {
    import com.softwaremill.sttp._
    import io.udash.rest.DefaultSttpBackend.backend

    val sttpMethod = method match {
      case RESTConnector.GET => Method.GET
      case RESTConnector.POST => Method.POST
      case RESTConnector.PATCH => Method.PATCH
      case RESTConnector.PUT => Method.PUT
      case RESTConnector.DELETE => Method.DELETE
    }

    val sttpUri =
    //need to be concatenated manually, API for paths would double-escape the url
      uri"${Uri(protocol.name, host, port).toString() + pathPrefix.stripSuffix("/") + "/" + url.stripPrefix("/")}"
        .copy(queryFragments = queryArguments.map { case (k, v) => KeyValue(k, v) }.toVector)

    val request = sttp.copy[Id, String, Nothing](
      uri = sttpUri,
      method = sttpMethod,
      headers = headers.toVector,
      body = Opt(body).map(StringBody(_, "utf-8")).getOrElse(NoBody)
    )

    request.send().map(response =>
      response.code / 100 match {
        case 2 => response.unsafeBody
        case 3 => throw RESTConnector.Redirection(response.code, response.body.left.get)
        case 4 => throw RESTConnector.ClientException(response.code, response.body.left.get)
        case 5 => throw RESTConnector.ServerException(response.code, response.body.left.get)
      }
    )(RunNowEC)
  }
}
