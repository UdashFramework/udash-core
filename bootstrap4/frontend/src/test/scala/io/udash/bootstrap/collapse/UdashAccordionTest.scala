package io.udash.bootstrap.collapse

import io.udash._
import io.udash.testing.AsyncUdashFrontendTest
import io.udash.wrappers.jquery._
import scalatags.JsDom.all._

import scala.collection.mutable
import scala.concurrent.Future

class UdashAccordionTest extends AsyncUdashFrontendTest {

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

      val removed = news.elemProperties(1)
      val removedCollapse = accordion.collapseOf(removed)
      news.remove("Title 2")
      accordionElement.textContent should be(">>Title 1Title 1>>Title 3Title 3")
      removedCollapse.get.listenersCount() should be(0)

      news.elemProperties.forall(item => accordion.collapseOf(item).isDefined) should be(true)
      accordion.collapseOf(removed).isDefined should be(false)

      val firstCollapse = accordion.collapseOf(news.elemProperties.head)
      accordion.kill()
      news.listenersCount() should be(0)
      news.structureListenersCount() should be(0)
      news.elemProperties.map(_.listenersCount()).sum should be(0)
      firstCollapse.get.listenersCount() should be(0)
    }

    "expose collapse events" in {
      val news = SeqProperty[String](Seq(
        "Title 1", "Title 2", "Title 3"
      ))

      val accordion = UdashAccordion(news)(
        (news, nested) => i(">>", nested(bind(news))).render,
        (news, nested) => span(nested(bind(news))).render
      )

      val events = mutable.ArrayBuffer.empty[UdashAccordion.AccordionEvent[String, _]]
      accordion.listen { case ev => events += ev }

      val accordionElement = accordion.render
      jQ("body").append(accordionElement)

      val headers = accordionElement.getElementsByTagName("i")
      headers.length should be(3)

      for {
        _ <- Future(jQ(headers(0)).trigger("click"))
        _ <- retrying {
          events.size should be(2)
          events(0).item should be("Title 1")
          events(0).idx should be(0)
          events(0).collapseEvent.tpe should be(UdashCollapse.CollapseEvent.EventType.Show)
          events(1).item should be("Title 1")
          events(1).idx should be(0)
          events(1).collapseEvent.tpe should be(UdashCollapse.CollapseEvent.EventType.Shown)
        }
        _ <- Future(jQ(headers(1)).trigger("click"))
        _ <- retrying {
          events.size should be(6)
          events(2).item should be("Title 2")
          events(2).idx should be(1)
          events(2).collapseEvent.tpe should be(UdashCollapse.CollapseEvent.EventType.Show)
          events(3).item should be("Title 1")
          events(3).idx should be(0)
          events(3).collapseEvent.tpe should be(UdashCollapse.CollapseEvent.EventType.Hide)
          events(4).item should be("Title 1")
          events(4).idx should be(0)
          events(4).collapseEvent.tpe should be(UdashCollapse.CollapseEvent.EventType.Hidden)
          events(5).item should be("Title 2")
          events(5).idx should be(1)
          events(5).collapseEvent.tpe should be(UdashCollapse.CollapseEvent.EventType.Shown)
        }
        r <- retrying {
          accordion.kill()
          news.listenersCount() should be(0)
          news.structureListenersCount() should be(0)
          news.elemProperties.map(_.listenersCount()).sum should be(0)
        }
      } yield r
    }
  }
}
