package io.udash.selenium.rpc

import io.udash.selenium.SeleniumTest

class RpcSerializationTest extends SeleniumTest {
  val rpcFrontendUrl = "/rpc"

  "RpcSerialization view" should {
    driver.get(createUrl(rpcFrontendUrl))

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
        driver.findElementById("gencodec-demo-double").getText shouldNot be(empty)
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
}
