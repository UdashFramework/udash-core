package io.udash.web.guide

import io.udash.rpc._
import io.udash.web.guide.demos.DemosClientRPC

trait MainClientRPC {
  def demos(): DemosClientRPC
}

object MainClientRPC extends DefaultClientRpcCompanion[MainClientRPC]