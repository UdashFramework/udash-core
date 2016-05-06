package io.udash.web.guide.demos

import com.avsystem.commons.rpc.RPC
import io.udash.web.guide.demos.rpc.{NotificationsClientRPC, PingClientRPC}

@RPC
trait DemosClientRPC {
  def pingDemo(): PingClientRPC
  def notificationsDemo(): NotificationsClientRPC
}
