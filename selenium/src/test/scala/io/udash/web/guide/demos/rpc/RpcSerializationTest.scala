package io.udash.web.guide.demos.rpc

import java.util.concurrent.TimeUnit

import io.udash.web.SeleniumTest
import org.openqa.selenium.WebElement

class RpcSerializationTest extends SeleniumTest {
  val rpcFrontendUrl = "/#/rpc/serialization"

  "RpcSerialization view" should {
    driver.get(server.createUrl(rpcFrontendUrl))

    "contain example button" in {
      eventually {
        driver.findElementById("gencodec-demo")
      }
    }

    "receive msg from backend" in {
      val callDemo = driver.findElementById("gencodec-demo")

      callDemo.isEnabled should be(true)
      callDemo.click()

      eventually {
        driver.findElementById("gencodec-demo-int").getText shouldNot be(empty)
        driver.findElementById("gencodec-demo-string").getText shouldNot be(empty)
        driver.findElementById("gencodec-demo-seq").getText shouldNot be(empty)
        driver.findElementById("gencodec-demo-map").getText shouldNot be(empty)
        driver.findElementById("gencodec-demo-caseClass").getText shouldNot be(empty)
        driver.findElementById("gencodec-demo-cls-int").getText shouldNot be(empty)
        driver.findElementById("gencodec-demo-cls-string").getText shouldNot be(empty)
        driver.findElementById("gencodec-demo-cls-var").getText shouldNot be(empty)
        driver.findElementById("gencodec-demo-sealedTrait").getText shouldNot be(empty)
      }
    }
  }

  private def responseElement: WebElement = driver.findElementById("notifications-demo-response")
}
