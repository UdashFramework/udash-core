package io.udash.selenium.rpc

import io.udash.rpc._
import io.udash.selenium.rpc.demos.DemosClientRPC

trait MainClientRPC {
  def demos(): DemosClientRPC
}

object MainClientRPC extends DefaultClientUdashRPCFramework.RPCCompanion[MainClientRPC]