package io.udash
package rest

import com.softwaremill.sttp.SttpBackend
import io.udash.rest.raw.RawRest.HandleRequest

import scala.concurrent.Future

final class SttpRestCallTest extends ServletRestCallTest {
  implicit val backend: SttpBackend[Future, Nothing] = SttpRestClient.defaultBackend()

  def clientHandle: HandleRequest =
    SttpRestClient.asHandleRequest(s"$baseUrl/api")

  override protected def afterAll(): Unit = {
    backend.close()
    super.afterAll()
  }
}
