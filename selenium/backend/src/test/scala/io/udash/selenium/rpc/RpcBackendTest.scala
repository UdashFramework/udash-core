package io.udash.selenium.rpc

import io.udash.selenium.SeleniumTest

class RpcBackendTest extends SeleniumTest {
  val url = "/rpc"

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    driver.get(createUrl(url))
  }

  "Rpcbackend view" should {
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
