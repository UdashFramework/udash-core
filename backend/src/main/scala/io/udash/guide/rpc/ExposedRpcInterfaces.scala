package io.udash.guide.rpc

import io.udash.guide.MainServerRPC
import io.udash.guide.demos.{DemosServer, DemosServerRPC}
import io.udash.rpc._

class ExposedRpcInterfaces(implicit clientId: ClientId) extends MainServerRPC {
  override def demos(): DemosServerRPC = new DemosServer
}