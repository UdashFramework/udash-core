package io.udash.selenium.rpc.demos.rpc

import io.udash.rpc._
import io.udash.rpc.utils.Logged

import scala.concurrent.Future

trait ClientIdServerRPC {
  @Logged
  def clientId(): Future[String]
}

object ClientIdServerRPC extends DefaultServerUdashRPCFramework.RPCCompanion[ClientIdServerRPC]