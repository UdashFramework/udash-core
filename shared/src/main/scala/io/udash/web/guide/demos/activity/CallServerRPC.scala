package io.udash.web.guide.demos.activity

import com.avsystem.commons.rpc.{RPC, RPCTypeClasses}
import io.udash.rpc.DefaultServerUdashRPCFramework

import scala.concurrent.Future

@RPC
trait CallServerRPC {
  def calls: Future[Seq[Call]]
}

object CallServerRPC extends RPCTypeClasses[DefaultServerUdashRPCFramework.type, CallServerRPC]
