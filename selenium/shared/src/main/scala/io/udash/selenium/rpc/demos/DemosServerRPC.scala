package io.udash.selenium.rpc.demos

import io.udash.rpc._
import io.udash.selenium.rpc.demos.activity.CallServerRPC
import io.udash.selenium.rpc.demos.rpc._

trait DemosServerRPC {
  import io.udash.i18n._

  def pingDemo(): PingServerRPC
  def clientIdDemo(): ClientIdServerRPC
  def notificationsDemo(): NotificationsServerRPC
  def gencodecsDemo(): GenCodecServerRPC
  def translations(): RemoteTranslationRPC
  def exceptions(): ExceptionsRPC

  def call(): CallServerRPC
}

object DemosServerRPC extends DefaultServerUdashRPCFramework.RPCCompanion[DemosServerRPC]