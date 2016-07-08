package io.udash.web.guide.demos

import io.udash.rpc._
import io.udash.web.guide.demos.rpc.{NotificationsClientRPC, PingClientRPC}

@RPC
trait DemosClientRPC {
  def pingDemo(): PingClientRPC
  def notificationsDemo(): NotificationsClientRPC
}
