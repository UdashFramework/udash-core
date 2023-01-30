package io.udash.web

import io.github.bonigarcia.wdm.WebDriverManager
import org.openqa.selenium.firefox.{FirefoxDriver, FirefoxOptions}
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.{By, Dimension, WebElement}
import org.scalatest.concurrent.Eventually
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.time.Duration

private trait ServerConfig {
  def init(): Unit
  def createUrl(part: String): String
  def destroy(): Unit
}

// Doesn't launch embedded guide app server
private final class ExternalServerConfig(urlPrefix: String) extends ServerConfig {
  require(!urlPrefix.endsWith("/"))

  override def createUrl(part: String): String = {
    require(part.startsWith("/"))
    urlPrefix + part
  }

  override def init(): Unit = {}
  override def destroy(): Unit = {}
}

// Launches embedded guide server
private final class InternalServerConfig extends ServerConfig {
  private val server = Launcher.createApplicationServer()

  override def init(): Unit = server.start()

  override def destroy(): Unit = server.stop()

  override def createUrl(part: String): String = {
    require(part.startsWith("/"))
    s"http://127.0.0.2:${server.port}$part"
  }
}

abstract class SeleniumTest extends AnyWordSpec with Matchers with BeforeAndAfterAll with BeforeAndAfterEach with Eventually {
  override implicit val patienceConfig: PatienceConfig = PatienceConfig(scaled(Span(10, Seconds)), scaled(Span(50, Millis)))

  private val driverManager = WebDriverManager.firefoxdriver()
  driverManager.config().setServerPort(0)
  driverManager.setup()
  protected final val driver: RemoteWebDriver = new FirefoxDriver(new FirefoxOptions().addArguments("-headless"))
  driver.manage().timeouts().implicitlyWait(Duration.ofMillis(200))
  driver.manage().window().setSize(new Dimension(1440, 800))

  protected final def findElementById(id: String): WebElement = eventually {
    driver.findElement(By.id(id))
  }

  protected def url: String

  private val server: ServerConfig = new InternalServerConfig

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    server.init()
  }

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    driver.get(server.createUrl(url))
  }

  override protected def afterAll(): Unit = {
    super.afterAll()
    server.destroy()
    driver.quit()
    driverManager.quit()
  }
}
