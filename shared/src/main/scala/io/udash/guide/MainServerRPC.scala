package io.udash.guide

import io.udash.guide.demos.DemosServerRPC
import io.udash.rpc._

trait MainServerRPC extends RPC {
  def demos(): DemosServerRPC
}
