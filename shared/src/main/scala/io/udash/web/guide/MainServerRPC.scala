package io.udash.web.guide

import io.udash.rpc._
import io.udash.web.guide.demos.DemosServerRPC

trait MainServerRPC {
  def demos(): DemosServerRPC
}

object MainServerRPC extends DefaultServerRpcCompanion[MainServerRPC]