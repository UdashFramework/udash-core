package io.udash.selenium.rpc

import io.udash.selenium.rpc.demos.DemosClientRPC

class RPCService extends MainClientRPC {
  override def demos(): DemosClientRPC = DemosClient
}
