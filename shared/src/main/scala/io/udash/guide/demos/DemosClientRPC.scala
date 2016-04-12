package io.udash.guide.demos

import io.udash.guide.demos.rpc.{NotificationsClientRPC, PingClientRPC}
import io.udash.rpc.ClientRPC

trait DemosClientRPC extends ClientRPC {
  def pingDemo(): PingClientRPC
  def notificationsDemo(): NotificationsClientRPC
}
