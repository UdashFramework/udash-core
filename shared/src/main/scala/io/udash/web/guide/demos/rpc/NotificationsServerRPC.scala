package io.udash.web.guide.demos.rpc

import com.avsystem.commons.rpc.RPCTypeClasses
import io.udash.rpc._
import io.udash.rpc.utils.Logged

import scala.concurrent.Future

@RPC
trait NotificationsServerRPC {
  @Logged
  def register(): Future[Unit]
  @Logged
  def unregister(): Future[Unit]
}

object NotificationsServerRPC extends RPCTypeClasses[DefaultServerUdashRPCFramework.type, NotificationsServerRPC]
