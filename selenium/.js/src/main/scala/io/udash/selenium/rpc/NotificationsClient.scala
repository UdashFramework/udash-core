package io.udash.selenium.rpc

import io.udash.selenium.rpc.demos.rpc.NotificationsClientRPC

import scala.concurrent.Future

object NotificationsClient extends NotificationsClientRPC {
  import io.udash.selenium.Launcher.serverRpc
  private val listeners = scala.collection.mutable.ArrayBuffer[String => Any]()

  def registerListener(listener: String => Any): Future[Unit] = {
    listeners += listener
    if (listeners.size == 1) serverRpc.demos().notificationsDemo().register()
    else Future.successful(())
  }

  def unregisterListener(listener: String => Any): Future[Unit] = {
    listeners -= listener
    if (listeners.isEmpty) serverRpc.demos().notificationsDemo().unregister()
    else Future.successful(())
  }

  override def notify(msg: String): Unit = {
    listeners.foreach(_(msg))
  }
}
