package io.udash.guide.demos.rpc

import com.avsystem.commons.rpc.RPC

@RPC
trait NotificationsClientRPC {
  def notify(msg: String): Unit
}