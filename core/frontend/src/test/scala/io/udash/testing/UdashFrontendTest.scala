package io.udash.testing

import com.github.ghik.silencer.silent
import org.scalajs.dom
import org.scalatest.{Assertion, Succeeded}
import org.scalatest.concurrent.PatienceConfiguration
import org.scalatest.time.{Millis, Span}

import scala.concurrent.{Future, Promise}
import scala.scalajs.concurrent.JSExecutionContext
import scala.scalajs.js.Date
import scala.util.{Failure, Success}

trait FrontendTestUtils {
  import scalatags.JsDom.all.div
  def emptyComponent() = div().render

  @silent
  implicit val testExecutionContext = JSExecutionContext.runNow
}

trait UdashFrontendTest extends UdashSharedTest with FrontendTestUtils
trait AsyncUdashFrontendTest extends AsyncUdashSharedTest with FrontendTestUtils with PatienceConfiguration {
  case object EventuallyTimeout extends Exception

  override implicit val patienceConfig = PatienceConfig(scaled(Span(5000, Millis)), scaled(Span(100, Millis)))

  def eventually(code: => Any)(implicit patienceConfig: PatienceConfig): Future[Assertion] = {
    val start = Date.now()
    val p = Promise[Assertion]
    def startTest(): Unit = {
      dom.window.setTimeout(() => {
        if (patienceConfig.timeout.toMillis > Date.now() - start) {
          try {
            code
            p.complete(Success(Succeeded))
          } catch {
            case _: Exception => startTest()
          }
        } else {
          p.complete(Failure(EventuallyTimeout))
        }
      }, patienceConfig.interval.toMillis)
    }
    startTest()
    p.future
  }
}