package io.udash.web.guide.demos.rpc

import java.util.concurrent.TimeUnit
import java.{time => jt}

import io.udash.rpc._
import io.udash.web.guide.rpc.ClientRPC

import scala.concurrent.Future

class NotificationsServer(implicit clientId: ClientId) extends NotificationsServerRPC {
  import io.udash.web.Implicits.backendExecutionContext

  override def register(): Future[Unit] = Future { NotificationsService.register }

  override def unregister(): Future[Unit] = Future { NotificationsService.unregister }
}

object NotificationsService {
  import io.udash.web.Implicits.backendExecutionContext

  private val clients = scala.collection.mutable.ArrayBuffer[ClientId]()

  def register(implicit clientId: ClientId) = clients.synchronized {
    clients += clientId
  }

  def unregister(implicit clientId: ClientId) = clients.synchronized {
    clients -= clientId
  }

  backendExecutionContext.execute(() => {
    while (true) {
      val msg = jt.LocalDateTime.now().toString
      clients.synchronized {
        clients.foreach(clientId => {
          ClientRPC(clientId).demos().notificationsDemo().notify(msg)
        })
      }
      TimeUnit.SECONDS.sleep(1)
    }
  })
}
