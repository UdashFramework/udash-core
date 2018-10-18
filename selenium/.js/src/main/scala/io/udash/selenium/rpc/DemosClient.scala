package io.udash.selenium.rpc

import io.udash.selenium.rpc.demos.DemosClientRPC
import io.udash.selenium.rpc.demos.rpc.{NotificationsClientRPC, PingClientRPC}

object DemosClient extends DemosClientRPC {
  override def pingDemo(): PingClientRPC = PingClient
  override def notificationsDemo(): NotificationsClientRPC = NotificationsClient
}
