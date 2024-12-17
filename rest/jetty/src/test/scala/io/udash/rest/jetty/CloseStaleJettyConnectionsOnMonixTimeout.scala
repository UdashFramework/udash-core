package io.udash.rest.jetty

import com.avsystem.commons.misc.ScalaDurationExtensions.durationIntOps
import com.avsystem.commons.universalOps
import io.udash.rest.jetty.CloseStaleJettyConnectionsOnMonixTimeout.RestApiWithNeverCounter
import io.udash.rest.{DefaultRestApiCompanion, GET, RestServlet}
import monix.eval.Task
import monix.execution.atomic.Atomic
import org.eclipse.jetty.client.HttpClient
import org.eclipse.jetty.ee8.servlet.{ServletContextHandler, ServletHolder}
import org.eclipse.jetty.server.{NetworkConnector, Server}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AsyncFunSuite

import java.net.InetSocketAddress
import scala.concurrent.Future
import scala.concurrent.duration.{FiniteDuration, IntMult}

final class CloseStaleJettyConnectionsOnMonixTimeout extends AsyncFunSuite with BeforeAndAfterEach {

  import monix.execution.Scheduler.Implicits.global

  private val MaxConnections: Int = 1 // to timeout quickly
  private val Connections: Int = 10 // > MaxConnections
  private val RequestTimeout: FiniteDuration = 1.hour // no timeout
  private val CallTimeout: FiniteDuration = 300.millis

  private var server: Server = _
  private var httpClient: HttpClient = _
  private var client: RestApiWithNeverCounter = _

  override def beforeEach(): Unit = {
    super.beforeEach()
    server = new Server(new InetSocketAddress("localhost", 0)) {
      setHandler(
        new ServletContextHandler().setup(
          _.addServlet(
            new ServletHolder(
              RestServlet[RestApiWithNeverCounter](RestApiWithNeverCounter.Impl)
            ),
            "/*",
          )
        )
      )
      start()
    }

    httpClient = new HttpClient() {
      setMaxConnectionsPerDestination(MaxConnections)
      setIdleTimeout(RequestTimeout.toMillis)
      start()
    }

    client = JettyRestClient[RestApiWithNeverCounter](
      client = httpClient,
      baseUri = server.getConnectors.head |> { case connector: NetworkConnector => s"http://${connector.getHost}:${connector.getLocalPort}" },
      maxResponseLength = Int.MaxValue, // to avoid unnecessary logs
      timeout = RequestTimeout,
    )
  }

  override def afterEach(): Unit = {
    RestApiWithNeverCounter.Impl.counter.set(0)
    server.stop()
    httpClient.stop()
    super.afterEach()
  }

  test("close connection on monix task timeout") {
    Task
      .traverse(List.range(0, Connections))(_ => Task.fromFuture(client.neverGet).timeout(CallTimeout).failed)
      .timeoutTo(Connections * CallTimeout + 500.millis, Task(fail("All connections should have been closed"))) // + 500 millis just in case
      .map(_ => assert(RestApiWithNeverCounter.Impl.counter.get() == Connections)) // neverGet should be called Connections times
      .runToFuture
  }

  test("close connection on monix task cancellation") {
    Task
      .traverse(List.range(0, Connections)) { i =>
        val cancelable = Task.fromFuture(client.neverGet).runAsync(_ => ())
        Task.sleep(100.millis)
          .restartUntil(_ => RestApiWithNeverCounter.Impl.counter.get() >= i)
          .map(_ => cancelable.cancel())
      }
      .map(_ => assert(RestApiWithNeverCounter.Impl.counter.get() == Connections))
      .runToFuture
  }
}

private object CloseStaleJettyConnectionsOnMonixTimeout {
  sealed trait RestApiWithNeverCounter {
    final val counter = Atomic(0)
    @GET def neverGet: Future[Unit]
  }

  object RestApiWithNeverCounter extends DefaultRestApiCompanion[RestApiWithNeverCounter] {
    final val Impl: RestApiWithNeverCounter = new RestApiWithNeverCounter {
      override def neverGet: Future[Unit] = {
        counter.increment()
        Future.never
      }
    }
  }
}
