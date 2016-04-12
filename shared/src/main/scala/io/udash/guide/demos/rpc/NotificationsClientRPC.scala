package io.udash.guide.demos.rpc

import io.udash.rpc.ClientRPC

trait NotificationsClientRPC extends ClientRPC {
  def notify(msg: String): Unit
}