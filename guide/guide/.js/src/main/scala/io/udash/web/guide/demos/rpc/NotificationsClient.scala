package io.udash.web.guide.demos.rpc

import io.udash.web.guide.Context

import scala.concurrent.Future

object NotificationsClient extends NotificationsClientRPC {

  import Context._

  private val listeners = scala.collection.mutable.ArrayBuffer[String => Any]()

  def registerListener(listener: String => Any): Future[Unit] = {
    listeners += listener
    if (listeners.size == 1) serverRpc.demos.notificationsDemo.register()
    else Future.unit
  }

  def unregisterListener(listener: String => Any): Future[Unit] = {
    listeners -= listener
    if (listeners.isEmpty) serverRpc.demos.notificationsDemo.unregister()
    else Future.unit
  }

  override def notify(msg: String): Unit = {
    listeners.foreach(_ (msg))
  }
}
