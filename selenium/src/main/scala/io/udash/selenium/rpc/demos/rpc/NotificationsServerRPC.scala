package io.udash.selenium.rpc.demos.rpc

import io.udash.rpc.DefaultRpcCompanion
import io.udash.rpc.utils.Logged

import scala.concurrent.Future

trait NotificationsServerRPC {
  @Logged
  def register(): Future[Unit]
  @Logged
  def unregister(): Future[Unit]
}

object NotificationsServerRPC extends DefaultRpcCompanion[NotificationsServerRPC]
