package io.udash.web.guide.demos.frontend

import io.udash.web.SeleniumTest
import org.openqa.selenium.By.{ByClassName, ByCssSelector, ByTagName}
import org.openqa.selenium.{By, Keys}

import com.avsystem.commons._
import scala.util.Random

class FrontendFormsTest extends SeleniumTest {
  override protected final val url = "/frontend/forms"

  "FrontendForms view" should {

    "contain working checkbox demo" in {
      val checkboxes = findElementById("checkbox-demo")

      def clickAndCheck(propertyName: String, expect: String) = {
        val checkbox = checkboxes.findElement(new ByClassName(s"checkbox-demo-$propertyName"))
        checkbox.click()
        eventually {
          checkboxes.findElements(new ByCssSelector(s"[data-bind=$propertyName]")).asScala.forall(el => {
            el.getText == expect
          }) should be(true)
          checkboxes.findElements(new ByClassName(s"checkbox-demo-$propertyName")).asScala.forall(el => {
            el.getAttribute("selected") == checkbox.getAttribute("selected")
          }) should be(true)
        }
      }

      for (_ <- 1 to 3) {
        clickAndCheck("a", "false")
        clickAndCheck("b", "true")
        clickAndCheck("c", "No")
        clickAndCheck("a", "true")
        clickAndCheck("b", "false")
        clickAndCheck("c", "Yes")
      }
    }

    "contain working check buttons demo" in {
      val checkButtons = findElementById("check-buttons-demo")

      def clickAndCheck(propertyName: String) = {
        val checkbox = checkButtons.findElement(new ByCssSelector(s"[data-label=$propertyName]")).findElement(new ByTagName("input"))
        checkbox.click()
        eventually {
          checkButtons.findElements(new ByClassName("check-buttons-demo-fruits")).asScala.forall(el => {
            val contains = el.getText.contains(propertyName)
            if (checkbox.getAttribute("selected") != null) contains else !contains
          }) should be(true)
          checkButtons.findElements(new ByCssSelector(s"[data-label=$propertyName]")).asScala.forall(el => {
            el.findElement(new ByTagName("input")).getAttribute("selected") == checkbox.getAttribute("selected")
          }) should be(true)
        }
      }

      for (_ <- 1 to 15) {
        clickAndCheck(Random.shuffle(Seq("Apple", "Orange", "Banana")).head)
      }
    }

    "contain working multi select demo" in {
      val multiSelect = findElementById("multi-select-demo")

      def clickAndCheck(propertyName: String, propertyIdx: Int) = {
        val select = multiSelect.findElement(new ByTagName("select"))
        val option = select.findElement(new ByCssSelector(s"[value='$propertyIdx']"))
        option.click()
        eventually {
          multiSelect.findElements(new ByClassName("multi-select-demo-fruits")).asScala.forall(el => {
            val contains = el.getText.contains(propertyName)
            if (option.getAttribute("selected") != null) contains else !contains
          }) should be(true)
          multiSelect.findElements(new ByTagName("select")).asScala.forall(el => {
            el.findElement(new ByCssSelector(s"[value='$propertyIdx']")).getAttribute("selected") == option.getAttribute("selected")
          }) should be(true)
        }
      }

      val options = Seq("Apple", "Orange", "Banana").zipWithIndex
      for (_ <- 1 to 15) {
        val (name, idx) = Random.shuffle(options).head
        clickAndCheck(name, idx)
      }
    }

    "contain working radio buttons demo" in {
      val radioButtons = findElementById("radio-buttons-demo")

      def clickAndCheck(propertyName: String, propertyIdx: Int) = {
        val radio = radioButtons.findElement(new ByCssSelector(s"[data-label=$propertyName]")).findElement(new ByTagName("input"))
        driver.executeScript("arguments[0].click();", radio)
        eventually {
          radioButtons.findElements(new ByClassName("radio-buttons-demo-fruits")).asScala.forall(el => {
            el.getText == propertyName
          }) should be(true)
          radioButtons.findElements(new ByCssSelector(s"input")).asScala.forall(el => {
            val eq = el.getAttribute("selected") == radio.getAttribute("selected")
            if (el.getAttribute("value").toInt == propertyIdx) eq else !eq
          }) should be(true)
        }
      }

      val options = Seq("Apple", "Orange", "Banana").zipWithIndex
      for (_ <- 1 to 15) {
        val (name, idx) = Random.shuffle(options).head
        clickAndCheck(name, idx)
      }
    }

    "contain working select demo" in {
      val selectDemo = findElementById("select-demo")

      def clickAndCheck(propertyName: String, propertyIdx: Int) = {
        val select = selectDemo.findElement(new ByTagName("select"))
        val option = select.findElement(new ByCssSelector(s"[value='$propertyIdx']"))
        option.click()
        eventually {
          selectDemo.findElements(new ByClassName("select-demo-fruits")).asScala.forall(el => {
            el.getText == propertyName
          }) should be(true)
          selectDemo.findElements(new ByTagName(s"select")).asScala.forall(el => {
            el.findElement(new ByCssSelector(s"[value='$propertyIdx']")).getAttribute("selected") == option.getAttribute("selected")
          }) should be(true)
        }
      }

      val options = Seq("Apple", "Orange", "Banana").zipWithIndex
      for (_ <- 1 to 15) {
        val (name, idx) = Random.shuffle(options).head
        clickAndCheck(name, idx)
      }
    }

    "contain working text area demo" in {
      val textAreaDemo = findElementById("text-area-demo")

      def typeAndCheck(text: String) = {
        val textArea = textAreaDemo.findElement(new ByTagName("textarea"))
        textArea.clear()
        textArea.sendKeys(text)
        eventually {
          textAreaDemo.findElements(new ByTagName(s"textarea")).asScala.forall(el => {
            el.getAttribute("value") == text
          }) should be(true)
        }
      }

      for (_ <- 1 to 15) {
        typeAndCheck(Random.shuffle(Seq("Apple", "Orange", "Banana")).head)
      }
    }

    "contain working text input demo" in {
      val inputsDemo = findElementById("inputs-demo")

      def typeAndCheck(text: String, tpe: String) = {
        val input = inputsDemo.findElement(new ByCssSelector(s"input[type=$tpe]"))
        input.clear()
        input.sendKeys(text)
        eventually {
          inputsDemo.findElements(new ByCssSelector(s"input[type=$tpe]")).asScala.forall(el => {
            el.getAttribute("value") == text
          }) should be(true)
        }
      }

      for (_ <- 1 to 15) {
        typeAndCheck(Random.shuffle(Seq("Apple", "Orange", "Banana")).head, "text")
        typeAndCheck(Random.shuffle(Seq("Apple", "Orange", "Banana")).head, "password")
        typeAndCheck(Random.shuffle(Seq("123354", "-123", "32")).head, "number")
      }
    }

    //todo migrate content from udash selenium or remove
    "contain working range input demo" ignore {
      val demo = findElementById("range-input-demo")

      val minInput = demo.findElement(By.id("range-min"))
      val maxInput = demo.findElement(By.id("range-max"))
      val stepInput = demo.findElement(By.id("range-step"))
      val selector1 = demo.findElement(By.id("range-selector1"))
      val selector2 = demo.findElement(By.id("range-selector2"))
      val label1 = demo.findElement(By.id("range-label1"))
      val label2 = demo.findElement(By.id("range-label2"))

      minInput.clear()
      minInput.sendKeys("0")
      maxInput.clear()
      maxInput.sendKeys("100")
      stepInput.clear()
      stepInput.sendKeys("1")

      for (_ <- 0 to 200) selector1.sendKeys(Keys.LEFT)
      eventually {
        label1.getText should be("Range selector: 0")
        label2.getText should be("Second selector: 0")
      }

      for (i <- 1 to 10) {
        selector2.sendKeys(Keys.RIGHT)
        eventually {
          label1.getText should be(s"Range selector: $i")
          label2.getText should be(s"Second selector: $i")
        }
      }

      stepInput.clear()
      stepInput.sendKeys("2")

      for (i <- 1 to 10) {
        selector1.sendKeys(Keys.RIGHT)
        eventually {
          label1.getText should be(s"Range selector: ${i * 2 + 10}")
          label2.getText should be(s"Second selector: ${i * 2 + 10}")
        }
      }

      stepInput.clear()
      stepInput.sendKeys("25")
      eventually {
        label1.getText should be("Range selector: 25")
        label2.getText should be("Second selector: 25")
      }

      for (i <- 1 to 2) {
        selector2.sendKeys(Keys.RIGHT)
        eventually {
          label1.getText should be(s"Range selector: ${i * 25 + 25}")
          label2.getText should be(s"Second selector: ${i * 25 + 25}")
        }
      }

      for (_ <- 1 to 20) {
        selector1.sendKeys(Keys.RIGHT)
        eventually {
          label1.getText should be(s"Range selector: 100")
          label2.getText should be(s"Second selector: 100")
        }
      }

      stepInput.clear()
      stepInput.sendKeys("5")
      maxInput.clear()
      maxInput.sendKeys("65")

      eventually {
        label1.getText should be(s"Range selector: 65")
        label2.getText should be(s"Second selector: 65")
      }

      maxInput.clear()
      maxInput.sendKeys("253")
      minInput.clear()
      minInput.sendKeys("89")

      eventually {
        label1.getText should be(s"Range selector: 89")
        label2.getText should be(s"Second selector: 89")
      }

      for (i <- 1 to 200) {
        selector1.sendKeys(Keys.RIGHT)
        eventually {
          label1.getText should be(s"Range selector: ${math.min(i * 5 + 89, 249)}")
          label2.getText should be(s"Second selector: ${math.min(i * 5 + 89, 249)}")
        }
      }

      eventually {
        label1.getText should be(s"Range selector: 249")
        label2.getText should be(s"Second selector: 249")
      }
    }
  }
}
