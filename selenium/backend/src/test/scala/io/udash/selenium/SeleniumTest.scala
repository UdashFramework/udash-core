package io.udash.selenium

import java.util.concurrent.TimeUnit

import io.udash.selenium.server.ApplicationServer
import org.openqa.selenium.Dimension
import org.openqa.selenium.firefox.{FirefoxDriver, FirefoxOptions}
import org.openqa.selenium.remote.RemoteWebDriver
import org.scalatest.concurrent.Eventually
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}

abstract class SeleniumTest extends WordSpec with Matchers with BeforeAndAfterAll with Eventually {
  override implicit val patienceConfig: PatienceConfig =
    PatienceConfig(scaled(Span(10, Seconds)), scaled(Span(5, Millis)))

  private var _driver: RemoteWebDriver = _
  def driver: RemoteWebDriver = _driver

  private val server: ApplicationServer = new ApplicationServer(9999, "selenium/frontend/target/UdashStatics/WebContent")

  def createUrl(path: String): String = {
    require(path.startsWith("/"))
    s"http://127.0.0.1:${server.port}$path"
  }

  override protected def beforeAll(): Unit = {
    super.beforeAll()
//    _driver = new ChromeDriver(new ChromeOptions().setHeadless(true))
    _driver = new FirefoxDriver(new FirefoxOptions().setHeadless(true))
    _driver.manage().timeouts().implicitlyWait(500, TimeUnit.MILLISECONDS)
    _driver.manage().window().setSize(new Dimension(1440, 800))

    server.start()
  }
  override protected def afterAll(): Unit = {
    server.stop()
    _driver.quit()
    _driver = null
    super.afterAll()
  }
}
