package io.udash.bootstrap.collapse

import io.udash._
import io.udash.testing.UdashFrontendTest
import io.udash.wrappers.jquery.jQ
import scalatags.JsDom.all._

class UdashAccordionTest extends UdashFrontendTest {

  "UdashAccordion component" should {
    val news = SeqProperty[String](Seq(
      "Title 1", "Title 2", "Title 3"
    ))

    "grant access to created UdashCollapse instances" in {
      val accordion = UdashAccordion(news)(
        (news) => span(news.get).render,
        (news) => span(news.get).render
      )

      val accordionElement = accordion.render
      jQ("body").append(accordionElement)

      news.elemProperties.forall(item =>
        accordion.collapseOf(item).isDefined
      ) should be(true)
    }
  }
}
