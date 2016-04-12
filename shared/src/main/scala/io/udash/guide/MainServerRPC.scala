package io.udash.guide

import com.avsystem.commons.rpc.RPC
import io.udash.guide.demos.DemosServerRPC

@RPC
trait MainServerRPC {
  def demos(): DemosServerRPC
}
