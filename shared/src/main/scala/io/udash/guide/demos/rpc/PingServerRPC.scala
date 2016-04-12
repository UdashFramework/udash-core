package io.udash.guide.demos.rpc

import io.udash.rpc.RPC

import scala.concurrent.Future

trait PingServerRPC extends RPC {
  def ping(id: Int): Unit
  def fPing(id: Int): Future[Int]
}
