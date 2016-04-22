package io.udash.guide.demos

import com.avsystem.commons.rpc.RPC
import io.udash.guide.demos.rpc.{ClientIdServerRPC, GenCodecServerRPC, NotificationsServerRPC, PingServerRPC}

@RPC
trait DemosServerRPC {
  def pingDemo(): PingServerRPC
  def clientIdDemo(): ClientIdServerRPC
  def notificationsDemo(): NotificationsServerRPC
  def gencodecsDemo(): GenCodecServerRPC
}
