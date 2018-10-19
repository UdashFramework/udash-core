package io.udash.bootstrap.carousel

import io.udash._
import io.udash.bootstrap.carousel.UdashCarousel.CarouselEvent
import io.udash.bootstrap.utils.BootstrapStyles.Carousel
import io.udash.bootstrap.utils.{BootstrapStyles, BootstrapTags}
import io.udash.i18n.{Bundle, BundleHash, Lang, LocalTranslationProvider, TranslationKey}
import io.udash.properties.seq.SeqProperty
import io.udash.testing.AsyncUdashFrontendTest
import io.udash.wrappers.jquery._
import scalatags.JsDom.all._

import scala.concurrent.Future
import scala.util.Random

class UdashCarouselTest extends AsyncUdashFrontendTest {

  "UdashCarousel component" should {
    val omitUrl = Url("//:0")
    def newSlide() = UdashCarouselSlide(omitUrl)(BigInt.probablePrime(80, Random).toString(36))
    def slides() = SeqProperty((1 to 10).map(_ => newSlide()))

    "show indicators conditionally" in {
      val carousel1 = UdashCarousel.default(slides(), showIndicators = false.toProperty)()
      val carousel2 = UdashCarousel.default(slides(), showIndicators = true.toProperty)()
      jQ(carousel1.render).has(s".${BootstrapStyles.Carousel.indicators.className}").length shouldBe 0
      jQ(carousel2.render).has(s".${BootstrapStyles.Carousel.indicators.className}").length shouldBe 1
    }

    "select sensible first slide" in {
      val carousel4 = UdashCarousel.default(slides(), activeSlide = Property(4))()
      val carousel0 = UdashCarousel.default(slides(), activeSlide = Property(0))()
      val carousel11 = UdashCarousel.default(slides(), activeSlide = Property(11))()
      val carousel10 = UdashCarousel.default(slides(), activeSlide = Property(10))()
      val carousel9 = UdashCarousel.default(slides(), activeSlide = Property(9))()
      val carouselNegative = UdashCarousel.default(slides(), activeSlide = Property(-4))()
      val carouselEmpty = UdashCarousel.default(SeqProperty.blank, activeSlide = Property(7))()
      carousel4.activeSlide.get shouldBe 4
      carousel0.activeSlide.get shouldBe 0
      carousel11.activeSlide.get shouldBe 9
      carousel10.activeSlide.get shouldBe 9
      carousel9.activeSlide.get shouldBe 9
      carouselNegative.activeSlide.get shouldBe 0
      carouselEmpty.activeSlide.get shouldBe 0
    }

    "make first slide active" in {
      val sl = slides()
      val carousel = UdashCarousel.default(
        sl,
        activeSlide = Property(3),
        animationOptions = UdashCarousel.AnimationOptions(active = false).toProperty
      )()
      jQ("body").append(carousel.render)
      carousel.activeSlide.get shouldBe 3
      activeIdx(carousel) shouldBe 3
      activeIndicatorIdx(carousel) shouldBe 3
    }

    "clean up listeners" in {
      val sl = slides()
      val activeSlide = Property(3)
      val options = UdashCarousel.AnimationOptions(active = false).toProperty
      val carousel = UdashCarousel.default(
        sl,
        activeSlide = activeSlide,
        animationOptions = options
      )()
      jQ("body").append(carousel.render)

      carousel.kill()
      sl.listenersCount() should be(0)
      sl.structureListenersCount() should be(0)
      sl.elemProperties.foreach(_.listenersCount() should be(0))
      activeSlide.listenersCount() should be(0)
      options.listenersCount() should be(0)
    }

    //For some reason, the 3 tests below have to be independent (can't first goTo() then nextSlide() without timeout)
    //This does not seem to cause any problems with carousels in the wild (outside tests).

    "go to slide in" in {
      val sl = slides()
      val carousel = UdashCarousel.default(
        sl,
        activeSlide = Property(1),
        animationOptions = UdashCarousel.AnimationOptions(active = false).toProperty
      )()
      jQ("body").append(carousel.render)
      carousel.activeSlide.get shouldBe 1
      carousel.goTo(5)
      retrying(carousel.activeSlide.get shouldBe 5)
      retrying(activeIdx(carousel) shouldBe 5)
      retrying(activeIndicatorIdx(carousel) shouldBe 5)
    }

    "move to next slide" in {
      val sl = slides()
      val carousel = UdashCarousel.default(
        sl,
        activeSlide = Property(1),
        animationOptions = UdashCarousel.AnimationOptions(active = false).toProperty
      )()
      jQ("body").append(carousel.render)
      carousel.activeSlide.get shouldBe 1
      carousel.nextSlide()
      retrying(carousel.activeSlide.get shouldBe 2)
      retrying(activeIdx(carousel) shouldBe 2)
      retrying(activeIndicatorIdx(carousel) shouldBe 2)
    }

    "move to previous slide" in {
      val sl = slides()
      val carousel = UdashCarousel.default(
        sl,
        activeSlide = Property(1),
        animationOptions = UdashCarousel.AnimationOptions(active = false).toProperty
      )()
      jQ("body").append(carousel.render)
      carousel.activeSlide.get shouldBe 1
      carousel.previousSlide()
      retrying(carousel.activeSlide.get shouldBe 0)
      retrying(activeIdx(carousel) shouldBe 0)
      retrying(activeIndicatorIdx(carousel) shouldBe 0)
    }

    "emit slide change events" in {
      val carousel = UdashCarousel.default(
        slides(),
        activeSlide = Property(1),
        animationOptions = UdashCarousel.AnimationOptions(active = false).toProperty
      )()
      jQ("body").append(carousel.render)
      carousel.activeSlide.get shouldBe 1
      var changeEvent: CarouselEvent[UdashCarouselSlide, ReadableProperty[UdashCarouselSlide]] = null
      var changedEvent: CarouselEvent[UdashCarouselSlide, ReadableProperty[UdashCarouselSlide]] = null
      carousel.listen {
        case ev@CarouselEvent(_, _, _, false) =>
          changeEvent = ev.asInstanceOf[CarouselEvent[UdashCarouselSlide, ReadableProperty[UdashCarouselSlide]]]
        case ev@CarouselEvent(_, _, _, true) =>
          changedEvent = ev.asInstanceOf[CarouselEvent[UdashCarouselSlide, ReadableProperty[UdashCarouselSlide]]]
      }
      carousel.goTo(5)
      retrying(changeEvent shouldBe CarouselEvent(carousel, 5, CarouselEvent.Direction.Left, false))
      retrying(changedEvent shouldBe CarouselEvent(carousel, 5, CarouselEvent.Direction.Left, true))
      retrying(carousel.activeSlide.get shouldBe 5)
    }

    "maintain activeIndex property in proper state when prepending" in {
      val sl = slides()
      val carousel = UdashCarousel.default(
        sl,
        activeSlide = Property(3),
        animationOptions = UdashCarousel.AnimationOptions(active = false).toProperty
      )()
      jQ("body").append(carousel.render)
      carousel.activeSlide.get shouldBe 3
      sl.prepend(newSlide())
      retrying(carousel.activeSlide.get shouldBe 4)
    }

    "translate aria.label arrow descriptions" in {
      val tp = new LocalTranslationProvider(
        Map(
          Lang("test") -> Bundle(BundleHash("h"), Map("prev" -> "Poprzedni", "next" -> "Następny")),
          Lang("test2") -> Bundle(BundleHash("h"), Map("prev" -> "Prev", "next" -> "next"))
        )
      )
      val lang = Property(Lang("test"))

      val sl = slides()
      val carousel = UdashCarousel.default(
        sl,
        srTexts = Some((
          TranslationKey.key("prev"),
          TranslationKey.key("next"),
          lang, tp
        )),
        animationOptions = UdashCarousel.AnimationOptions(active = false).toProperty
      )()
      val el = carousel.render
      jQ("body").append(el)

      for {
        _ <- retrying {
          el.getElementsByClassName(Carousel.controlPrevIcon.className)(0).getAttribute(aria.label.name) should be("Poprzedni")
          el.getElementsByClassName(Carousel.controlNextIcon.className)(0).getAttribute(aria.label.name) should be("Następny")
        }
        _ <- Future {
          lang.set(Lang("test2"))
        }
        r <- retrying {
          el.getElementsByClassName(Carousel.controlPrevIcon.className)(0).getAttribute(aria.label.name) should be("Prev")
          el.getElementsByClassName(Carousel.controlNextIcon.className)(0).getAttribute(aria.label.name) should be("next")
        }
      } yield r
    }
  }

  private def activeIdx(carousel: UdashCarousel[_, _]): Int = {
    jQ(carousel.render)
      .find(s".${BootstrapStyles.Carousel.item.className}").get
      .zipWithIndex
      .collectFirst { case (el, idx) if el.classList.contains(BootstrapStyles.active.className) => idx }
      .getOrElse(-1)
  }

  private def activeIndicatorIdx(carousel: UdashCarousel[_, _]): Int = {
    val indicators = jQ(carousel.render).find(s".${BootstrapStyles.Carousel.indicators.className}").children()
    (0 until indicators.length)
      .map(indicators.at)
      .find(_.hasClass(BootstrapStyles.active.className)).get
      .attr(BootstrapTags.dataSlideTo.name).get
      .toInt
  }

}
