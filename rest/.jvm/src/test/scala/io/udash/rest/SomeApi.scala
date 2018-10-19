package io.udash
package rest

import scala.concurrent.Future

trait SomeApi {
  @GET
  def hello(who: String): Future[String]

  @POST("hello")
  def helloThere(who: String): Future[String]
}

object SomeApi extends DefaultRestApiCompanion[SomeApi] {
  def format(who: String) = s"Hello, $who!"
  val poison: String = "poison"

  val impl: SomeApi = new SomeApi {
    override def hello(who: String): Future[String] = {
      if (who == poison) throw new IllegalArgumentException(poison)
      else Future.successful(format(who))
    }

    override def helloThere(who: String): Future[String] = hello(who)
  }
}
