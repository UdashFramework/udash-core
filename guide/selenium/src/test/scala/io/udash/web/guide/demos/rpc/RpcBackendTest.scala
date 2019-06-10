package io.udash.web.guide.demos.rpc

import io.udash.web.SeleniumTest
import org.scalatest.BeforeAndAfterEach

class RpcBackendTest extends SeleniumTest with BeforeAndAfterEach {
  override protected final val url = "/rpc/client-server"

  "RpcBackend view" should {
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
