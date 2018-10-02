package io.udash.selenium.rpc.demos.activity

import io.udash.rpc._

import scala.concurrent.Future

trait CallServerRPC {
  def calls: Future[Seq[Call]]
}

object CallServerRPC extends DefaultServerUdashRPCFramework.RPCCompanion[CallServerRPC]
