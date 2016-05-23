package io.udash.web.guide.rpc

import io.udash.rpc._
import io.udash.web.guide.MainServerRPC
import io.udash.web.guide.demos.activity.CallLogger
import io.udash.web.guide.demos.{DemosServer, DemosServerRPC}

class ExposedRpcInterfaces(callLogger: CallLogger)(implicit clientId: ClientId) extends MainServerRPC {
  override def demos(): DemosServerRPC = new DemosServer(callLogger)
}