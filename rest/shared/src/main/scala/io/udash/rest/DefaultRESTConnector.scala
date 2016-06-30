package io.udash.rest

import java.nio.ByteBuffer

import fr.hmil.roshttp.{HttpRequest, Method}
import fr.hmil.roshttp.body.BodyPart
import io.udash.rest.internal.RESTConnector
import io.udash.rest.internal.RESTConnector.HTTPMethod

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions

/** Default implementation of [[io.udash.rest.internal.RESTConnector]] for Udash REST. */
class DefaultRESTConnector(val host: String, val port: Int, val pathPrefix: String)(implicit val ec: ExecutionContext) extends RESTConnector {
  private class InternalBodyPart(override val content: ByteBuffer) extends BodyPart {
    override val contentType: String = s"application/json; charset=utf-8"
  }

  override def send(url: String, method: HTTPMethod, queryArguments: Map[String, String], headers: Map[String, String], body: String): Future[String] = {
    val request: HttpRequest = HttpRequest()
      .withHost(host)
      .withPort(port)
      .withURL(pathPrefix.stripSuffix("/") + url)
      .withMethod(method)
      .withQueryParameters(queryArguments.toSeq:_*)
      .withHeaders(headers.toSeq:_*)

    val response =
      if (body == null) request.send()
      else request.send(new InternalBodyPart(ByteBuffer.wrap(body.getBytes("utf-8"))))

    response.flatMap(resp => {
      resp.statusCode / 100 match {
        case 2 => Future.successful(resp.body)
        case 3 => Future.failed(RESTConnector.Redirection(resp.statusCode, resp.body))
        case 4 => Future.failed(RESTConnector.ClientException(resp.statusCode, resp.body))
        case 5 => Future.failed(RESTConnector.ServerException(resp.statusCode, resp.body))
      }
    })
  }

  private implicit def methodConverter(method: HTTPMethod): Method = method match {
    case RESTConnector.GET => Method.GET
    case RESTConnector.POST => Method.POST
    case RESTConnector.PATCH => Method.PATCH
    case RESTConnector.PUT => Method.PUT
    case RESTConnector.DELETE => Method.DELETE
  }

}
