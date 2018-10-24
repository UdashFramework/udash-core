package io.udash.selenium.rpc.demos.rest

import io.udash.rest._

import scala.concurrent.Future

trait MainServerREST {
  def simple(): SimpleServerREST
  def echo(): EchoServerREST
}
object MainServerREST extends DefaultRESTFramework.RPCCompanion[MainServerREST]

trait SimpleServerREST {
  @GET def string(): Future[String]
  @GET def int(): Future[Int]
  @GET def cls(): Future[RestExampleClass]
}
object SimpleServerREST extends DefaultRESTFramework.RPCCompanion[SimpleServerREST]

trait EchoServerREST {
  def withQuery(@Query @RESTParamName("param") arg: String): Future[String]
  def withHeader(@Header @RESTParamName("X-test") arg: String): Future[String]
  def withUrlPart(@URLPart arg: String): Future[String]
  def withBody(@Body arg: String): Future[String]
}
object EchoServerREST extends DefaultRESTFramework.RPCCompanion[EchoServerREST]