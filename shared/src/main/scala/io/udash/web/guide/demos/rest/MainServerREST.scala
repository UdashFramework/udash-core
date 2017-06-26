package io.udash.web.guide.demos.rest

import io.udash.rest._

import scala.concurrent.Future

@REST
trait MainServerREST {
  def simple(): SimpleServerREST
  def echo(): EchoServerREST
}

@REST
trait SimpleServerREST {
  @GET def string(): Future[String]
  @GET def int(): Future[Int]
  @GET def cls(): Future[RestExampleClass]
}

@REST
trait EchoServerREST {
  def withQuery(@Query @RESTParamName("param") arg: String): Future[String]
  def withHeader(@Header @RESTParamName("X-test") arg: String): Future[String]
  def withUrlPart(@URLPart arg: String): Future[String]
  def withBody(@Body arg: String): Future[String]
}