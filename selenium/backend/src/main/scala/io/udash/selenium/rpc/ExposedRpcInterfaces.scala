package io.udash.selenium.rpc

import io.udash.rpc._
import io.udash.selenium.demos.DemosServer
import io.udash.selenium.demos.activity.CallLogger
import io.udash.selenium.rpc.demos.DemosServerRPC

import scala.concurrent.ExecutionContext

class ExposedRpcInterfaces(callLogger: CallLogger)(implicit clientId: ClientId, ec: ExecutionContext) extends MainServerRPC {
  override def demos(): DemosServerRPC = new DemosServer(callLogger)
}