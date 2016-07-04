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
  @GET @RESTName("class") def cls(): Future[RestExampleClass]
}

@REST
trait EchoServerREST {
  @GET @SkipRESTName def withQuery(@Query @RESTName("param") arg: String): Future[String]
  @GET @SkipRESTName def withHeader(@Header @RESTName("X-test") arg: String): Future[String]
  @GET @SkipRESTName def withUrlPart(@URLPart arg: String): Future[String]
  @POST @SkipRESTName def withBody(@Body arg: String): Future[String]
}