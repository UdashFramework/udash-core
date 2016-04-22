package io.udash.guide

import com.avsystem.commons.rpc.RPC
import io.udash.guide.demos.DemosClientRPC

@RPC
trait MainClientRPC {
  def demos(): DemosClientRPC
}
