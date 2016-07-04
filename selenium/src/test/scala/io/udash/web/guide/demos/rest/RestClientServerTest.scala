package io.udash.web.guide.demos.rest

import io.udash.web.SeleniumTest
import org.openqa.selenium.By.{ById, ByTagName}

class RestClientServerTest extends SeleniumTest {
  val rpcIntroUrl = "/#/rest/client-server"

  "RestClientServer view" should {
    driver.get(server.createUrl(rpcIntroUrl))

    "contain REST simple demo" in {
      eventually {
        driver.findElementById("echo-rest-demo")
      }
    }

    "receive response in demo" in {
      val callDemo = driver.findElementById("echo-rest-demo")
      val inputDemo = callDemo.findElement(new ById("echo-rest-demo-input"))
      val responseDemo = callDemo.findElement(new ById("echo-rest-demo-response"))
      val queryButton = callDemo.findElement(new ById("echo-rest-demo-query-btn"))
      val headerButton = callDemo.findElement(new ById("echo-rest-demo-header-btn"))
      val urlButton = callDemo.findElement(new ById("echo-rest-demo-url-btn"))
      val bodyButton = callDemo.findElement(new ById("echo-rest-demo-body-btn"))

      eventually {
        responseDemo.getText should be("Response:")
      }

      val request = inputDemo.getAttribute("value")

      queryButton.click()
      eventually {
        responseDemo.getText should be(s"Response: Query:$request")
      }

      headerButton.click()
      eventually {
        responseDemo.getText should be(s"Response: Header:$request")
      }

      urlButton.click()
      eventually {
        responseDemo.getText should be(s"Response: URL:$request")
      }

      bodyButton.click()
      eventually {
        responseDemo.getText should be(s"Response: Body:$request")
      }
    }
  }
}
