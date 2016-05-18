package io.udash.web.guide.demos

import io.udash.web.guide.demos.rpc.{NotificationsClient, NotificationsClientRPC, PingClient, PingClientRPC}

object DemosClient extends DemosClientRPC {
  override def pingDemo(): PingClientRPC = PingClient
  override def notificationsDemo(): NotificationsClientRPC = NotificationsClient
}
