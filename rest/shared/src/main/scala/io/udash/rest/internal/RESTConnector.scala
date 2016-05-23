package io.udash.rest.internal

import scala.concurrent.Future

/** Provides connection to REST server. */
trait RESTConnector {
  /** Sends HTTP request to REST server.
    * It should fail with [[io.udash.rest.internal.RESTConnector.RequestException]] if server returned code different than 2xx. */
  def send(url: String, method: RESTConnector.HTTPMethod, queryArguments: Map[String, String], headers: Map[String, String], body: String): Future[String]
}

object RESTConnector {
  sealed trait HTTPMethod
  case object GET extends HTTPMethod
  case object POST extends HTTPMethod
  case object PATCH extends HTTPMethod
  case object PUT extends HTTPMethod
  case object DELETE extends HTTPMethod

  sealed trait RequestException extends Exception {
    val code: Int
    val response: String
  }
  /** Error returned for 3xx response from server. */
  case class Redirection(code: Int, response: String) extends RequestException
  /** Error returned for 4xx response from server. */
  case class ClientException(code: Int, response: String) extends RequestException
  /** Error returned for 5xx response from server. */
  case class ServerException(code: Int, response: String) extends RequestException
}
