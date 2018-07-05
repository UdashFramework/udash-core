package io.udash.selenium.demos.rpc

import java.util.concurrent.TimeUnit
import java.{time => jt}

import io.udash.rpc._
import io.udash.selenium.rpc.ClientRPC
import io.udash.selenium.rpc.demos.rpc.NotificationsServerRPC

import scala.concurrent.{ExecutionContext, Future}

class NotificationsServer()(implicit clientId: ClientId, ec: ExecutionContext) extends NotificationsServerRPC {
  override def register(): Future[Unit] = Future { NotificationsService.register }
  override def unregister(): Future[Unit] = Future { NotificationsService.unregister }
}

object NotificationsService {
  import scala.concurrent.ExecutionContext.Implicits.global

  private val clients = scala.collection.mutable.ArrayBuffer[ClientId]()

  def register(implicit clientId: ClientId) = clients.synchronized {
    clients += clientId
  }

  def unregister(implicit clientId: ClientId) = clients.synchronized {
    clients -= clientId
  }

  global.execute(new Runnable {
    override def run(): Unit = {
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
  })
}
