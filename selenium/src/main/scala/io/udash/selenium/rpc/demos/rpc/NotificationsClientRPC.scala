package io.udash.selenium.rpc.demos.rpc

import io.udash.rpc.DefaultRpcCompanion

trait NotificationsClientRPC {
  def notify(msg: String): Unit
}

object NotificationsClientRPC extends DefaultRpcCompanion[NotificationsClientRPC]