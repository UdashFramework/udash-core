package io.udash.guide.demos

import io.udash.guide.demos.rpc.{ClientIdServerRPC, NotificationsServerRPC, PingServerRPC}
import io.udash.rpc.RPC

trait DemosServerRPC extends RPC {
  def pingDemo(): PingServerRPC
  def clientIdDemo(): ClientIdServerRPC
  def notificationsDemo(): NotificationsServerRPC
}
