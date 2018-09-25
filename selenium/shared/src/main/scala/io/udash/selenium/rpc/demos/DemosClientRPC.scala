package io.udash.selenium.rpc.demos

import io.udash.rpc._
import io.udash.selenium.rpc.demos.rpc.{NotificationsClientRPC, PingClientRPC}

trait DemosClientRPC {
  def pingDemo(): PingClientRPC
  def notificationsDemo(): NotificationsClientRPC
}

object DemosClientRPC extends DefaultClientUdashRPCFramework.RPCCompanion[DemosClientRPC]