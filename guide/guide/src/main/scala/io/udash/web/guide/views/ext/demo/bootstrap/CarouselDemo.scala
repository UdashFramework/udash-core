package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash._
import io.udash.bootstrap.button.{UdashButton, UdashButtonGroup, UdashButtonToolbar}
import io.udash.bootstrap.carousel.UdashCarousel.AnimationOptions
import io.udash.bootstrap.carousel.{UdashCarousel, UdashCarouselSlide}
import io.udash.css.CssView
import io.udash.logging.CrossLogging
import io.udash.properties.seq.SeqProperty
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom

import scala.language.postfixOps
import scala.util.Random

object CarouselDemo extends AutoDemo with CrossLogging with CssView {

  import JsDom.all._
  import io.udash.bootstrap.utils.BootstrapImplicits._

  private val (rendered, source) = {
    def randomString() = Random.nextLong().toString

    def newSlide(): UdashCarouselSlide = UdashCarouselSlide(
      Url("/assets/images/ext/bootstrap/carousel.jpg")
    )(
      h3(randomString()),
      p(randomString())
    )

    val slides = SeqProperty[UdashCarouselSlide](
      (1 to 5).map(_ => newSlide())
    )
    val active = Property(true)
    import scala.concurrent.duration._
    val carousel = UdashCarousel(
      slides = slides,
      activeSlide = Property(1),
      animationOptions = Property(AnimationOptions(
        interval = 2 seconds,
        keyboard = false,
        active = active.get
      ))
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
      div(
        UdashButtonToolbar()(
          UdashButton.toggle(active = active)(
            "Run animation"
          ).render,
          UdashButtonGroup()(
            prevButton.render,
            nextButton.render
          ).render,
          UdashButtonGroup()(
            prependButton.render,
            appendButton.render
          ).render
        )
      ),
      div(carousel)
    )
  }.withSourceCode

  override protected def demoWithSource(): (JsDom.all.Modifier, Iterator[String]) = {
    (div(GuideStyles.frame)(rendered), source.lines.drop(1))
  }
}

