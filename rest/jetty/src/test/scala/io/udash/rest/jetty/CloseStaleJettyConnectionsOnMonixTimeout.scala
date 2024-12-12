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
import org.scalatest.funsuite.AsyncFunSuite

import java.net.InetSocketAddress
import scala.concurrent.Future
import scala.concurrent.duration.{FiniteDuration, IntMult}

final class CloseStaleJettyConnectionsOnMonixTimeout extends AsyncFunSuite {

  test("close connection on monix task timeout") {
    import monix.execution.Scheduler.Implicits.global

    val MaxConnections: Int = 1 // to timeout quickly
    val Connections: Int = 10 // > MaxConnections
    val RequestTimeout: FiniteDuration = 1.hour // no timeout
    val CallTimeout: FiniteDuration = 300.millis


    val server = new Server(new InetSocketAddress("localhost", 0)) {
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

    val httpClient = new HttpClient() {
      setMaxConnectionsPerDestination(MaxConnections)
      setIdleTimeout(RequestTimeout.toMillis)
      start()
    }

    val client = JettyRestClient[RestApiWithNeverCounter](
      client = httpClient,
      baseUri = server.getConnectors.head |> { case connector: NetworkConnector => s"http://${connector.getHost}:${connector.getLocalPort}" },
      maxResponseLength = Int.MaxValue, // to avoid unnecessary logs
      timeout = RequestTimeout,
    )

    Task
      .traverse(List.range(0, Connections))(_ => Task.fromFuture(client.neverGet).timeout(CallTimeout).failed)
      .timeoutTo(Connections * CallTimeout + 500.millis, Task(fail("All connections should have been closed"))) // + 500 millis just in case
      .map(_ => assert(RestApiWithNeverCounter.Impl.counter.get() == Connections)) // neverGet should be called Connections times
      .guarantee(Task {
        server.stop()
        httpClient.stop()
      })
      .runToFuture
  }
}

object CloseStaleJettyConnectionsOnMonixTimeout {
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
