package io.udash.web.guide.demos.rpc

import com.avsystem.commons.rpc.RPC

import scala.concurrent.Future

@RPC
trait PingServerRPC {
  def ping(id: Int): Unit
  def fPing(id: Int): Future[Int]
}
