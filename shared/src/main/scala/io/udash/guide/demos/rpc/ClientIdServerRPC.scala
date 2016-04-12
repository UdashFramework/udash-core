package io.udash.guide.demos.rpc

import io.udash.rpc.RPC

import scala.concurrent.Future

trait ClientIdServerRPC extends RPC {
  def clientId(): Future[String]
}
