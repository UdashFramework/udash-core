package io.udash.selenium.rpc.demos.rpc

import io.udash.rpc._
import io.udash.rpc.utils.Logged

import scala.concurrent.Future

trait PingServerRPC {
  def ping(id: Int): Unit

  @Logged
  def fPing(id: Int): Future[Int]
}

object PingServerRPC extends DefaultServerUdashRPCFramework.RPCCompanion[PingServerRPC]
