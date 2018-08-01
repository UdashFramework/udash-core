package io.udash.rest.internal

import scala.concurrent.Future

/** Provides connection to REST server. */
trait RESTConnector {
  /** Sends HTTP request to REST server.
    * It should fail with [[io.udash.rest.internal.RESTConnector.RequestException]] if server returned code different than 2xx. */
  def send(url: String, method: RESTConnector.HttpMethod, queryArguments: Map[String, String], headers: Map[String, String], body: String): Future[String]
}

object RESTConnector {
  sealed trait HttpMethod
  case object GET extends HttpMethod
  case object POST extends HttpMethod
  case object PATCH extends HttpMethod
  case object PUT extends HttpMethod
  case object DELETE extends HttpMethod

  sealed abstract class RequestException(code: Int, response: String) extends Exception(s"Request error. Code $code, response: $response")

  /** Error returned for 3xx response from server. */
  case class Redirection(code: Int, response: String) extends RequestException(code, response)
  /** Error returned for 4xx response from server. */
  case class ClientException(code: Int, response: String) extends RequestException(code, response)
  /** Error returned for 5xx response from server. */
  case class ServerException(code: Int, response: String) extends RequestException(code, response)
}
