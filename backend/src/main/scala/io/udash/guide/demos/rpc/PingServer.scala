package io.udash.guide.demos.rpc

import java.util.concurrent.TimeUnit

import io.udash.guide.rpc.ClientRPC
import io.udash.rpc._

import scala.concurrent.Future

class PingServer(implicit clientId: ClientId) extends PingServerRPC {
  import io.udash.guide.Implicits._

  override def ping(id: Int): Unit = {
    TimeUnit.SECONDS.sleep(1)
    ClientRPC(clientId).demos().pingDemo().pong(id)
  }

  override def fPing(id: Int): Future[Int] = {
    TimeUnit.SECONDS.sleep(1)
    Future.successful(id)
  }
}
