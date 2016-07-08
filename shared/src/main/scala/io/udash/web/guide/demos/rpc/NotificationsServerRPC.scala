package io.udash.web.guide.demos.rpc

import com.avsystem.commons.rpc.RPC
import io.udash.rpc.utils.Logged

import scala.concurrent.Future

@RPC
trait NotificationsServerRPC {
  @Logged
  def register(): Future[Unit]
  @Logged
  def unregister(): Future[Unit]
}
