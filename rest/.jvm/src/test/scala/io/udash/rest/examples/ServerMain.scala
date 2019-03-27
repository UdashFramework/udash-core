package io.udash
package rest.examples

import io.udash.rest.RestServlet
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.{ServletContextHandler, ServletHolder}

import scala.concurrent.Future

class UserApiImpl extends UserApi {
  def createUser(name: String): Future[User] =
    Future.successful(User(UserId(0), name))
  def getUser(id: UserId): Future[User] =
    Future.successful(User(id, s"$id-name"))
}

object ServerMain {
  def main(args: Array[String]): Unit = {
    val server = new Server(9090)
    val handler = new ServletContextHandler
    handler.addServlet(new ServletHolder(RestServlet[UserApi](new UserApiImpl)), "/*")
    server.setHandler(handler)
    server.start()
    server.join()
  }
}
