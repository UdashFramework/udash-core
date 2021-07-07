package io.udash.rest

import scala.concurrent.Future

final class SomeServerApiImpl {
  @GET
  def thingy(param: Int): Future[String] = Future.successful((param - 1).toString)
}
object SomeServerApiImpl extends DefaultRestServerApiImplCompanion[SomeServerApiImpl]
