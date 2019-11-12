package io.udash
package web.guide.demos.rest

import io.udash.web.SeleniumTest
import org.openqa.selenium.By.{ById, ByTagName}
import org.scalatest.Ignore

//todo migrate content from udash selenium or remove
@Ignore
class RestIntroTest extends SeleniumTest {
  val url = "/rest"

  "RestIntro view" should {
    "receive response in demo" in {
      val callDemo = findElementById("simple-rest-demo")
      val stringButton = callDemo.findElement(new ById("simple-rest-demo-string-btn"))
      val intButton = callDemo.findElement(new ById("simple-rest-demo-int-btn"))
      val classButton = callDemo.findElement(new ById("simple-rest-demo-class-btn"))

      eventually {
        val responses = callDemo.findElements(new ByTagName("div"))
        responses.size should be(4)
        responses.get(1).getText should be("String: -")
        responses.get(2).getText should be("Int: 0")
        responses.get(3).getText should be("Class: None")
      }

      stringButton.click()
      intButton.click()
      classButton.click()

      eventually {
        val responses = callDemo.findElements(new ByTagName("div"))
        responses.size should be(4)
        responses.get(1).getText should be("String: OK")
        responses.get(2).getText should be("Int: 123")
        responses.get(3).getText should be("Class: Some(RestExampleClass(42,Udash,RestTuple(321.123,REST Support)))")
      }
    }
  }
}
