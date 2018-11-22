package io.udash.selenium.demos.rpc

import io.udash.rpc.RpcServer
import io.udash.rpc.utils.ClientId
import io.udash.selenium.rpc.MainClientRPC
import io.udash.selenium.rpc.demos.rpc.PingServerRPC

import scala.concurrent.{ExecutionContext, Future}

class PingServer(server: RpcServer[_, MainClientRPC])(implicit clientId: ClientId, ec: ExecutionContext) extends PingServerRPC {

  override def ping(id: Int): Unit = {
    server.call(clientId).demos().pingDemo().pong(id)
  }

  override def fPing(id: Int): Future[Int] = {
    Future.successful(id)
  }
}
