package io.udash.testing

import com.avsystem.commons._
import org.scalajs.dom
import org.scalajs.dom.Element
import org.scalajs.dom.raw.DOMTokenList
import org.scalatest.enablers.Containing

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

trait AsyncUdashFrontendTest extends AsyncUdashSharedTest with FrontendTestUtils