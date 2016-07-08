package io.udash.web.guide.demos.rpc

import io.udash.rpc._

@RPC
trait NotificationsClientRPC {
  def notify(msg: String): Unit
}