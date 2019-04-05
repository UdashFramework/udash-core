package io.udash.web.guide.demos

import io.udash.rpc._
import io.udash.web.guide.demos.activity.CallServerRPC
import io.udash.web.guide.demos.rpc._

trait DemosServerRPC {
  import io.udash.i18n._

  def pingDemo: PingServerRPC
  def clientIdDemo: ClientIdServerRPC
  def notificationsDemo: NotificationsServerRPC
  def gencodecsDemo: GenCodecServerRPC
  def translations: RemoteTranslationRPC
  def exceptions: ExceptionsRPC

  def call: CallServerRPC
}

object DemosServerRPC extends DefaultServerRpcCompanion[DemosServerRPC]
