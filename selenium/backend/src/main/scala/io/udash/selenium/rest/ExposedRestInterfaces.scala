package io.udash.selenium.rest

import io.udash.selenium.rpc.demos.rest.{EchoServerREST, MainServerREST, RestExampleClass, SimpleServerREST}

import scala.concurrent.Future

class ExposedRestInterfaces extends MainServerREST {
  override def simple(): SimpleServerREST = new SimpleServerREST {
    override def string(): Future[String] = Future.successful("OK")
    override def cls(): Future[RestExampleClass] = Future.successful(RestExampleClass(42, "Udash", (321.123, "REST Support")))
    override def int(): Future[Int] = Future.successful(123)
  }
  override def echo(): EchoServerREST = new EchoServerREST {
    override def withUrlPart(arg: String): Future[String] = Future.successful(s"URL:$arg")
    override def withQuery(arg: String): Future[String] = Future.successful(s"Query:$arg")
    override def withHeader(arg: String): Future[String] = Future.successful(s"Header:$arg")
    override def withBody(arg: String): Future[String] = Future.successful(s"Body:$arg")
  }
}