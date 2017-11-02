package io.udash.web.guide

import com.avsystem.commons.rpc.RPCTypeClasses
import io.udash.rpc._
import io.udash.web.guide.demos.DemosServerRPC

@RPC
trait MainServerRPC {
  def demos(): DemosServerRPC
}

object MainServerRPC extends RPCTypeClasses[DefaultServerUdashRPCFramework.type, MainServerRPC]