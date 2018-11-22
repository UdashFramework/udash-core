package io.udash.selenium.demos.rpc

import io.udash.rpc.utils.ClientId
import io.udash.selenium.rpc.demos.rpc.ClientIdServerRPC

import scala.concurrent.Future

class ClientIdServer(implicit cid: ClientId) extends ClientIdServerRPC {
  override def clientId(): Future[String] = {
    Future.successful(cid.toString)
  }
}
