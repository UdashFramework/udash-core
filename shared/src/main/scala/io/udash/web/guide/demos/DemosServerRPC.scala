package io.udash.web.guide.demos

import com.avsystem.commons.rpc.RPC
import io.udash.web.guide.demos.activity.CallServerRPC
import io.udash.web.guide.demos.rpc.{ClientIdServerRPC, GenCodecServerRPC, NotificationsServerRPC, PingServerRPC}

@RPC
trait DemosServerRPC {
  import io.udash.i18n._

  def pingDemo(): PingServerRPC
  def clientIdDemo(): ClientIdServerRPC
  def notificationsDemo(): NotificationsServerRPC
  def gencodecsDemo(): GenCodecServerRPC
  def translations(): RemoteTranslationRPC

  def call(): CallServerRPC
}
