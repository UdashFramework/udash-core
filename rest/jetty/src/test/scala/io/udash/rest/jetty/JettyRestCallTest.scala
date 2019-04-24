package io.udash
package rest.jetty

import io.udash.rest.raw.RawRest.HandleRequest
import io.udash.rest.{RestApiTestScenarios, ServletBasedRestApiTest}
import org.eclipse.jetty.client.HttpClient

final class JettyRestCallTest extends ServletBasedRestApiTest with RestApiTestScenarios {
  val client: HttpClient = new HttpClient

  def clientHandle: HandleRequest =
    JettyRestClient.asHandleRequest(client, s"$baseUrl/api", maxPayloadSize)

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    client.start()
  }

  override protected def afterAll(): Unit = {
    client.stop()
    super.afterAll()
  }
}
