package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash.css.CssView
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

object CarouselDemo extends AutoDemo with CssView {

  private val (rendered, source) = {
    import io.udash._
    import io.udash.bootstrap._
    import io.udash.bootstrap.button.{UdashButton, UdashButtonGroup, UdashButtonToolbar}
    import io.udash.bootstrap.carousel.UdashCarousel.AnimationOptions
    import io.udash.bootstrap.carousel.{UdashCarousel, UdashCarouselSlide}
    import scalatags.JsDom.all._

    import scala.concurrent.duration._
    import scala.util.Random

    def randomString(): String = {
      Random.nextLong().toString
    }

    def newSlide(): UdashCarouselSlide = {
      UdashCarouselSlide(
        Url("/assets/images/ext/bootstrap/carousel.jpg")
      )(
        h3(randomString()),
        p(randomString())
      )
    }

    val slides = SeqProperty((1 to 5).map(_ => newSlide()))
    val active = Property(false)
    val animationOptions = active.transform(a => AnimationOptions(
      interval = 2.seconds,
      keyboard = false,
      active = a
    ))

    val carousel = UdashCarousel(
      slides = slides,
      activeSlide = Property(1),
      animationOptions = animationOptions
    ) { case (slide, nested) => nested(produce(slide)(_.render)) }

    val prevButton = UdashButton()("Prev")
    val nextButton = UdashButton()("Next")
    val prependButton = UdashButton()("Prepend")
    val appendButton = UdashButton()("Append")

    prevButton.listen { case _ => carousel.previousSlide() }
    nextButton.listen { case _ => carousel.nextSlide() }
    prependButton.listen { case _ => slides.prepend(newSlide()) }
    appendButton.listen { case _ => slides.append(newSlide()) }

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
      div(carousel.render)
    ).render
  }.withSourceCode

  override protected def demoWithSource(): (Modifier, String) =
    (rendered.setup(_.applyTags(GuideStyles.frame)), source)
}

