package io.udash.selenium.rpc

import io.udash.rpc.RpcServer
import io.udash.rpc.utils.ClientId
import io.udash.selenium.demos.DemosServer
import io.udash.selenium.demos.activity.CallLogger
import io.udash.selenium.rpc.demos.DemosServerRPC

import scala.concurrent.ExecutionContext

class ExposedRpcInterfaces(
  server: RpcServer[_, MainClientRPC], callLogger: CallLogger
)(implicit clientId: ClientId, ec: ExecutionContext) extends MainServerRPC {
  override def demos(): DemosServerRPC = new DemosServer(server, callLogger)
}