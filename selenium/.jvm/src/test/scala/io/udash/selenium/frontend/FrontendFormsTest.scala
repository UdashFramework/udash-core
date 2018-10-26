package io.udash.selenium.frontend

import com.avsystem.commons._
import io.udash.selenium.SeleniumTest
import org.openqa.selenium.By.{ByClassName, ByCssSelector, ByTagName}
import org.openqa.selenium.{By, Keys}

import scala.util.Random

class FrontendFormsTest extends SeleniumTest {
  val url = "/frontend"

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    driver.get(createUrl(url))
  }

  "FrontendForms view" should {
    "contain demo elements" in {
      eventually {
        driver.findElementById("checkbox-demo")
        driver.findElementById("check-buttons-demo")
        driver.findElementById("multi-select-demo")
        driver.findElementById("radio-buttons-demo")
        driver.findElementById("select-demo")
        driver.findElementById("text-area-demo")
        driver.findElementById("inputs-demo")
        driver.findElementById("range-input-demo")
      }
    }

    "contain working checkbox demo" in {
      val checkboxes = driver.findElementById("checkbox-demo")

      def clickAndCheck(propertyName: String, expect: String) = {
        val checkbox = checkboxes.findElement(By.cssSelector(s"""label[for="checkbox-demo-$propertyName"]"""))
        checkbox.click()
        eventually {
          checkboxes.findElements(new ByCssSelector(s"[data-bind=$propertyName]")).asScala
            .foreach { el => el.getText should be(expect) }
          checkboxes.findElements(new ByClassName(s"checkbox-demo-$propertyName")).asScala
            .foreach { el => el.getAttribute("selected") should be(checkbox.getAttribute("selected")) }
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
        val checkboxLabel = checkButtons.findElement(By.id(s"check-label-$propertyName"))
        checkboxLabel.click()
        eventually {
          checkButtons.findElements(new ByClassName("check-buttons-demo-fruits")).asScala
            .foreach { el =>
              val contains = el.getText.contains(propertyName)
              val selected = checkboxLabel.findElement(By.xpath("../input")).getAttribute("selected")
              if (contains) selected shouldNot be(null)
              else selected should be(null)
            }
        }
      }

      for (_ <- 1 to 15) {
        clickAndCheck(Random.shuffle(Seq("Apple", "Orange", "Banana")).head)
      }
    }

    "contain working multi select demo" in {
      val multiSelect = driver.findElementById("multi-select-demo")

      def clickAndCheck(propertyName: String, propertyIdx: Int) = {
        val select = multiSelect.findElement(new ByTagName("select"))
        val option = select.findElement(new ByCssSelector(s"[value='$propertyIdx']"))
        option.click()
        eventually {
          multiSelect.findElements(new ByClassName("multi-select-demo-fruits")).asScala
            .foreach { el =>
              val contains = el.getText.contains(propertyName)
              val selected = option.getAttribute("selected")
              if (contains) selected shouldNot be(null)
              else selected should be(null)
            }
          multiSelect.findElements(new ByTagName("select")).asScala
            .foreach { el =>
              val inputSelected = el.findElement(new ByCssSelector(s"[value='$propertyIdx']")).getAttribute("selected")
              val optionSelected = option.getAttribute("selected")
              inputSelected should be(optionSelected)
            }
        }
      }

      val options = Seq("Apple", "Orange", "Banana").zipWithIndex
      for (_ <- 1 to 15) {
        val (name, idx) = Random.shuffle(options).head
        clickAndCheck(name, idx)
      }
    }

    "contain working radio buttons demo" in {
      val radioButtons = driver.findElementById("radio-buttons-demo")

      def clickAndCheck(propertyName: String, propertyIdx: Int) = {
        val radioLabel = radioButtons.findElement(By.id(s"radio-label-$propertyName"))
        radioLabel.click()
        eventually {
          radioButtons.findElements(new ByClassName("radio-buttons-demo-fruits")).asScala
            .foreach { el =>
              el.getText should be(propertyName)
            }
        }
      }

      val options = Seq("Apple", "Orange", "Banana").zipWithIndex
      for (_ <- 1 to 15) {
        val (name, idx) = Random.shuffle(options).head
        clickAndCheck(name, idx)
      }
    }

    "contain working select demo" in {
      val selectDemo = driver.findElementById("select-demo")

      def clickAndCheck(propertyName: String, propertyIdx: Int) = {
        val select = selectDemo.findElement(new ByTagName("select"))
        val option = select.findElement(new ByCssSelector(s"[value='$propertyIdx']"))
        option.click()
        eventually {
          selectDemo.findElements(new ByClassName("select-demo-fruits")).asScala
            .foreach { el =>
              el.getText should be(propertyName)
            }
          selectDemo.findElements(new ByTagName(s"select")).asScala
            .foreach { el =>
              val inputSelected = el.findElement(new ByCssSelector(s"[value='$propertyIdx']")).getAttribute("selected")
              val optionSelected = option.getAttribute("selected")
              inputSelected should be(optionSelected)
            }
        }
      }

      val options = Seq("Apple", "Orange", "Banana").zipWithIndex
      for (_ <- 1 to 15) {
        val (name, idx) = Random.shuffle(options).head
        clickAndCheck(name, idx)
      }
    }

    "contain working text area demo" in {
      val textAreaDemo = driver.findElementById("text-area-demo")

      def typeAndCheck(text: String) = {
        val textArea = textAreaDemo.findElement(new ByTagName("textarea"))
        textArea.clear()
        textArea.sendKeys(text)
        eventually {
          textAreaDemo.findElements(new ByTagName(s"textarea")).asScala
            .foreach { el =>
              el.getAttribute("value") should be(text)
            }
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
          inputsDemo.findElements(new ByCssSelector(s"input[type=$tpe]")).asScala
            .foreach { el =>
              el.getAttribute("value") should be(text)
            }
        }
      }

      for (_ <- 1 to 15) {
        typeAndCheck(Random.shuffle(Seq("Apple", "Orange", "Banana")).head, "text")
        typeAndCheck(Random.shuffle(Seq("Apple", "Orange", "Banana")).head, "password")
        typeAndCheck(Random.shuffle(Seq("123354", "-123", "32")).head, "number")
      }
    }

    "contain working range input demo" in {
      val demo = driver.findElementById("range-input-demo")

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
