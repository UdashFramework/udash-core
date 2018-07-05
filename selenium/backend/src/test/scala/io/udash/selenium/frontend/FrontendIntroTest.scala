package io.udash.selenium.frontend

import io.udash.selenium.SeleniumTest
import org.openqa.selenium.By.ById

class FrontendIntroTest extends SeleniumTest {
  val url = "/frontend"

  "FrontendIntro view" should {
    driver.get(createUrl(url))

    "contain demo element" in {
      eventually {
        driver.findElementById("frontend-intro-demo")
      }
    }

    "give response on init values" in {
      val demo = driver.findElementById("frontend-intro-demo")
      val valid = demo.findElement(new ById("valid"))

      eventually {
        valid.getText should be("Yes")
      }
    }

    "report invalid values" in {
      val demo = driver.findElementById("frontend-intro-demo")
      val minimum = demo.findElement(new ById("minimum"))
      val between = demo.findElement(new ById("between"))
      val maximum = demo.findElement(new ById("maximum"))
      def valid = demo.findElement(new ById("valid"))

      def setValues(a: Int, b: Int, c: Int) = {
        minimum.clear()
        between.clear()
        maximum.clear()
        minimum.sendKeys(a.toString)
        between.sendKeys(b.toString)
        maximum.sendKeys(c.toString)
      }

      setValues(15, -15, 15)
      eventually {
        valid.getText.startsWith("No") should be(true)
      }

      setValues(15, 15, 15)
      eventually {
        valid.getText should be("Yes")
      }

      setValues(20, 15, 15)
      eventually {
        valid.getText.startsWith("No") should be(true)
      }

      setValues(-15, 15, 15)
      eventually {
        valid.getText should be("Yes")
      }

      setValues(8, 15, 7)
      eventually {
        valid.getText.startsWith("No") should be(true)
      }

      setValues(-15, 8, 15)
      eventually {
        valid.getText should be("Yes")
      }

      setValues(8, 15, 10)
      eventually {
        valid.getText.startsWith("No") should be(true)
      }

      setValues(-15, -15, 15)
      eventually {
        valid.getText should be("Yes")
      }
    }

    "randomize values on button click" in {
      val demo = driver.findElementById("frontend-intro-demo")
      val randomizeButton = eventually { demo.findElement(new ById("randomize")) }
      val minimum = demo.findElement(new ById("minimum"))
      val between = demo.findElement(new ById("between"))
      val maximum = demo.findElement(new ById("maximum"))

      var lastMinimum = minimum.getAttribute("value")
      var lastBetween = between.getAttribute("value")
      var lastMaximum = maximum.getAttribute("value")

      for (_ <- 1 to 5) {
        randomizeButton.click()
        eventually {
          (lastMinimum != minimum.getAttribute("value") ||
            lastBetween != between.getAttribute("value") ||
            lastMaximum != maximum.getAttribute("value")) should be(true)
        }

        lastMinimum = minimum.getAttribute("value")
        lastBetween = between.getAttribute("value")
        lastMaximum = maximum.getAttribute("value")
      }
    }
  }
}
