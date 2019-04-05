package io.udash.web.guide.demos.ext


import com.avsystem.commons.jiop.JavaInterop._
import io.udash.web.SeleniumTest
import org.openqa.selenium.By.{ByCssSelector, ById}

class I18nDemosTest extends SeleniumTest {
  val url = "/ext/i18n"

  //todo switch to commented version when new rpc (and i18n.md) is used
  //private def runDemo(id: String)(additionalAction: WebElement => Unit = _ => ())(implicit position: Position): Map[String, String] = {
  //  def demo = {
  //    driver.switchTo().parentFrame()
  //    driver.findElementById(id)
  //  }
  //
  //  eventually {
  //    driver.switchTo().frame(demo.findElement(new ByTagName("iframe")))
  //    driver.findElement(new ById("run-icon")).click()
  //  }
  //
  //  eventually {
  //    driver.switchTo().frame(demo.findElement(new ByTagName("iframe")))
  //    driver.switchTo().frame(driver.findElement(new ById("codeframe")))
  //    val output = driver.findElementById("output")
  //    additionalAction(output)
  //    val translations = output.getText.split("\n").collect {
  //      case line if line.indexOf(":") != -1 => (line.take(line.indexOf(":")), line.drop(line.indexOf(":") + 2))
  //    }.toMap
  //    translations.size should be(6)
  //    translations
  //  }
  //}


  "I18n view" should {
    driver.get(server.createUrl(url))

    "contain demo elements" in {
      eventually {
        driver.findElementById("frontend-translations-demo")
        driver.findElementById("rpc-translations-demo")
        driver.findElementById("dynamic-rpc-translations-demo")
      }
    }

    "contain working frontend translations demo" in {
      driver.navigate().refresh()

      //runDemo("frontend-translations-demo")()
      def demo = driver.findElementById("frontend-translations-demo")

      eventually {
        val elements = demo.findElements(new ByCssSelector("li"))
        val translations: Map[String, String] = elements.asScala.map(item => {
          val parts = item.getText.split(":")
          (parts(0).trim, parts(1).trim)
        }).toMap[String, String]

        verifyEnTranslations(translations)
      }
    }

    "contain working remote translations demo" in {
      driver.navigate().refresh()

      //val translations = runDemo("rpc-translations-demo")()
      //verifyPlTranslations(translations)
      def demo = driver.findElementById("rpc-translations-demo")

      eventually {
        val elements = demo.findElements(new ByCssSelector("li"))
        val translations: Map[String, String] = elements.asScala.map(item => {
          val parts = item.getText.split(":")
          (parts(0).trim, parts(1).trim)
        }).toMap[String, String]

        verifyPlTranslations(translations)
      }
    }

    "contain working dynamic remote translations demo" in {
      driver.navigate().refresh()

      def demo = driver.findElementById("dynamic-rpc-translations-demo")

      val enButton = demo.findElement(new ById("enButton"))
      val plButton = demo.findElement(new ById("plButton"))

      def elements = demo.findElements(new ByCssSelector("li"))

      def translations: Map[String, String] = elements.asScala.map(item => {
        val parts = item.getText.split(":")
        (parts(0).trim, parts(1).trim)
      }).toMap[String, String]

      for (_ <- 1 to 5) {
        enButton.click()
        eventually {
          verifyEnTranslations(translations)
        }

        plButton.click()
        eventually {
          verifyPlTranslations(translations)
        }
      }
      //for (_ <- 1 to 5) {
      //  val enTranslations = runDemo("dynamic-rpc-translations-demo")(_.findElement(new ById("enButton")).click())
      //  eventually(verifyEnTranslations(enTranslations))
      //
      //  val plTranslations = runDemo("dynamic-rpc-translations-demo")(_.findElement(new ById("plButton")).click())
      //  eventually(verifyPlTranslations(plTranslations))
      //}
    }
  }

  def verifyEnTranslations(translations: Map[String, String]) = {
    translations("auth.loginLabel") should be("Username")
    translations("auth.passwordLabel") should be("Password")
    translations("auth.login.buttonLabel") should be("Sign in")
    translations("auth.login.retriesLeft") should be("3 retries left")
    translations("auth.login.retriesLeftOne") should be("1 retry left")
    translations("auth.register.buttonLabel") should be("Sign up")
  }

  def verifyPlTranslations(translations: Map[String, String]) = {
    translations("auth.loginLabel") should be("Nazwa użytkownika")
    translations("auth.passwordLabel") should be("Hasło")
    translations("auth.login.buttonLabel") should be("Zaloguj")
    translations("auth.login.retriesLeft") should be("Zostało kilka prób")
    translations("auth.login.retriesLeftOne") should be("Została ostatnia próba")
    translations("auth.register.buttonLabel") should be("Zarejestruj")
  }
}
