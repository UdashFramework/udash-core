package io.udash
package rest

import io.udash.rest.raw.HttpErrorException
import io.udash.rest.raw.RawRest.HandleRequest
import sttp.client3.SttpBackend

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

trait SttpClientRestTest extends ServletBasedRestApiTest {
  implicit val backend: SttpBackend[Future, Any] = SttpRestClient.defaultBackend()

  def clientHandle: HandleRequest =
    SttpRestClient.asHandleRequest[Future](s"$baseUrl/api")

  override protected def afterAll(): Unit = {
    backend.close()
    super.afterAll()
  }
}

class SttpRestCallTest extends SttpClientRestTest with RestApiTestScenarios {
  test("too large binary request") {
    val future = proxy.binaryEcho(Array.fill[Byte](maxPayloadSize + 1)(5))
    val exception = future.failed.futureValue
    assert(exception == HttpErrorException.plain(413, "Payload is larger than maximum 1048576 bytes (1048577)"))
  }
}

class ServletTimeoutTest extends SttpClientRestTest {
  override def serverTimeout: FiniteDuration = 500.millis

  test("rest method timeout") {
    val exception = proxy.neverGet.failed.futureValue
    assert(exception == HttpErrorException.plain(500, "server operation timed out after 500 milliseconds"))
  }

  test("subsequent requests with timeout") {
    assertThrows[HttpErrorException](Await.result(proxy.wait(600), Duration.Inf))
    assertThrows[HttpErrorException](Await.result(proxy.wait(600), Duration.Inf))
    assertThrows[HttpErrorException](Await.result(proxy.wait(600), Duration.Inf))
    assertThrows[HttpErrorException](Await.result(proxy.wait(600), Duration.Inf))
  }
}
