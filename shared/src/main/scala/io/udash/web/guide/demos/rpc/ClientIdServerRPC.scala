package io.udash.web.guide.demos.rpc

import com.avsystem.commons.rpc.RPC
import io.udash.rpc.utils.Logged

import scala.concurrent.Future

@RPC
trait ClientIdServerRPC {
  @Logged
  def clientId(): Future[String]
}
