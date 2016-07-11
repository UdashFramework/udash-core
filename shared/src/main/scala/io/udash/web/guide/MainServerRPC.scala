package io.udash.web.guide

import io.udash.rpc._
import io.udash.web.guide.demos.DemosServerRPC

@RPC
trait MainServerRPC {
  def demos(): DemosServerRPC
}
