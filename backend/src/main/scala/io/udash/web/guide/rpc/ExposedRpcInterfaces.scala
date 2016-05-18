package io.udash.web.guide.rpc

import io.udash.web.guide.MainServerRPC
import io.udash.web.guide.demos.{DemosServer, DemosServerRPC}
import io.udash.rpc._

class ExposedRpcInterfaces(implicit clientId: ClientId) extends MainServerRPC {
  override def demos(): DemosServerRPC = new DemosServer
}