package io.udash.web.guide.views.ext.demo

import io.udash._
import io.udash.bootstrap._
import io.udash.bootstrap.button._
import io.udash.bootstrap.carousel.UdashCarousel.AnimationOptions
import io.udash.bootstrap.carousel.{UdashCarousel, UdashCarouselSlide}
import io.udash.bootstrap.collapse.{UdashAccordion, UdashCollapse}
import io.udash.css.CssView
import io.udash.logging.CrossLogging
import io.udash.properties.seq.SeqProperty
import io.udash.web.guide.styles.partials.GuideStyles
import org.scalajs.dom
import scalatags.JsDom

import scala.language.postfixOps
import scala.util.Random

object BootstrapDemos extends CrossLogging with CssView {

  import JsDom.all._
  import io.udash.web.guide.components.BootstrapUtils._

  def accordionCollapse(): dom.Element = {
    val events = SeqProperty.blank[UdashCollapse.CollapseEvent]
    val news = SeqProperty[String](
      "Title 1", "Title 2", "Title 3"
    )

    val accordion = UdashAccordion(news)(
      (news, _) => span(news.get).render,
      (_, _) => div(wellStyles)(ul(repeat(events)(event => li(event.get.toString).render))).render
    )

    val accordionElement = accordion.render
    news.elemProperties.map(accordion.collapseOf)
      .filter(_.isDefined)
      .foreach(_.get.listen { case ev => events.append(ev) })

    div(GuideStyles.frame)(accordionElement).render
  }

  def carousel(): dom.Element = {
    def newSlide(): UdashCarouselSlide = UdashCarouselSlide(
      Url("/assets/images/ext/bootstrap/carousel.jpg")
    )(
      h3(randomString()),
      p(randomString())
    )

    val slides = SeqProperty[UdashCarouselSlide]((1 to 5).map(_ => newSlide()))
    val active = Property(true)
    import scala.concurrent.duration._
    val carousel = UdashCarousel(
      slides = slides,
      activeSlide = Property(1),
      animationOptions = Property(AnimationOptions(interval = 2 seconds, keyboard = false, active = active.get))
    ) { case (slide, nested) => nested(produce(slide)(_.render)) }
    val prevButton = UdashButton()("Prev")
    val nextButton = UdashButton()("Next")
    val prependButton = UdashButton()("Prepend")
    val appendButton = UdashButton()("Append")
    prevButton.listen { case _ => carousel.previousSlide() }
    nextButton.listen { case _ => carousel.nextSlide() }
    prependButton.listen { case _ => slides.prepend(newSlide()) }
    appendButton.listen { case _ => slides.append(newSlide()) }
    active.listen(b => if (b) carousel.cycle() else carousel.pause())
    div(
      div(GuideStyles.frame)(
        UdashButtonToolbar()(
          UdashButton.toggle(active = active)("Run animation").render,
          UdashButtonGroup()(
            prevButton.render,
            nextButton.render
          ).render,
          UdashButtonGroup()(
            prependButton.render,
            appendButton.render
          ).render
        ).render
      ),
      div(
        carousel.render
      )
    ).render
  }

  private def randomString(): String = Random.nextLong().toString
}
