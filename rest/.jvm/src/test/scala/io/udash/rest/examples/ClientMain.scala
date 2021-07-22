package io.udash
package rest.examples

import com.avsystem.commons.Try
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

    Try(Await.result(result, 10.seconds)) match {
      case Success(user) => println(s"User $user created")
      case Failure(cause) => cause.printStackTrace()
    }
  }
}
