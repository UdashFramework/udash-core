package io.udash.web.guide

import io.udash.rpc._
import io.udash.web.guide.demos.DemosClientRPC

@RPC
trait MainClientRPC {
  def demos(): DemosClientRPC
}
