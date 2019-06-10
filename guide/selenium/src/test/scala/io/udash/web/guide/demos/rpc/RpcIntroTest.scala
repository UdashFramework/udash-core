package io.udash.web.guide.demos.rpc

import io.udash.web.SeleniumTest
import org.openqa.selenium.WebElement

class RpcIntroTest extends SeleniumTest {
  override protected final val url = "/rpc"

  "RpcIntro view" should {
    "contain two example buttons" in {
      eventually {
        driver.findElementById("ping-pong-call-demo")
        driver.findElementById("ping-pong-push-demo")
      }
    }

    "receive response in call demo" in {
      val callDemo = driver.findElementById("ping-pong-call-demo")
      buttonTest(callDemo)
    }

    "receive response in push demo" in {
      val pushDemo = driver.findElementById("ping-pong-push-demo")
      buttonTest(pushDemo)
    }
  }

  def buttonTest(callDemo: WebElement): Unit = {
    for (i <- 1 to 3) {
      callDemo.click()
      eventually {
        callDemo.isEnabled should be(true)
        callDemo.getText should be(s"Ping($i)")
      }
    }
  }
}
