package io.udash.selenium.rpc

import io.udash.selenium.SeleniumTest

class RpcLoggingTest extends SeleniumTest {
  val url = "/rpc"

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    driver.get(createUrl(url))
  }

  "Rpcbackend view" should {
    "contain example button" in {
      eventually {
        driver.findElementById("load-calls-btn")
      }
    }

    "receive ClientId in demo" in {
      val callDemo = driver.findElementById("ping-pong-call-demo")
      for (_ <- 0 to 2) {
        callDemo.click()
        eventually(callDemo.isEnabled should be(true))
      }

      val triggerBtn = driver.findElementById("load-calls-btn")
      def results = driver.findElementById("calls-list")

      triggerBtn.isEnabled should be(true)
      results.getText.equalsIgnoreCase("") should be(true)

      triggerBtn.click()

      eventually {
        results.getText should be("PingServerRPC.fPing args: [0]\nPingServerRPC.fPing args: [1]\nPingServerRPC.fPing args: [2]")
      }

      callDemo.click()
      eventually(callDemo.isEnabled should be(true))
      triggerBtn.click()

      eventually {
        results.getText should be("PingServerRPC.fPing args: [0]\nPingServerRPC.fPing args: [1]\nPingServerRPC.fPing args: [2]\nPingServerRPC.fPing args: [3]")
      }
    }
  }
}
