package io.udash.selenium.frontend

import io.udash.selenium.SeleniumTest
import org.openqa.selenium.By.{ByCssSelector, ById}
import org.openqa.selenium.WebElement

import scala.collection.JavaConverters._

class FrontendBindingsTest extends SeleniumTest {
  val url = "/frontend"

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    driver.get(createUrl(url))
  }

  "FrontendBinding view" should {
    "contain demo elements" in {
      eventually {
        driver.findElementById("bind-demo")
        driver.findElementById("produce-demo")
        driver.findElementById("repeat-demo")
      }
    }

    "contain working bind demo" in {
      def bind = driver.findElementById("bind-demo")

      def checkName(expect: String) = {
        eventually {
          bind.getText should be(s"Name: $expect")
        }
      }

      for (_ <- 1 to 3) {
        checkName("Diana")
        checkName("John")
        checkName("Amy")
        checkName("Bryan")
      }
    }

    "contain working produce demo" in {
      def produce = driver.findElementById("produce-demo")

      def checkName(expect: String) = {
        eventually {
          produce.findElement(new ById("produce-demo-name")).getText should be(expect)
        }
      }

      def collectIntegers(container: WebElement): String = {
        container.findElements(new ByCssSelector("*")).asScala.foldLeft("")((result, el) => result + el.getText)
      }

      var prevIntegers = ""
      def checkIntegers() = {
        eventually {
          val std = collectIntegers(produce.findElement(new ById("produce-demo-integers")))
          prevIntegers shouldNot be(std)
          prevIntegers = std
        }
      }

      for (_ <- 1 to 3) {
        checkName("Diana")
        checkIntegers()
        checkName("John")
        checkIntegers()
        checkName("Amy")
        checkIntegers()
        checkName("Bryan")
        checkIntegers()
      }
    }

    "contain working repeat demo" in {
      def validation = driver.findElementById("repeat-demo")

      def collectIntegers(container: WebElement): String = {
        container.findElements(new ByCssSelector("*")).asScala.foldLeft("")((result, el) => result + el.getText)
      }

      var prevIntegers = ""
      def checkIntegers() = {
        eventually {
          val repeat = collectIntegers(validation.findElement(new ById("repeat-demo-integers")))
          val produce = collectIntegers(validation.findElement(new ById("repeat-demo-integers-produce")))
          repeat should be(produce)
          prevIntegers shouldNot be(repeat)
          prevIntegers = repeat
        }
      }

      for (_ <- 1 to 5) checkIntegers()
    }
  }
}
