package io.udash.web.guide.demos.rpc

import com.avsystem.commons.rpc.RPC

import scala.concurrent.Future

@RPC
trait ExceptionsRPC {
  def example(): Future[Unit]
  def exampleWithTranslatableError(): Future[Unit]
  def unknownError(): Future[Unit]
}