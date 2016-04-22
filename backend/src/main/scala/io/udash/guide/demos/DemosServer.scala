package io.udash.guide.demos

import io.udash.guide.demos.rpc._
import io.udash.rpc._

class DemosServer(implicit clientId: ClientId) extends DemosServerRPC {
  override def pingDemo(): PingServerRPC = new PingServer
  override def clientIdDemo(): ClientIdServerRPC = new ClientIdServer
  override def notificationsDemo(): NotificationsServerRPC = new NotificationsServer
  override def gencodecsDemo(): GenCodecServerRPC = new GenCodecServer
}
