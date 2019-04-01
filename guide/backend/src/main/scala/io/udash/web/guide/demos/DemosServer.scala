package io.udash.web.guide.demos

import io.udash.i18n.RemoteTranslationRPC
import io.udash.rpc._
import io.udash.web.guide.demos.activity.{CallLogger, CallServer, CallServerRPC}
import io.udash.web.guide.demos.i18n.TranslationServer
import io.udash.web.guide.demos.rpc._

class DemosServer(callLogger: CallLogger)(implicit clientId: ClientId) extends DemosServerRPC {
  override def pingDemo(): PingServerRPC = new PingServer
  override def clientIdDemo(): ClientIdServerRPC = new ClientIdServer
  override def notificationsDemo(): NotificationsServerRPC = new NotificationsServer
  override def gencodecsDemo(): GenCodecServerRPC = new GenCodecServer
  override def translations(): RemoteTranslationRPC = new TranslationServer
  override def exceptions(): ExceptionsRPC = new ExceptionsServer

  override def call(): CallServerRPC = new CallServer(callLogger)
}
