package io.udash.selenium.rpc.demos.rpc

import io.udash.rpc.DefaultRpcCompanion
import io.udash.rpc.utils.Logged

import scala.concurrent.Future

trait ClientIdServerRPC {
  @Logged
  def clientId(): Future[String]
}

object ClientIdServerRPC extends DefaultRpcCompanion[ClientIdServerRPC]