package io.udash.selenium.demos

import io.udash.i18n.RemoteTranslationRPC
import io.udash.rpc.RpcServer
import io.udash.rpc.utils.ClientId
import io.udash.selenium.demos.activity.{CallLogger, CallServer}
import io.udash.selenium.demos.i18n.TranslationServer
import io.udash.selenium.demos.rpc._
import io.udash.selenium.rpc.MainClientRPC
import io.udash.selenium.rpc.demos.DemosServerRPC
import io.udash.selenium.rpc.demos.activity.CallServerRPC
import io.udash.selenium.rpc.demos.rpc._

import scala.concurrent.ExecutionContext

class DemosServer(server: RpcServer[_, MainClientRPC], callLogger: CallLogger)(implicit clientId: ClientId, ec: ExecutionContext) extends DemosServerRPC {
  override def pingDemo(): PingServerRPC = new PingServer(server)
  override def clientIdDemo(): ClientIdServerRPC = new ClientIdServer
  override def notificationsDemo(): NotificationsServerRPC = new NotificationsServer(server)
  override def gencodecsDemo(): GenCodecServerRPC = new GenCodecServer
  override def translations(): RemoteTranslationRPC = new TranslationServer
  override def exceptions(): ExceptionsRPC = new ExceptionsServer
  override def call(): CallServerRPC = new CallServer(callLogger)
}
