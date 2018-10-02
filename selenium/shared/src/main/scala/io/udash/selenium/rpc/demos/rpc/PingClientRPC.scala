package io.udash.selenium.rpc.demos.rpc

import io.udash.rpc._

trait PingClientRPC {
  def pong(id: Int): Unit
}

object PingClientRPC extends DefaultClientUdashRPCFramework.RPCCompanion[PingClientRPC]
