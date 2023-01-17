package io.udash.testing

import com.avsystem.commons._
import org.scalactic.source.Position
import org.scalajs.dom
import org.scalajs.dom.{DOMTokenList, Element}
import org.scalatest.enablers.Containing
import org.scalatest.{Assertion, Succeeded}

import scala.concurrent.duration.FiniteDuration

trait FrontendTestUtils {
  def emptyComponent(): Element = dom.document.createElement("div")

  implicit val DOMTokensListContains: Containing[DOMTokenList] = new Containing[DOMTokenList] {
    override def contains(container: DOMTokenList, element: Any): Boolean = element match {
      case s: String => container.contains(s)
      case _ => false
    }

    override def containsOneOf(container: DOMTokenList, elements: BSeq[Any]): Boolean = elements.exists {
      case s: String => container.contains(s)
      case _ => false
    }

    override def containsNoneOf(container: DOMTokenList, elements: BSeq[Any]): Boolean = elements.forall {
      case s: String => container.contains(s)
      case _ => false
    }
  }
}

trait UdashFrontendTest extends UdashSharedTest with FrontendTestUtils

trait AsyncUdashFrontendTest extends AsyncUdashSharedTest with FrontendTestUtils {
  def waiting(code: => Any)(duration: FiniteDuration)(implicit pos: Position): Future[Assertion] = {
    val p = Promise[Assertion]()
    dom.window.setTimeout(() => {
      try {
        code
        p.complete(Success(Succeeded))
      } catch {
        case ex: Throwable =>
          p.complete(Failure(ex))
      }
    }, duration.toMillis.toDouble)

    p.future
  }
}