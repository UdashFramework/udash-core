package io.udash
package rest.examples

import io.udash.rest.DefaultRestClient

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object ClientMain {
  def main(args: Array[String]): Unit = {
    val proxy = DefaultRestClient[UserApi]("http://localhost:9090/")

    // just for this example, normally it's not recommended
    import scala.concurrent.ExecutionContext.Implicits.global

    val result = proxy.createUser("Fred", 1990).andThen {
      case Success(user) => println(s"User ${user.id} created")
      case Failure(cause) => cause.printStackTrace()
    }

    // just wait until future is complete so that main thread doesn't finish prematurely
    Await.result(result, 10.seconds)
  }
}
