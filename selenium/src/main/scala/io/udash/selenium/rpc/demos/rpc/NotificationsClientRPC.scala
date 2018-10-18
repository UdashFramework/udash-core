package io.udash.selenium.rpc.demos.rpc

import io.udash.rpc._

trait NotificationsClientRPC {
  def notify(msg: String): Unit
}

object NotificationsClientRPC extends DefaultClientUdashRPCFramework.RPCCompanion[NotificationsClientRPC]