package io.udash.selenium.frontend

import io.udash.selenium.SeleniumTest

class FrontendRoutingTest extends SeleniumTest {
  val url = "/frontend/routing"

  "FrontendRouting view" should {
    "contain demo element" in {
      driver.get(createUrl(url))
      eventually {
        val link = driver.findElementById("url-demo-link")
        driver.findElementById("url-demo-link-apple")
        driver.findElementById("url-demo-link-orange")
        driver.findElementById("url-demo-link-chocolate")
        driver.findElementById("url-demo-link-pizza")

        link.getText should be("/frontend/routing")
      }
    }

    "change URL without view redraw" in {
      driver.get(createUrl(url))
      val link = driver.findElementById("url-demo-link")
      val input = driver.findElementById("url-demo-input")

      val apple = driver.findElementById("url-demo-link-apple")
      val orange = driver.findElementById("url-demo-link-orange")
      val chocolate = driver.findElementById("url-demo-link-chocolate")
      val pizza = driver.findElementById("url-demo-link-pizza")

      link.getText should be("/frontend/routing")

      input.clear()
      input.sendKeys("It should not disappear... Selenium")

      apple.click()
      Thread.sleep(500) // wait for scroll
      eventually {
        driver.getCurrentUrl should endWith("/frontend/routing/apple")
        link.getText should be("/frontend/routing/apple")
      }

      orange.click()
      eventually {
        driver.getCurrentUrl should endWith("/frontend/routing/orange")
        link.getText should be("/frontend/routing/orange")
      }

      chocolate.click()
      eventually {
        driver.getCurrentUrl should endWith("/frontend/routing/chocolate")
        link.getText should be("/frontend/routing/chocolate")
      }

      pizza.click()
      eventually {
        driver.getCurrentUrl should endWith("/frontend/routing/pizza")
        link.getText should be("/frontend/routing/pizza")
      }

      input.getAttribute("value") should be("It should not disappear... Selenium")
    }

    "change URL basing on input without view redraw" in {
      driver.get(createUrl(url))
      val link = driver.findElementById("url-demo-link")
      val input = driver.findElementById("url-demo-input")

      val linkChanger = driver.findElementById("url-demo-link-input")
      val init = driver.findElementById("url-demo-link-init")

      init.getText should be("/frontend/routing")
      link.getText should be("/frontend/routing")

      input.clear()
      input.sendKeys("It should not disappear... Selenium")

      for (s <- Seq("test", "test with space", "hash#hash")) {
        linkChanger.clear()
        linkChanger.sendKeys(s)
        eventually {
          val escaped = s"/frontend/routing/${s.replaceAll(" ", "%20").replaceAll("#", "%23")}"
          val unescaped = s"/frontend/routing/$s"
          init.getText should be("/frontend/routing")
          link.getText should matchPattern {
            case str: String if str == escaped || str == unescaped =>
          }
        }
      }

      init.getText should be("/frontend/routing")
      input.getAttribute("value") should be("It should not disappear... Selenium")
    }
  }
}
