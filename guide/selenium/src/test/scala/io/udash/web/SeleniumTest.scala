package io.udash.web

import java.util.concurrent.TimeUnit

import com.avsystem.commons.spring.HoconBeanDefinitionReader
import io.udash.web.server.ApplicationServer
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.{Dimension, WebElement}
import org.scalatest.concurrent.Eventually
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, Matchers, WordSpec}
import org.springframework.context.support.GenericApplicationContext

trait ServerConfig {
  def init(): Unit
  def createUrl(part: String): String
  def destroy(): Unit
}

class ExternalServerConfig(urlPrefix: String) extends ServerConfig {
  require(!urlPrefix.endsWith("/"))

  override def createUrl(part: String): String = {
    require(part.startsWith("/"))
    urlPrefix + part
  }

  override def init(): Unit = {}
  override def destroy(): Unit = {}
}

class InternalServerConfig extends ServerConfig {
  val guideCtx = Launcher.createApplicationContext()

  override def init(): Unit =
    guideCtx.getBean(classOf[ApplicationServer]).start()

  override def destroy(): Unit = {
    guideCtx.getBean(classOf[ApplicationServer]).stop()
    guideCtx.close()
  }

  override def createUrl(part: String): String = {
    require(part.startsWith("/"))
    s"http://127.0.0.2:${guideCtx.getBean(classOf[ApplicationServer]).port}$part"
  }
}

abstract class SeleniumTest extends WordSpec with Matchers with BeforeAndAfterAll with BeforeAndAfterEach with Eventually {
  override implicit val patienceConfig: PatienceConfig = PatienceConfig(scaled(Span(10, Seconds)), scaled(Span(50, Millis)))

  private val testingCtx = createTestingContext()

  protected final val driver: RemoteWebDriver = testingCtx.getBean(classOf[RemoteWebDriver])
  driver.manage().timeouts().implicitlyWait(200, TimeUnit.MILLISECONDS)
  driver.manage().window().setSize(new Dimension(1440, 800))

  protected final def findElementById(id: String): WebElement = eventually {
    driver.findElementById(id)
  }

  protected def url: String

  private val server: ServerConfig = testingCtx.getBean(classOf[ServerConfig])

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
    driver.close()
  }

  private def createTestingContext(): GenericApplicationContext = {
    val ctx = new GenericApplicationContext()
    val bdr = new HoconBeanDefinitionReader(ctx)
    bdr.loadBeanDefinitions("testing_beans.conf")
    ctx.refresh()
    ctx
  }
}
