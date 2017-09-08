package io.udash.bootstrap
package carousel

import io.udash._
import io.udash.bootstrap.UdashBootstrap.ComponentId
import io.udash.bootstrap.carousel.UdashCarousel.AnimationOptions.PauseOption
import io.udash.bootstrap.carousel.UdashCarousel.CarouselEvent.Direction
import io.udash.bootstrap.carousel.UdashCarousel.{AnimationOptions, CarouselEvent}
import io.udash.bootstrap.utils.Icons
import io.udash.properties.PropertyCreator
import io.udash.wrappers.jquery.JQuery
import org.scalajs.dom
import org.scalajs.dom.Element

import scala.concurrent.ExecutionContext
import scala.language.postfixOps
import scala.scalajs.js
import scala.scalajs.js.Dictionary
import scala.util.Try
import scalatags.JsDom.all._

final class UdashCarousel private(content: ReadableSeqProperty[UdashCarouselSlide], val componentId: ComponentId,
                                  showIndicators: Boolean, activeSlide: Int, animationOptions: AnimationOptions)
                                 (implicit ec: ExecutionContext)
  extends UdashBootstrapComponent with Listenable[UdashCarousel, CarouselEvent] {

  import BootstrapStyles.Carousel._
  import BootstrapTags._
  import UdashCarousel._
  import io.udash.wrappers.jquery._
  import io.udash.css.CssView._

  require(activeSlide >= 0, "Active slide index cannot be negative.")

  private val indices = content.transform((slides: Seq[UdashCarouselSlide]) => slides.length)
  private val _activeIndex: Property[Int] = Property[Int](firstActive)

  content.listen(slides => _activeIndex.set(slides.zipWithIndex.collectFirst {
    case (sl, idx) if jQ(sl.render).hasClass(BootstrapStyles.active.className) => idx
  }.get))

  override val render: Element = {
    def indicators() = {
      def indicator(index: Int) =
        li(
          dataTarget := s"#$componentId", dataSlideTo := index,
          BootstrapStyles.active.styleIf(activeIndex.transform((idx: Int) => idx == index))
        )

      produce(indices)(length =>
        ol(carouselIndicators)(
          (0 until length).map(indicator).render
        ).render
      )
    }

    def extractEventData(ev: JQueryEvent): (Int, Direction) = {
      val idx = content.get.iterator.zipWithIndex.collectFirst {
        case (sl: UdashCarouselSlide, idx: Int) if sl.render == ev.relatedTarget => idx
      }.get
      val direction = Try {
        val directionString = ev.asInstanceOf[Dictionary[String]].apply("direction")
        if (directionString == "left") CarouselEvent.Direction.Left else if (directionString == "right") CarouselEvent.Direction.Right else CarouselEvent.Direction.Unknown
      }
      (idx, direction.getOrElse(CarouselEvent.Direction.Unknown))
    }


    val counter = new Countdown(firstActive)
    val res = div(id := componentId, carousel, slide)(
      if (showIndicators) indicators() else {},
      div(carouselInner, role := "listbox")(
        repeat(content) { slide =>
          val res = slide.get.render
          if (counter.left() == 0) BootstrapStyles.active.applyTo(res)
          res
        }
      ),
      a(BootstrapStyles.left, carouselControl, href := s"#$componentId", role := "button", dataSlide := "prev")(
        span(Icons.Glyphicon.chevronLeft),
        span(`class` := "sr-only", "Previous")
      ),
      a(BootstrapStyles.right, carouselControl, href := s"#$componentId", role := "button", dataSlide := "next")(
        span(Icons.Glyphicon.chevronRight),
        span(`class` := "sr-only", "Next")
      )
    ).render
    val jqCarousel = jQ(res).asCarousel()
    jqCarousel.on("slide.bs.carousel", (_: dom.Element, ev: JQueryEvent) => {
      val (idx, dir) = extractEventData(ev)
      _activeIndex.set(idx)
      fire(SlideChangeEvent(this, idx, dir))
    })
    jqCarousel.on("slid.bs.carousel", (_: dom.Element, ev: JQueryEvent) => {
      val (idx, dir) = extractEventData(ev)
      _activeIndex.set(idx)
      fire(SlideChangedEvent(this, idx, dir))
    })
    jqCarousel.carousel(animationOptions)
    if (!animationOptions.active) jqCarousel.pause()
    res
  }

  /**
    * @return Property containing active slide index
    */
  def activeIndex: ReadableProperty[Int] = _activeIndex.transform(identity)

  /**
    * Turn on slide transition.
    */
  def cycle(): Unit = jQSelector().cycle()

  /**
    * Pause slide transition.
    */
  def pause(): Unit = jQSelector().pause()

  /**
    * Change active slide.
    *
    * @param slideNumber new active slide index
    */
  def goTo(slideNumber: Int): Unit = jQSelector().goTo(slideNumber)

  /**
    * Change active slide to the next one (index order).
    */
  def nextSlide(): Unit = jQSelector().nextSlide()

  /**
    * Change active slide to the previous one (index order).
    */
  def previousSlide(): Unit = jQSelector().previousSlide()

  private def jQSelector(): UdashCarouselJQuery = jQ(render).asCarousel()

  private def firstActive: Int = math.min(activeSlide, content.length - 1)

  private class Countdown(private var ticks: Int) {
    def left(): Int =
      if (ticks > -1) {
        val res = ticks
        ticks -= 1
        res
      } else ticks
  }


}

object UdashCarousel {

  /**
    * Creates the UdashCarousel component.
    * More: <a href="http://getbootstrap.com/javascript/#carousel">Bootstrap Docs</a>.
    *
    * @param content          SeqProperty of carousel slides.
    * @param componentId      Carousel div ID.
    * @param showIndicators   Show carousel slide indicators.
    * @param activeSlide      Initially active carousel slide.
    * @param animationOptions Carousel animation options.
    * @param ec               ExecutionContext for carousel internal properties
    * @return `UdashCarousel` component
    */
  def apply(content: ReadableSeqProperty[UdashCarouselSlide], componentId: ComponentId = UdashBootstrap.newId(),
            showIndicators: Boolean = true, activeSlide: Int = 0, animationOptions: AnimationOptions = AnimationOptions())
           (implicit ec: ExecutionContext): UdashCarousel =
    new UdashCarousel(content, componentId, showIndicators, activeSlide, animationOptions)

  /**
    * Event hierarchy for [[UdashCarousel]]-emitted events.
    */
  sealed trait CarouselEvent extends ListenableEvent[UdashCarousel] {
    /**
      * @return The index of the slide source transitioned to. Either [[CarouselEvent.Direction.Left]] or [[CarouselEvent.Direction.Right]].
      */
    def targetIndex: Int

    /**
      * @return The animation direction
      */
    def direction: Direction
  }

  /**
    * Event emitted by [[UdashCarousel]] on slide change transition start
    *
    * @param source      The [[UdashCarousel]] emitting the event.
    * @param targetIndex The index of the slide source transitioned to.
    * @param direction   The animation direction. Either [[CarouselEvent.Direction.Left]] or [[CarouselEvent.Direction.Right]].
    */
  case class SlideChangeEvent(source: UdashCarousel, targetIndex: Int, direction: Direction) extends CarouselEvent

  /**
    * Event emitted by [[UdashCarousel]] on slide change transition finish.
    *
    * @param source      The [[UdashCarousel]] emitting the event.
    * @param targetIndex The index of the slide source transitioned to.
    * @param direction   The animation direction. Either [[CarouselEvent.Direction.Left]] or [[CarouselEvent.Direction.Right]].
    */
  case class SlideChangedEvent(source: UdashCarousel, targetIndex: Int, direction: Direction) extends CarouselEvent

  object CarouselEvent {

    /**
      * Carousel animation direction.
      */
    sealed trait Direction

    object Direction {

      case object Left extends Direction

      case object Right extends Direction

      /**
        * Animation direction from carousel.js that neither left nor right.
        */
      case object Unknown extends Direction
    }

  }

  @js.native
  private trait UdashCarouselJQuery extends JQuery {
    def carousel(options: CarouselOptionsJS): UdashCarouselJQuery = js.native

    def carousel(cmd: String): UdashCarouselJQuery = js.native

    def carousel(number: Int): UdashCarouselJQuery = js.native
  }

  import scala.concurrent.duration._

  @js.native
  private trait CarouselOptionsJS extends js.Object {
    var interval: Int = js.native
    var pause: String = js.native
    var wrap: Boolean = js.native
    var keyboard: Boolean = js.native
  }

  /**
    * [[UdashCarousel]] animation options.
    *
    * @param interval The amount of time to delay between automatically cycling an item.
    * @param pause    Indicated whether the carousel should pass on some specific event. See [[UdashCarousel.AnimationOptions.PauseOption]].
    * @param wrap     Should the carousel cycle continuously or have hard stops.
    * @param keyboard Should the carousel react to keyboard events.
    * @param active   Should the animation be active.
    */
  case class AnimationOptions(interval: Duration = 5 seconds, pause: PauseOption = PauseOption.Hover, wrap: Boolean = true,
                              keyboard: Boolean = true, active: Boolean = true) {
    private[UdashCarousel] def native: CarouselOptionsJS = {
      val options = js.Object().asInstanceOf[CarouselOptionsJS]
      options.interval = interval.toMillis.toInt
      options.pause = pause.raw
      options.wrap = wrap
      options.keyboard = keyboard
      options
    }
  }

  private implicit class UdashCarouselJQueryExt(jQ: JQuery) {
    def asCarousel(): UdashCarouselJQuery = jQ.asInstanceOf[UdashCarouselJQuery]
  }

  private implicit class UdashCarouselJQueryOps(jq: UdashCarouselJQuery) {

    def carousel(animationOptions: AnimationOptions): Unit = jq.carousel(animationOptions.native)

    def cycle(): Unit = jq.carousel("cycle")

    def pause(): Unit = jq.carousel("pause")

    def goTo(slideNumber: Int): Unit = jq.carousel(slideNumber)

    def nextSlide(): Unit = jq.carousel("next")

    def previousSlide(): Unit = jq.carousel("prev")

  }

  object AnimationOptions {

    sealed abstract class PauseOption(val raw: String)

    object PauseOption {
      /** Pauses the cycling of the carousel on mouseenter and resumes the cycling of the carousel on mouseleave. */
      case object Hover extends PauseOption("hover")
      case object False extends PauseOption("false")
    }
  }
}

/**
  * [[UdashCarousel]] slide.
  *
  * @param imgSrc  Slide image source url.
  * @param content Slide content.
  */
case class UdashCarouselSlide(imgSrc: Url, override val componentId: ComponentId = UdashBootstrap.newId())(content: Modifier*) extends UdashBootstrapComponent {
  import io.udash.css.CssView._
  override lazy val render: Element =
    div(id := componentId, BootstrapStyles.item)(
    img(src := imgSrc.value),
    div(BootstrapStyles.Carousel.carouselCaption)(
      content
    )
  ).render
}

object UdashCarouselSlide {
  implicit val pc: PropertyCreator[UdashCarouselSlide] = PropertyCreator.propertyCreator[UdashCarouselSlide]
  implicit val pcS: PropertyCreator[Seq[UdashCarouselSlide]] = PropertyCreator.propertyCreator[Seq[UdashCarouselSlide]]
  implicit val pcO: PropertyCreator[Option[UdashCarouselSlide]] = PropertyCreator.propertyCreator[Option[UdashCarouselSlide]]
}