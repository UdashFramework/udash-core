package io.udash.selenium.rpc.demos.rpc

import io.udash.rpc.DefaultClientRpcCompanion

trait PingClientRPC {
  def pong(id: Int): Unit
}

object PingClientRPC extends DefaultClientRpcCompanion[PingClientRPC]
