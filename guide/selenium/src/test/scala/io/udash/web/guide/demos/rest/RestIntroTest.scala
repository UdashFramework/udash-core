package io.udash.web.guide.demos.rest

import io.udash.web.SeleniumTest
import org.openqa.selenium.By.ById

class RestIntroTest extends SeleniumTest {
  val rpcIntroUrl = "/rest"

  "RestIntro view" should {
    driver.get(server.createUrl(rpcIntroUrl))

    "contain REST simple demo" in {
      eventually {
        driver.findElementById("simple-rest-demo")
      }
    }

    "receive response in demo" in {
      val callDemo = driver.findElementById("simple-rest-demo")
      val stringButton = callDemo.findElement(new ById("simple-rest-demo-string-btn"))
      val intButton = callDemo.findElement(new ById("simple-rest-demo-int-btn"))
      val classButton = callDemo.findElement(new ById("simple-rest-demo-class-btn"))
      val responseHeader = callDemo.findElement(new ById("simple-rest-demo-response-header"))
      val stringResponse = callDemo.findElement(new ById("simple-rest-demo-response-string"))
      val intResponse = callDemo.findElement(new ById("simple-rest-demo-response-int"))
      val classResponse = callDemo.findElement(new ById("simple-rest-demo-response-class"))

      eventually {
        responseHeader.getText should be("Results:")
      }

      eventually {
        stringResponse.getText should be("String: -")
        intResponse.getText should be("Int: 0")
        classResponse.getText should be("Class: None")
      }

      stringButton.click()
      intButton.click()
      classButton.click()

      eventually {
        stringResponse.getText should be("String: OK")
        intResponse.getText should be("Int: 123")
        classResponse.getText should be("Class: Some(RestExampleClass(42,Udash,InnerClass(321.123,REST Support)))")
      }
    }
  }
}
