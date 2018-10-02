package io.udash.selenium.demos.rpc

import io.udash.rpc._
import io.udash.selenium.rpc.ClientRPC
import io.udash.selenium.rpc.demos.rpc.PingServerRPC

import scala.concurrent.{ExecutionContext, Future}

class PingServer()(implicit clientId: ClientId, ec: ExecutionContext) extends PingServerRPC {

  override def ping(id: Int): Unit = {
    ClientRPC(clientId).demos().pingDemo().pong(id)
  }

  override def fPing(id: Int): Future[Int] = {
    Future.successful(id)
  }
}
