package io.udash.legacyrest

import com.avsystem.commons.concurrent.RunNowEC
import com.avsystem.commons.misc.{AbstractValueEnum, AbstractValueEnumCompanion, EnumCtx, Opt}
import com.softwaremill.sttp.Uri.QueryFragment.KeyValue
import io.udash.legacyrest.internal.RESTConnector
import io.udash.legacyrest.internal.RESTConnector.HttpMethod

import scala.concurrent.Future

final class Protocol(val defaultPort: Int)(implicit enumCtx: EnumCtx) extends AbstractValueEnum

object Protocol extends AbstractValueEnumCompanion[Protocol] {
  final val http: Value = new Protocol(80)
  final val https: Value = new Protocol(443)
}

/** Default implementation of [[io.udash.legacyrest.internal.RESTConnector]] for Udash REST. */
class DefaultRESTConnector(val protocol: Protocol, val host: String, val port: Int, val pathPrefix: String) extends RESTConnector {

  override def send(url: String, method: HttpMethod, queryArguments: Map[String, String], headers: Map[String, String], body: String): Future[String] = {
    import com.softwaremill.sttp._
    import io.udash.legacyrest.DefaultSttpBackend.backend

    val sttpMethod = method match {
      case RESTConnector.HttpMethod.GET => Method.GET
      case RESTConnector.HttpMethod.POST => Method.POST
      case RESTConnector.HttpMethod.PATCH => Method.PATCH
      case RESTConnector.HttpMethod.PUT => Method.PUT
      case RESTConnector.HttpMethod.DELETE => Method.DELETE
    }

    //needs to be concatenated manually, API for paths would double-escape the url
    val sttpUri =
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
