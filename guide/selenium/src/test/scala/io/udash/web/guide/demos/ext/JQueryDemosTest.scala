package io.udash.web.guide.demos.ext

import io.udash.web.SeleniumTest
import org.openqa.selenium.By.{ByCssSelector, ById}

class JQueryDemosTest extends SeleniumTest {
  override protected final val url = "/ext/jquery"

  "JQueryExt view" should {
    "contain demo elements" in {
      eventually {
        driver.findElementById("jquery-events-demo")
        driver.findElementById("jquery-callbacks-demo")
      }
    }

    "contain working events demo" in {
      driver.navigate().refresh()
      def events = driver.findElementById("jquery-events-demo")

      val clickButton = events.findElement(new ById("click"))
      val offButton = events.findElement(new ById("off"))
      val list = events.findElement(new ByCssSelector("ul"))

      clickButton.click()
      list.getText should be("This will be added on every click\nThis will be added only once")

      clickButton.click()
      list.getText should be("This will be added on every click\nThis will be added only once\nThis will be added on every click")

      clickButton.click()
      list.getText should be("This will be added on every click\nThis will be added only once\nThis will be added on every click\nThis will be added on every click")

      offButton.click()

      clickButton.click()
      list.getText should be("This will be added on every click\nThis will be added only once\nThis will be added on every click\nThis will be added on every click")

      clickButton.click()
      list.getText should be("This will be added on every click\nThis will be added only once\nThis will be added on every click\nThis will be added on every click")
    }

    "contain working events demo (instant off)" in {
      driver.navigate().refresh()
      def events = driver.findElementById("jquery-events-demo")

      val clickButton = events.findElement(new ById("click"))
      val offButton = events.findElement(new ById("off"))
      val list = events.findElement(new ByCssSelector("ul"))

      offButton.click()

      clickButton.click()
      list.getText should be("")

      clickButton.click()
      list.getText should be("")

      clickButton.click()
      list.getText should be("")
    }

    "contain working callbacks demo" in {
      def events = driver.findElementById("jquery-callbacks-demo")

      val clickButton = events.findElement(new ById("fire"))
      val plusList = events.findElement(new ById("plus"))
      val minusList = events.findElement(new ById("minus"))
      val mulList = events.findElement(new ById("mul"))
      val divList = events.findElement(new ById("div"))

      plusList.getText should be("")
      minusList.getText should be("")
      mulList.getText should be("")
      divList.getText should be("")

      clickButton.click()

      plusList.getText shouldNot be("")
      minusList.getText shouldNot be("")
      mulList.getText shouldNot be("")
      divList.getText shouldNot be("")

      val oldPlusList = plusList.getText
      val oldMinusList = minusList.getText
      val oldMulList = mulList.getText
      val oldDivList = divList.getText

      clickButton.click()

      plusList.getText should be(oldPlusList)
      minusList.getText should be(oldMinusList)
      mulList.getText should be(oldMulList)
      divList.getText should be(oldDivList)
    }
  }
}
