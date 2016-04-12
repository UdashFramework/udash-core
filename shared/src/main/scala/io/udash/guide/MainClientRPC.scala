package io.udash.guide

import io.udash.guide.demos.DemosClientRPC
import io.udash.rpc._

trait MainClientRPC extends ClientRPC {
  def demos(): DemosClientRPC
}
