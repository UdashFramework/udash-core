package io.udash.web.guide.demos.frontend

import io.udash.web.SeleniumTest
import org.openqa.selenium.By.{ByClassName, ByCssSelector, ByTagName}

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
          forAll(checkboxes.findElements(new ByCssSelector(s"[data-bind=$propertyName]")))(el =>
            el.getText shouldBe expect
          )
          forAll(checkboxes.findElements(new ByClassName(s"checkbox-demo-$propertyName")))(el =>
            el.isSelected shouldBe checkbox.isSelected
          )
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
          forAll(checkButtons.findElements(new ByClassName("check-buttons-demo-fruits")))(el => {
            val contains = el.getText.contains(propertyName)
            assert(if (checkbox.isSelected) contains else !contains)
          })
          forAll(checkButtons.findElements(new ByCssSelector(s"[data-label=$propertyName]")))(el =>
            el.findElement(new ByTagName("input")).isSelected shouldBe checkbox.isSelected
          )
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
          forAll(multiSelect.findElements(new ByClassName("multi-select-demo-fruits")))(el => {
            val contains = el.getText.contains(propertyName)
            assert(if (option.isSelected) contains else !contains)
          })
          forAll(multiSelect.findElements(new ByTagName("select")))(el => {
            el.findElement(new ByCssSelector(s"[value='$propertyIdx']")).isSelected shouldBe option.isSelected
          })
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
          forAll(radioButtons.findElements(new ByClassName("radio-buttons-demo-fruits")))(el =>
            el.getText shouldBe propertyName
          )
          forAll(radioButtons.findElements(new ByCssSelector(s"input")))(el => {
            val eq = el.isSelected == radio.isSelected
            assert(if (el.getDomProperty("value").toInt == propertyIdx) eq else !eq)
          })
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
          forAll(selectDemo.findElements(new ByClassName("select-demo-fruits")))(el => {
            el.getText shouldBe propertyName
          })
          forAll(selectDemo.findElements(new ByTagName(s"select")))(el => {
            el.findElement(new ByCssSelector(s"[value='$propertyIdx']")).isSelected shouldBe option.isSelected
          })
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
          forAll(textAreaDemo.findElements(new ByTagName(s"textarea")))(el => {
            el.getDomProperty("value") shouldBe text
          })
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
          forAll(inputsDemo.findElements(new ByCssSelector(s"input[type=$tpe]")))(el => {
            el.getDomProperty("value") shouldBe text
          })
        }
      }

      for (_ <- 1 to 15) {
        typeAndCheck(Random.shuffle(Seq("Apple", "Orange", "Banana")).head, "text")
        typeAndCheck(Random.shuffle(Seq("Apple", "Orange", "Banana")).head, "password")
        typeAndCheck(Random.shuffle(Seq("123354", "-123", "32")).head, "number")
      }
    }
  }
}
