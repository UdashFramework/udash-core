package io.udash.web.guide.demos.rpc

import com.avsystem.commons.rpc.RPCTypeClasses
import io.udash.rpc._
import io.udash.rpc.utils.Logged

import scala.concurrent.Future

@RPC
trait PingServerRPC {
  def ping(id: Int): Unit

  @Logged
  def fPing(id: Int): Future[Int]
}

object PingServerRPC extends RPCTypeClasses[DefaultServerUdashRPCFramework.type, PingServerRPC]
