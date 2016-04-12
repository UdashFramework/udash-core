package io.udash.guide.demos.rpc

import io.udash.rpc._

import scala.concurrent.Future

class ClientIdServer(implicit cid: ClientId) extends ClientIdServerRPC {
  override def clientId(): Future[String] = {
    Future.successful(cid.toString)
  }
}
