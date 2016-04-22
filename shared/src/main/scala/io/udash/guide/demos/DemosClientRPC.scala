package io.udash.guide.demos

import com.avsystem.commons.rpc.RPC
import io.udash.guide.demos.rpc.{NotificationsClientRPC, PingClientRPC}

@RPC
trait DemosClientRPC {
  def pingDemo(): PingClientRPC
  def notificationsDemo(): NotificationsClientRPC
}
