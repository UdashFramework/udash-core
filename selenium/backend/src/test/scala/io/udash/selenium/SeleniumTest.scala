package io.udash.selenium

import java.util.concurrent.TimeUnit

import io.udash.selenium.server.ApplicationServer
import org.openqa.selenium.Dimension
import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}
import org.openqa.selenium.remote.RemoteWebDriver
import org.scalatest.concurrent.Eventually
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}

abstract class SeleniumTest extends WordSpec with Matchers with BeforeAndAfterAll with Eventually {
  override implicit val patienceConfig: PatienceConfig =
    PatienceConfig(scaled(Span(10, Seconds)), scaled(Span(50, Millis)))

  val driver: RemoteWebDriver = new ChromeDriver(new ChromeOptions().setHeadless(true))
  driver.manage().timeouts().implicitlyWait(200, TimeUnit.MILLISECONDS)
  driver.manage().window().setSize(new Dimension(1440, 800))

  private val server: ApplicationServer = new ApplicationServer(9999, "selenium/frontend/target/UdashStatics/WebContent")
  server.start()

  def createUrl(path: String): String = {
    require(path.startsWith("/"))
    s"http://127.0.0.1:${server.port}$path"
  }

  override protected def afterAll(): Unit = {
    server.stop()
    driver.close()
    super.afterAll()
  }
}
