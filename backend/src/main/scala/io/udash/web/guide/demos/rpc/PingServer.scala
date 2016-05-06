package io.udash.web.guide.demos.rpc

import java.util.concurrent.TimeUnit

import io.udash.web.guide.rpc.ClientRPC
import io.udash.rpc._

import scala.concurrent.Future

class PingServer(implicit clientId: ClientId) extends PingServerRPC {
  import io.udash.web.Implicits._

  override def ping(id: Int): Unit = {
    ClientRPC(clientId).demos().pingDemo().pong(id)
  }

  override def fPing(id: Int): Future[Int] = {
    Future.successful(id)
  }
}
