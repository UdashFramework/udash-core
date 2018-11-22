package io.udash.selenium.demos.rpc

import java.util.{Timer, TimerTask}
import java.{time => jt}

import io.udash.rpc._
import io.udash.rpc.utils.ClientId
import io.udash.selenium.rpc.MainClientRPC
import io.udash.selenium.rpc.demos.rpc.NotificationsServerRPC

import scala.concurrent.{ExecutionContext, Future}

class NotificationsServer(server: RpcServer[_, MainClientRPC])(implicit clientId: ClientId, ec: ExecutionContext) extends NotificationsServerRPC {
  NotificationsService.setRpcServer(server)
  override def register(): Future[Unit] = Future { NotificationsService.register }
  override def unregister(): Future[Unit] = Future { NotificationsService.unregister }
}

object NotificationsService {
  private val clients = scala.collection.mutable.ArrayBuffer[ClientId]()
  private var server: RpcServer[_, MainClientRPC] = _

  def setRpcServer(server: RpcServer[_, MainClientRPC]): Unit = {
    clients.synchronized(this.server = server)
  }

  def register(implicit clientId: ClientId): Unit = {
    clients.synchronized {
      clients += clientId
    }
  }

  def unregister(implicit clientId: ClientId): Unit = {
    clients.synchronized {
      clients -= clientId
    }
  }

  private val timer: Timer = new Timer()
  timer.scheduleAtFixedRate(new TimerTask {
    override def run(): Unit = {
      val msg = jt.LocalDateTime.now().toString
      clients.synchronized {
        server.call(clients, _.demos().notificationsDemo().notify(msg))
      }
    }
  }, 100, 100)
}
