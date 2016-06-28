package io.udash.bootstrap.carousel

import io.udash._
import io.udash.bootstrap.carousel.UdashCarousel.{CarouselEvent, SlideChangeEvent, SlideChangedEvent}
import io.udash.bootstrap.{BootstrapStyles, BootstrapTags}
import io.udash.properties.SeqProperty
import io.udash.testing.AsyncUdashFrontendTest
import io.udash.wrappers.jquery._

import scala.language.postfixOps
import scala.util.Random
import scalatags.JsDom.all._

class UdashCarouselTest extends AsyncUdashFrontendTest {

  "UdashCarousel component" should {
    val omitUrl = Url("//:0")
    def newSlide() = UdashCarouselSlide(omitUrl)(BigInt.probablePrime(80, Random).toString(36))
    def slides() = SeqProperty((1 to 10).map(_ => newSlide()))

    "show indicators conditionally" in {
      val carousel1 = UdashCarousel(slides(), showIndicators = false)
      val carousel2 = UdashCarousel(slides(), showIndicators = true)
      jQ(carousel1.render).has(s".${BootstrapStyles.Carousel.carouselIndicators.cls}").length shouldBe 0
      jQ(carousel2.render).has(s".${BootstrapStyles.Carousel.carouselIndicators.cls}").length shouldBe 1
    }

    "select sensible first slide" in {
      val carousel4 = UdashCarousel(slides(), activeSlide = 4)
      val carousel0 = UdashCarousel(slides(), activeSlide = 0)
      val carousel11 = UdashCarousel(slides(), activeSlide = 11)
      val carousel10 = UdashCarousel(slides(), activeSlide = 10)
      val carousel9 = UdashCarousel(slides(), activeSlide = 9)
      an[IllegalArgumentException] shouldBe thrownBy(UdashCarousel(slides(), activeSlide = -1))
      carousel4.activeIndex.get shouldBe 4
      carousel0.activeIndex.get shouldBe 0
      carousel11.activeIndex.get shouldBe 9
      carousel10.activeIndex.get shouldBe 9
      carousel9.activeIndex.get shouldBe 9
    }

    "make first slide active" in {
      val sl = slides()
      val carousel = UdashCarousel(sl, activeSlide = 3, animationOptions = UdashCarousel.AnimationOptions(active = false))
      jQ("body").append(carousel.render)
      carousel.activeIndex.get shouldBe 3
      activeIdx(sl.get) shouldBe 3
      activeIndicatorIdx(carousel) shouldBe 3
    }

    //For some reason, the 3 tests below have to be independent (can't first goTo() then nextSlide() without timeout)
    //This does not seem to cause any problems with carousels in the wild (outside tests).

    "go to slide in" in {
      val sl = slides()
      val carousel = UdashCarousel(sl, activeSlide = 1, animationOptions = UdashCarousel.AnimationOptions(active = false))
      jQ("body").append(carousel.render)
      carousel.activeIndex.get shouldBe 1
      carousel.goTo(5)
      eventually(carousel.activeIndex.get shouldBe 5)
      eventually(activeIdx(sl.get) shouldBe 5)
      eventually(activeIndicatorIdx(carousel) shouldBe 5)
    }

    "move to next slide" in {
      val sl = slides()
      val carousel = UdashCarousel(sl, activeSlide = 1, animationOptions = UdashCarousel.AnimationOptions(active = false))
      jQ("body").append(carousel.render)
      carousel.activeIndex.get shouldBe 1
      carousel.nextSlide()
      eventually(carousel.activeIndex.get shouldBe 2)
      eventually(activeIdx(sl.get) shouldBe 2)
      eventually(activeIndicatorIdx(carousel) shouldBe 2)
    }

    "move to previous slide" in {
      val sl = slides()
      val carousel = UdashCarousel(sl, activeSlide = 1, animationOptions = UdashCarousel.AnimationOptions(active = false))
      jQ("body").append(carousel.render)
      carousel.activeIndex.get shouldBe 1
      carousel.previousSlide()
      eventually(carousel.activeIndex.get shouldBe 0)
      eventually(activeIdx(sl.get) shouldBe 0)
      eventually(activeIndicatorIdx(carousel) shouldBe 0)
    }

    "emit slide change events" in {
      val carousel = UdashCarousel(slides(), activeSlide = 1, animationOptions = UdashCarousel.AnimationOptions(active = false))
      jQ("body").append(carousel.render)
      carousel.activeIndex.get shouldBe 1
      var changeEvent: SlideChangeEvent = null
      var changedEvent: SlideChangedEvent = null
      carousel.listen {
        case ev: SlideChangeEvent => changeEvent = ev
        case ev: SlideChangedEvent => changedEvent = ev
      }
      carousel.goTo(5)
      eventually(changeEvent shouldBe SlideChangeEvent(carousel, 5, CarouselEvent.Left))
      eventually(changedEvent shouldBe SlideChangedEvent(carousel, 5, CarouselEvent.Left))
      eventually(carousel.activeIndex.get shouldBe 5)
    }

    "maintain activeIndex property in proper state when prepending" in {
      val sl = slides()
      val carousel = UdashCarousel(sl, activeSlide = 3, animationOptions = UdashCarousel.AnimationOptions(active = false))
      jQ("body").append(carousel.render)
      carousel.activeIndex.get shouldBe 3
      sl.prepend(newSlide())
      eventually(carousel.activeIndex.get shouldBe 4)
    }

  }

  private def activeIdx(sls: Seq[UdashCarouselSlide]): Int =
    sls.zipWithIndex.collectFirst {
      case (sl, idx) if jQ(sl.render).hasClass(BootstrapStyles.active.cls) => idx
    }.get

  private def activeIndicatorIdx(carousel: UdashCarousel): Int = {
    val indicators = jQ(carousel.render).find(s".${BootstrapStyles.Carousel.carouselIndicators.cls}").children()
    (0 until indicators.length)
      .map(indicators.at)
      .find(_.hasClass(BootstrapStyles.active.cls)).get
      .attr(BootstrapTags.dataSlideTo.name).get
      .toInt
  }

}
