package io.udash.web.guide.demos.rpc

import io.udash.rpc._

@RPC
trait PingClientRPC {
  def pong(id: Int): Unit
}

object PingClientRPC extends DefaultClientUdashRPCFramework.RPCCompanion[PingClientRPC]
