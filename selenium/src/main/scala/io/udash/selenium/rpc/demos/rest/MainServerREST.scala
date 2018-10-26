package io.udash.selenium.rpc.demos.rest

import io.udash.rest._

import scala.concurrent.Future

trait MainServerREST {
  def simple(): SimpleServerREST
  def echo(): EchoServerREST
}
object MainServerREST extends DefaultRestApiCompanion[MainServerREST]

trait SimpleServerREST {
  @GET def string(): Future[String]
  @GET def int(): Future[Int]
  @GET def cls(): Future[RestExampleClass]
}
object SimpleServerREST extends DefaultRestApiCompanion[SimpleServerREST]

trait EchoServerREST {
  def withQuery(@Query("param") arg: String): Future[String]
  def withHeader(@Header("X-test") arg: String): Future[String]
  def withUrlPart(@Path arg: String): Future[String]
  def withBody(@Body arg: String): Future[String]
}
object EchoServerREST extends DefaultRestApiCompanion[EchoServerREST]
