package io.udash.selenium.rpc

import io.udash.selenium.SeleniumTest

class RpcLoggingTest extends SeleniumTest {
  val rpcBackendUrl = "/rpc"

  "Rpcbackend view" should {
    driver.get(createUrl(rpcBackendUrl))

    "contain example button" in {
      eventually {
        driver.findElementById("load-calls-btn")
      }
    }

    "receive ClientId in demo" in {
      val callDemo = driver.findElementById("ping-pong-call-demo")
      for (i <- 0 to 2) callDemo.click()

      val triggerBtn = driver.findElementById("load-calls-btn")
      def results = driver.findElementById("calls-list")

      triggerBtn.isEnabled should be(true)
      results.getText.equalsIgnoreCase("") should be(true)

      triggerBtn.click()

      eventually {
        results.getText should be("PingServerRPC.fPing args: [0]\nPingServerRPC.fPing args: [1]\nPingServerRPC.fPing args: [2]")
      }

      callDemo.click()
      triggerBtn.click()

      eventually {
        results.getText should be("PingServerRPC.fPing args: [0]\nPingServerRPC.fPing args: [1]\nPingServerRPC.fPing args: [2]\nPingServerRPC.fPing args: [3]")
      }
    }
  }
}
