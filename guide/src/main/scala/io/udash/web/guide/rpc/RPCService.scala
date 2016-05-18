package io.udash.web.guide.rpc

import io.udash.web.guide.MainClientRPC
import io.udash.web.guide.demos.{DemosClient, DemosClientRPC}

class RPCService extends MainClientRPC {
  override def demos(): DemosClientRPC = DemosClient
}
