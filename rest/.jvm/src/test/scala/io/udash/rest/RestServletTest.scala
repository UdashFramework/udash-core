package io.udash.rest

import io.udash.rest.raw.HttpErrorException
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.{ServletContextHandler, ServletHolder}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite
import sttp.client.SttpBackend
import sttp.client.asynchttpclient.WebSocketHandler

import java.util.concurrent.Executors
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.Random

trait Waiter {
  def wait(millis: Int): Future[String]
}

object Waiter extends DefaultRestApiCompanion[Waiter]

object WaiterImpl extends Waiter {
  implicit val executionContext: ExecutionContext =
    ExecutionContext.fromExecutorService(Executors.newCachedThreadPool())

  override def wait(millis: Int): Future[String] = Future {
    Thread.sleep(millis)
    s"waited $millis"
  }
}

class RestServletTest extends AnyFunSuite with BeforeAndAfterAll {
  val port: Int = 32768 + Random.nextInt(32768)

  val server = new Server(port)
  val handler = new ServletContextHandler
  handler.addServlet(new ServletHolder(RestServlet[Waiter](WaiterImpl, 1.seconds)), "/*")
  server.setHandler(handler)

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    server.start()
  }

  override protected def afterAll(): Unit = {
    server.stop()
    super.afterAll()
  }

  test("async context protection") {
    implicit val backend: SttpBackend[Future, Nothing, WebSocketHandler] = DefaultSttpBackend()
    val client = SttpRestClient[Waiter](s"http://localhost:$port")

    assertThrows[HttpErrorException](Await.result(client.wait(1100), Duration.Inf))
    assertThrows[HttpErrorException](Await.result(client.wait(1100), Duration.Inf))
  }
}
