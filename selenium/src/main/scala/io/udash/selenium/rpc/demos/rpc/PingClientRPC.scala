package io.udash.selenium.rpc.demos.rpc

import io.udash.rpc.DefaultRpcCompanion

trait PingClientRPC {
  def pong(id: Int): Unit
}

object PingClientRPC extends DefaultRpcCompanion[PingClientRPC]
