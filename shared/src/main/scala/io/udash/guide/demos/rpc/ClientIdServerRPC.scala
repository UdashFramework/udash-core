package io.udash.guide.demos.rpc

import com.avsystem.commons.rpc.RPC

import scala.concurrent.Future

@RPC
trait ClientIdServerRPC {
  def clientId(): Future[String]
}
