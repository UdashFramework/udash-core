package io.udash.web.guide.demos.frontend

import io.udash.web.SeleniumTest
import org.openqa.selenium.By.{ByClassName, ByCssSelector, ByTagName}

import scala.collection.JavaConversions._
import scala.util.Random

class FrontendFormsTest extends SeleniumTest {
  val url = "/#/frontend/forms"

  "FrontendForms view" should {
    driver.get(server.createUrl(url))

    "contain demo elements" in {
      eventually {
        val checkbox = driver.findElementById("checkbox-demo")
        val checkButtons = driver.findElementById("check-buttons-demo")
        val multiSelect = driver.findElementById("multi-select-demo")
        val radioButtons = driver.findElementById("radio-buttons-demo")
        val select = driver.findElementById("select-demo")
        val textArea = driver.findElementById("text-area-demo")
        val inputs = driver.findElementById("inputs-demo")
      }
    }

    "contain working checkbox demo" in {
      val checkboxes = driver.findElementById("checkbox-demo")

      def clickAndCheck(propertyName: String, expect: String) = {
        val checkbox = checkboxes.findElement(new ByClassName(s"checkbox-demo-$propertyName"))
        checkbox.click()
        eventually {
          checkboxes.findElements(new ByCssSelector(s"[data-bind=$propertyName]")).forall(el => {
            el.getText == expect
          }) should be(true)
          checkboxes.findElements(new ByClassName(s"checkbox-demo-$propertyName")).forall(el => {
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
      val checkButtons = driver.findElementById("check-buttons-demo")

      def clickAndCheck(propertyName: String) = {
        val checkbox = checkButtons.findElement(new ByCssSelector(s"[data-label=$propertyName]")).findElement(new ByTagName("input"))
        checkbox.click()
        eventually {
          checkButtons.findElements(new ByClassName("check-buttons-demo-fruits")).forall(el => {
            val contains = el.getText.contains(propertyName)
            if (checkbox.getAttribute("selected") != null) contains else !contains
          }) should be(true)
          checkButtons.findElements(new ByCssSelector(s"[data-label=$propertyName]")).forall(el => {
            el.findElement(new ByTagName("input")).getAttribute("selected") == checkbox.getAttribute("selected")
          }) should be(true)
        }
      }

      for (_ <- 1 to 15) {
        clickAndCheck(Random.shuffle(Seq("Apple", "Orange", "Banana")).head)
      }
    }

    "contain working multi select demo" in {
      val multiSelect = driver.findElementById("multi-select-demo")

      def clickAndCheck(propertyName: String) = {
        val select = multiSelect.findElement(new ByTagName("select"))
        val option = select.findElement(new ByCssSelector(s"[value=$propertyName]"))
        option.click()
        eventually {
          multiSelect.findElements(new ByClassName("multi-select-demo-fruits")).forall(el => {
            val contains = el.getText.contains(propertyName)
            if (option.getAttribute("selected") != null) contains else !contains
          }) should be(true)
          multiSelect.findElements(new ByTagName("select")).forall(el => {
            el.findElement(new ByCssSelector(s"[value=$propertyName]")).getAttribute("selected") == option.getAttribute("selected")
          }) should be(true)
        }
      }

      for (_ <- 1 to 15) {
        clickAndCheck(Random.shuffle(Seq("Apple", "Orange", "Banana")).head)
      }
    }

    "contain working radio buttons demo" in {
      val radioButtons = driver.findElementById("radio-buttons-demo")

      def clickAndCheck(propertyName: String) = {
        val radio = radioButtons.findElement(new ByCssSelector(s"[data-label=$propertyName]")).findElement(new ByTagName("input"))
        radio.click()
        eventually {
          radioButtons.findElements(new ByClassName("radio-buttons-demo-fruits")).forall(el => {
            el.getText == propertyName
          }) should be(true)
          radioButtons.findElements(new ByCssSelector(s"input")).forall(el => {
            val eq = el.getAttribute("selected") == radio.getAttribute("selected")
            if (el.getAttribute("value") == propertyName) eq else !eq
          }) should be(true)
        }
      }

      for (_ <- 1 to 15) {
        clickAndCheck(Random.shuffle(Seq("Apple", "Orange", "Banana")).head)
      }
    }

    "contain working select demo" in {
      val selectDemo = driver.findElementById("select-demo")

      def clickAndCheck(propertyName: String) = {
        val select = selectDemo.findElement(new ByTagName("select"))
        val option = select.findElement(new ByCssSelector(s"[value=$propertyName]"))
        option.click()
        eventually {
          selectDemo.findElements(new ByClassName("select-demo-fruits")).forall(el => {
            el.getText == propertyName
          }) should be(true)
          selectDemo.findElements(new ByTagName(s"select")).forall(el => {
            el.findElement(new ByCssSelector(s"[value=$propertyName]")).getAttribute("selected") == option.getAttribute("selected")
          }) should be(true)
        }
      }

      for (_ <- 1 to 15) {
        clickAndCheck(Random.shuffle(Seq("Apple", "Orange", "Banana")).head)
      }
    }

    "contain working text area demo" in {
      val textAreaDemo = driver.findElementById("text-area-demo")

      def typeAndCheck(text: String) = {
        val textArea = textAreaDemo.findElement(new ByTagName("textarea"))
        textArea.clear()
        textArea.sendKeys(text)
        eventually {
          textAreaDemo.findElements(new ByTagName(s"textarea")).forall(el => {
            el.getAttribute("value") == text
          }) should be(true)
        }
      }

      for (_ <- 1 to 15) {
        typeAndCheck(Random.shuffle(Seq("Apple", "Orange", "Banana")).head)
      }
    }

    "contain working text input demo" in {
      val inputsDemo = driver.findElementById("inputs-demo")

      def typeAndCheck(text: String, tpe: String) = {
        val input = inputsDemo.findElement(new ByCssSelector(s"input[type=$tpe]"))
        input.clear()
        input.sendKeys(text)
        eventually {
          inputsDemo.findElements(new ByCssSelector(s"input[type=$tpe]")).forall(el => {
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
  }
}
