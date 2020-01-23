package io.udash.web.guide.demos.rpc

import io.udash.web.SeleniumTest
import org.openqa.selenium.WebElement

class RpcIntroTest extends SeleniumTest {
  override protected final val url = "/rpc"

  "RpcIntro view" should {
    "receive response in call demo" in {
      val callDemo = findElementById("ping-pong-call-demo")
      buttonTest(callDemo)
    }

    "receive response in push demo" in {
      val pushDemo = findElementById("ping-pong-push-demo")
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
