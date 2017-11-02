package io.udash.web.guide

import com.avsystem.commons.rpc.RPCTypeClasses
import io.udash.rpc._
import io.udash.web.guide.demos.DemosClientRPC

@RPC
trait MainClientRPC {
  def demos(): DemosClientRPC
}

object MainClientRPC extends RPCTypeClasses[DefaultClientUdashRPCFramework.type, MainClientRPC]