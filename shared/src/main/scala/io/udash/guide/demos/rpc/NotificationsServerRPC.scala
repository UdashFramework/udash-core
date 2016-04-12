package io.udash.guide.demos.rpc

import com.avsystem.commons.rpc.RPC

import scala.concurrent.Future

@RPC
trait NotificationsServerRPC {
  def register(): Future[Unit]
  def unregister(): Future[Unit]
}
