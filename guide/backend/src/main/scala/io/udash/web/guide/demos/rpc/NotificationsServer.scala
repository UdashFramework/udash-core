package io.udash.web.guide.demos.rpc

import java.util.concurrent.TimeUnit
import java.{time => jt}

import io.udash.rpc._
import io.udash.web.guide.rpc.ClientRPC

import com.avsystem.commons._

import scala.concurrent.Future

class NotificationsServer(implicit clientId: ClientId) extends NotificationsServerRPC {
  override def register(): Future[Unit] = Future.eval(NotificationsService.register)

  override def unregister(): Future[Unit] = Future.eval(NotificationsService.unregister)
}

object NotificationsService {

  private val clients = scala.collection.mutable.ArrayBuffer[ClientId]()

  def register(implicit clientId: ClientId): Unit = clients.synchronized {
    clients += clientId
  }.discard

  def unregister(implicit clientId: ClientId): Unit = clients.synchronized {
    clients -= clientId
  }.discard

  import io.udash.web.Implicits.backendExecutionContext

  backendExecutionContext.execute { () =>
    while (true) {
      val msg = jt.LocalDateTime.now().toString
      clients.synchronized {
        clients.foreach(clientId => {
          ClientRPC(clientId).demos().notificationsDemo().notify(msg)
        })
      }
      TimeUnit.SECONDS.sleep(1)
    }
  }
}
