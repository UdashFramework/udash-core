package io.udash.selenium.rpc

import io.udash.rpc._
import io.udash.selenium.rpc.demos.DemosServerRPC

trait MainServerRPC {
  def demos(): DemosServerRPC
}

object MainServerRPC extends DefaultServerUdashRPCFramework.RPCCompanion[MainServerRPC]