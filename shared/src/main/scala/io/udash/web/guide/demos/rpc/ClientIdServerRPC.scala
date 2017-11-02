package io.udash.web.guide.demos.rpc

import com.avsystem.commons.rpc.RPCTypeClasses
import io.udash.rpc._
import io.udash.rpc.utils.Logged

import scala.concurrent.Future

@RPC
trait ClientIdServerRPC {
  @Logged
  def clientId(): Future[String]
}

object ClientIdServerRPC extends RPCTypeClasses[DefaultServerUdashRPCFramework.type, ClientIdServerRPC]