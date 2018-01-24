package io.udash.web.guide.demos.rpc

import io.udash.rpc._
import io.udash.rpc.utils.Logged

import scala.concurrent.Future

@RPC
trait PingServerRPC {
  def ping(id: Int): Unit

  @Logged
  def fPing(id: Int): Future[Int]
}

object PingServerRPC extends DefaultServerUdashRPCFramework.RPCCompanion[PingServerRPC]
