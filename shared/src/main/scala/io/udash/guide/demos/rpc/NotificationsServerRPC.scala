package io.udash.guide.demos.rpc

import io.udash.rpc.RPC

import scala.concurrent.Future

trait NotificationsServerRPC extends RPC {
  def register(): Future[Unit]
  def unregister(): Future[Unit]
}
