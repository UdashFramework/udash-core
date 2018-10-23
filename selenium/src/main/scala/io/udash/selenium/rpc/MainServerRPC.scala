package io.udash.selenium.rpc

import io.udash.rpc.DefaultServerRpcCompanion
import io.udash.selenium.rpc.demos.DemosServerRPC

trait MainServerRPC {
  def demos(): DemosServerRPC
}

object MainServerRPC extends DefaultServerRpcCompanion[MainServerRPC]
