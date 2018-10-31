package io.udash.web.guide.demos.rpc

import io.udash.rpc._

trait PingClientRPC {
  def pong(id: Int): Unit
}

object PingClientRPC extends DefaultClientRpcCompanion[PingClientRPC]
