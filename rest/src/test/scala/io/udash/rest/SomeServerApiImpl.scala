package io.udash.rest

import scala.concurrent.Future

final class SomeServerApiImpl {
  @GET
  def thingy(param: Int): Future[String] = Future.successful((param - 1).toString)

  val subapi = new SomeServerSubApiImpl
}
object SomeServerApiImpl extends DefaultRestServerApiImplCompanion[SomeServerApiImpl]

final class SomeServerSubApiImpl {
  @POST
  def yeet(data: String): Future[String] = Future.successful(s"yeet $data")
}
object SomeServerSubApiImpl extends DefaultRestServerApiImplCompanion[SomeServerSubApiImpl]
