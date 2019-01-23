package io.udash
package rest.examples

import io.udash.rest.DefaultRestClient

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

object ClientMain {
  def main(args: Array[String]): Unit = {
    val proxy: UserApi = DefaultRestClient[UserApi]("http://localhost:9090/")

    val result: Future[User] = proxy.createUser("Fred", 1990)

    // just for this example, normally it's not recommended
    import scala.concurrent.ExecutionContext.Implicits.global

    result.onComplete {
      case Success(user) => println(s"User ${user.id} created")
      case Failure(cause) => cause.printStackTrace()
    }

    // just wait until future is complete so that main thread doesn't finish prematurely
    Await.ready(result, 10.seconds)
  }
}
