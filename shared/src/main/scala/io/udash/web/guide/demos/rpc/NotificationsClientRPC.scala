package io.udash.web.guide.demos.rpc

import com.avsystem.commons.rpc.RPCTypeClasses
import io.udash.rpc._

@RPC
trait NotificationsClientRPC {
  def notify(msg: String): Unit
}

object NotificationsClientRPC extends RPCTypeClasses[DefaultClientUdashRPCFramework.type, NotificationsClientRPC]