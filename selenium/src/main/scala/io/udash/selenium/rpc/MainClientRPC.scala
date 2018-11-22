package io.udash.selenium.rpc

import io.udash.rpc.DefaultRpcCompanion
import io.udash.selenium.rpc.demos.DemosClientRPC

trait MainClientRPC {
  def demos(): DemosClientRPC
}

object MainClientRPC extends DefaultRpcCompanion[MainClientRPC]