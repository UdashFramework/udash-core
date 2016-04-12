package io.udash.guide.demos.rpc

import io.udash.rpc.ClientRPC

trait PingClientRPC extends ClientRPC {
  def pong(id: Int): Unit
}
