package io.udash.web.guide

import com.avsystem.commons.rpc.RPC
import io.udash.web.guide.demos.DemosServerRPC

@RPC
trait MainServerRPC {
  def demos(): DemosServerRPC
}
