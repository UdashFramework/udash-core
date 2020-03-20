package io.udash
package rest.examples

import io.udash.rest.SttpRestClient
import sttp.client.SttpBackend

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

object ClientMain {
  def main(args: Array[String]): Unit = {
    implicit val sttpBackend: SttpBackend[Future, Nothing, Nothing] = SttpRestClient.defaultBackend()
    val proxy: UserApi = SttpRestClient[UserApi]("http://localhost:9090")

    // make a remote REST call
    val result: Future[User] = proxy.createUser("Fred")

    // use whatever execution context is appropriate
    import scala.concurrent.ExecutionContext.Implicits.global

    result.onComplete {
      case Success(user) => println(s"User $user created")
      case Failure(cause) => cause.printStackTrace()
    }

    // just wait until future is complete so that main thread doesn't finish prematurely
    Await.ready(result, 10.seconds)
  }
}
