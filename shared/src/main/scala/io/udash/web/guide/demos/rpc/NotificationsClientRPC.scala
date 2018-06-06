package io.udash.web.guide.demos.rpc

import io.udash.rpc._

trait NotificationsClientRPC {
  def notify(msg: String): Unit
}

object NotificationsClientRPC extends DefaultClientUdashRPCFramework.RPCCompanion[NotificationsClientRPC]