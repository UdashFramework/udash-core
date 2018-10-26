package io.udash
package rest.examples

import io.udash.rest.RestServlet
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.{ServletContextHandler, ServletHolder}

import scala.concurrent.Future

class UserApiImpl extends UserApi {
  def createUser(name: String, birthYear: Int): Future[User] =
    Future.successful(User(s"$name-ID", name, birthYear))
}

object ServerMain {
  def main(args: Array[String]): Unit = {
    val server = new Server(9090)
    val handler = new ServletContextHandler
    handler.addServlet(new ServletHolder(RestServlet[UserApi](new UserApiImpl)), "/")
    server.setHandler(handler)
    server.start()
    server.join()
  }
}
