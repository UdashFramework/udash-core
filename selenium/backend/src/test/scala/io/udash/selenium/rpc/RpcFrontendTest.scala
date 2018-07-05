package io.udash.selenium.rpc

import java.util.concurrent.TimeUnit

import io.udash.selenium.SeleniumTest
import org.openqa.selenium.WebElement

class RpcFrontendTest extends SeleniumTest {
  val rpcFrontendUrl = "/rpc"

  "RpcFrontend view" should {
    driver.get(createUrl(rpcFrontendUrl))

    "contain example button" in {
      eventually {
        driver.findElementById("notifications-demo")
      }
    }

    "receive msg every second after registration" in {
      val callDemo = driver.findElementById("notifications-demo")

      callDemo.isEnabled should be(true)
      var responseText = responseElement.getText.stripPrefix("Last message: ")
      responseText.equalsIgnoreCase("-") should be(true)

      for (_ <- 1 to 3) {
        responseText = responseElement.getText
        callDemo.click()

        for (_ <- 1 to 3) eventually {
          responseElement.getText shouldNot be(responseText)
          responseText = responseElement.getText
        }

        callDemo.click()
        responseText = responseElement.getText

        for (_ <- 1 to 2) eventually {
          TimeUnit.SECONDS.sleep(2)
          responseElement.getText should be(responseText)
          responseText = responseElement.getText
        }
      }
    }
  }

  private def responseElement: WebElement = driver.findElementById("notifications-demo-response")
}
