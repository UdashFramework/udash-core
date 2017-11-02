package io.udash.web.guide.demos.rpc

import com.avsystem.commons.rpc.RPCTypeClasses
import io.udash.rpc._

@RPC
trait PingClientRPC {
  def pong(id: Int): Unit
}

object PingClientRPC extends RPCTypeClasses[DefaultClientUdashRPCFramework.type, PingClientRPC]
