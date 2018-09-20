package io.udash.bootstrap.collapse

import io.udash._
import io.udash.wrappers.jquery._
import scalatags.JsDom.all._
import io.udash.testing.UdashFrontendTest

class UdashAccordionTest extends UdashFrontendTest {

  "UdashAccordion component" should {
    "grant access to created UdashCollapse instances" in {
      val news = SeqProperty[String](Seq(
        "Title 1", "Title 2", "Title 3"
      ))

      val accordion = UdashAccordion(news)(
        (news, nested) => span(">>", nested(bind(news))).render,
        (news, nested) => span(nested(bind(news))).render
      )

      val accordionElement = accordion.render
      jQ("body").append(accordionElement)

      accordionElement.textContent should be(">>Title 1Title 1>>Title 2Title 2>>Title 3Title 3")

      news.remove("Title 2")
      accordionElement.textContent should be(">>Title 1Title 1>>Title 3Title 3")

      news.elemProperties.forall(item => accordion.collapseOf(item).isDefined) should be(true)

      accordion.kill()
      news.listenersCount() should be(0)
      news.structureListenersCount() should be(0)
      news.elemProperties.foreach(_.listenersCount() should be(0))
    }
  }
}
