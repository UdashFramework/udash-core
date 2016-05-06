package io.udash.guide.rpc

import io.udash.guide.MainClientRPC
import io.udash.guide.demos.{DemosClient, DemosClientRPC}

class RPCService extends MainClientRPC {
  override def demos(): DemosClientRPC = DemosClient
}
