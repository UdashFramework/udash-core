package io.udash.selenium.rpc

import io.udash.rpc.DefaultClientRpcCompanion
import io.udash.selenium.rpc.demos.DemosClientRPC

trait MainClientRPC {
  def demos(): DemosClientRPC
}

object MainClientRPC extends DefaultClientRpcCompanion[MainClientRPC]