package io.udash
package rest

import com.softwaremill.sttp.SttpBackend
import io.udash.rest.raw.HttpErrorException
import io.udash.rest.raw.RawRest.HandleRequest

import scala.concurrent.Future
import scala.concurrent.duration._

trait SttpClientRestTest extends ServletBasedRestApiTest {
  implicit val backend: SttpBackend[Future, Nothing] = SttpRestClient.defaultBackend()

  def clientHandle: HandleRequest =
    SttpRestClient.asHandleRequest(s"$baseUrl/api")

  override protected def afterAll(): Unit = {
    backend.close()
    super.afterAll()
  }
}

class SttpRestCallTest extends SttpClientRestTest with RestApiTestScenarios {
  def port: Int = 9090

  test("too large binary request") {
    val future = proxy.binaryEcho(Array.fill[Byte](maxPayloadSize + 1)(5))
    val exception = future.failed.futureValue
    assert(exception == HttpErrorException(413, "Payload is larger than maximum 1048576 bytes (1048577)"))
  }
}

class ServletTimeoutTest extends SttpClientRestTest {
  def port: Int = 9091
  override def serverTimeout: FiniteDuration = 1.millisecond

  test("rest method timeout") {
    val exception = proxy.neverGet.failed.futureValue
    assert(exception == HttpErrorException(500, "server operation timed out"))
  }
}
