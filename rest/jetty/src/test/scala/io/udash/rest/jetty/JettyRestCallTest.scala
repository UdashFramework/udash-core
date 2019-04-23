package io.udash
package rest.jetty

import io.udash.rest.ServletRestCallTest
import io.udash.rest.raw.RawRest.HandleRequest
import org.eclipse.jetty.client.HttpClient

final class JettyRestCallTest extends ServletRestCallTest {
  val client: HttpClient = new HttpClient

  def clientHandle: HandleRequest =
    JettyRestClient.asHandleRequest(client, s"$baseUrl/api", MaxPayloadSize)

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    client.start()
  }

  override protected def afterAll(): Unit = {
    client.stop()
    super.afterAll()
  }
}
