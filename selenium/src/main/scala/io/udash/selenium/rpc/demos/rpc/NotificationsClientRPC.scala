package io.udash.selenium.rpc.demos.rpc

import io.udash.rpc.DefaultClientRpcCompanion

trait NotificationsClientRPC {
  def notify(msg: String): Unit
}

object NotificationsClientRPC extends DefaultClientRpcCompanion[NotificationsClientRPC]