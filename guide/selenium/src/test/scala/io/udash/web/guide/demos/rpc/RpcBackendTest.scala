package io.udash.web.guide.demos.rpc

import io.udash.web.SeleniumTest

class RpcBackendTest extends SeleniumTest {
  val rpcBackendUrl = "/rpc/client-server"

  "Rpcbackend view" should {
    driver.get(server.createUrl(rpcBackendUrl))

    "contain example button" in {
      eventually {
        driver.findElementById("client-id-demo")
      }
    }

    "receive ClientId in demo" in {
      val callDemo = driver.findElementById("client-id-demo")
      var response = driver.findElementById("client-id-demo-response")

      callDemo.isEnabled should be(true)
      response.getText.equalsIgnoreCase("???") should be(true)

      callDemo.click()

      eventually {
        response = driver.findElementById("client-id-demo-response")
        response.getText.startsWith("ClientId") should be(true)
        callDemo.isEnabled should be(false)
      }
    }
  }
}
