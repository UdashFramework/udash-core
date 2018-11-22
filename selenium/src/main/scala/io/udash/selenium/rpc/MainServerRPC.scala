package io.udash.selenium.rpc

import io.udash.rpc.DefaultRpcCompanion
import io.udash.selenium.rpc.demos.DemosServerRPC

trait MainServerRPC {
  def demos(): DemosServerRPC
}

object MainServerRPC extends DefaultRpcCompanion[MainServerRPC]
