package io.udash.selenium.rpc.demos.rpc

import io.udash.rpc.DefaultServerRpcCompanion

import scala.concurrent.Future

trait ExceptionsRPC {
  def example(): Future[Unit]
  def exampleWithTranslatableError(): Future[Unit]
  def unknownError(): Future[Unit]
}

object ExceptionsRPC extends DefaultServerRpcCompanion[ExceptionsRPC]