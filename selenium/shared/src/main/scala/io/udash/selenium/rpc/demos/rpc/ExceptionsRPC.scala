package io.udash.selenium.rpc.demos.rpc

import io.udash.rpc.DefaultServerUdashRPCFramework

import scala.concurrent.Future

trait ExceptionsRPC {
  def example(): Future[Unit]
  def exampleWithTranslatableError(): Future[Unit]
  def unknownError(): Future[Unit]
}

object ExceptionsRPC extends DefaultServerUdashRPCFramework.RPCCompanion[ExceptionsRPC]