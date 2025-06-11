package io.udash
package rest

import io.udash.rest.raw.{HttpErrorException, RawRest}
import sttp.client3.{HttpClientFutureBackend, SttpBackend}

import java.net.http.HttpClient
import java.time.Duration as JDuration
import scala.concurrent.duration.*
import scala.concurrent.{Await, Future}

trait SttpClientRestTest extends ServletBasedRestApiTest {
  /**
   * Similar to the defaultHttpClient, but with a connection timeout
   * significantly exceeding the value of the CallTimeout
   */
  implicit val backend: SttpBackend[Future, Any] = HttpClientFutureBackend.usingClient(
    HttpClient
      .newBuilder()
      .connectTimeout(JDuration.ofMillis(IdleTimout.toMillis))
      .followRedirects(HttpClient.Redirect.NEVER)
      .build()
  )

  def clientHandle: RawRest.HandleRequest =
    SttpRestClient.asHandleRequest[Future](s"$baseUrl/api")

  override protected def afterAll(): Unit = {
    backend.close()
    super.afterAll()
  }
}

class SttpRestCallTest extends SttpClientRestTest with RestApiTestScenarios {
  "too large binary request" in {
    proxy.binaryEcho(Array.fill[Byte](maxPayloadSize + 1)(5))
      .failed
      .map { exception =>
        assert(exception == HttpErrorException.plain(413, "Payload is larger than maximum 1048576 bytes (1048577)"))
      }
  }
}

class ServletTimeoutTest extends SttpClientRestTest {
  override def serverTimeout: FiniteDuration = 300.millis

  "rest method timeout" in {
    proxy.neverGet
      .failed
      .map { exception =>
        assert(exception == HttpErrorException.plain(500, s"server operation timed out after $serverTimeout"))
      }
  }

  "subsequent requests with timeout" in {
    assertThrows[HttpErrorException](Await.result(proxy.wait(600), Duration.Inf))
    assertThrows[HttpErrorException](Await.result(proxy.wait(600), Duration.Inf))
    assertThrows[HttpErrorException](Await.result(proxy.wait(600), Duration.Inf))
    assertThrows[HttpErrorException](Await.result(proxy.wait(600), Duration.Inf))
  }
}
