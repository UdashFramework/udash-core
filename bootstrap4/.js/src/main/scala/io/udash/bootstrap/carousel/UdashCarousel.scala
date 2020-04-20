package io.udash.bootstrap
package carousel

import com.avsystem.commons.misc._
import io.udash._
import io.udash.bindings.modifiers.Binding
import io.udash.bootstrap.carousel.UdashCarousel.AnimationOptions
import io.udash.bootstrap.carousel.UdashCarousel.AnimationOptions.PauseOption
import io.udash.bootstrap.carousel.UdashCarousel.CarouselEvent.Direction
import io.udash.bootstrap.utils.{BootstrapStyles, UdashBootstrapComponent}
import io.udash.properties.seq
import io.udash.wrappers.jquery.JQuery
import org.scalajs.dom.{Element, Node}
import scalatags.JsDom.all._

import scala.scalajs.js
import scala.scalajs.js.Dictionary
import scala.util.Try

final class UdashCarousel[ItemType, ElemType <: ReadableProperty[ItemType]] private(
  slides: seq.ReadableSeqProperty[ItemType, ElemType],
  showIndicators: ReadableProperty[Boolean],
  animationOptions: ReadableProperty[AnimationOptions],
  srTexts: Option[(ReadableProperty[String], ReadableProperty[String])],
  val activeSlide: Property[Int],
  override val componentId: ComponentId
)(
  slideContentFactory: (ElemType, Binding.NestedInterceptor) => Modifier
) extends UdashBootstrapComponent with Listenable {

  import UdashCarousel._
  import io.udash.bootstrap.utils.BootstrapStyles.Carousel
  import io.udash.bootstrap.utils.BootstrapTags._
  import io.udash.css.CssView._
  import io.udash.wrappers.jquery._

  override type EventType = CarouselEvent[ItemType, ElemType]

  if (activeSlide.get >= slides.size) activeSlide.set(slides.size - 1)
  if (activeSlide.get < 0) activeSlide.set(0)

  propertyListeners += slides.listenStructure { patch =>
    val active = activeSlide.get
    if (patch.idx <= active) {
      if (patch.idx + patch.removed.size <= active) {
        activeSlide.set(active - patch.removed.size + patch.added.size)
      } else {
        activeSlide.set(if (patch.idx != 0) patch.idx - 1 else 0)
      }
    }
  }

  override val render: Element = {
    def indicators(): Binding = {
      def indicator(index: Int) = li(
        dataTarget := s"#$componentId", dataSlideTo := index,
        nestedInterceptor(BootstrapStyles.active.styleIf(activeSlide.transform(_ == index)))
      )

      val indices = slides.transform(_.length)
      produce(indices) { length =>
        ol(Carousel.indicators)(
          (0 until length).map(indicator).render
        ).render
      }
    }

    val slidesComponent = div(Carousel.inner, role := "listbox")(
      nestedInterceptor(repeatWithIndex(slides) { (slide, idx, nested) =>
        div(
          BootstrapStyles.Carousel.item,
          nested(BootstrapStyles.active.styleIf(idx.combine(activeSlide)(_ == _)))
        )(
          slideContentFactory(slide, nested)
        ).render
      })
    ).render

    def extractEventData(ev: JQueryEvent): (Int, Direction) = {
      val idx = jQ(slidesComponent).children().index(ev.relatedTarget)
      val direction = Try {
        val directionString = ev.asInstanceOf[Dictionary[String]].apply("direction")

        if (directionString == "left") CarouselEvent.Direction.Left
        else if (directionString == "right") CarouselEvent.Direction.Right
        else CarouselEvent.Direction.Unknown
      }
      (idx, direction.getOrElse(CarouselEvent.Direction.Unknown))
    }

    val res: Element = div(componentId, Carousel.carousel, Carousel.slide)(
      nestedInterceptor(produceWithNested(showIndicators) {
        case (true, nested) => span(nested(indicators())).render
        case (false, _) => span().render
      }),
      slidesComponent,
      a(Carousel.controlPrev, Carousel.control, href := s"#$componentId", role := "button", dataSlide := "prev")(
        span(aria.label.bind(srTexts.map(_._1).getOrElse("Previous".toProperty)))(Carousel.controlPrevIcon)
      ),
      a(Carousel.controlNext, Carousel.control, href := s"#$componentId", role := "button", dataSlide := "next")(
        span(aria.label.bind(srTexts.map(_._2).getOrElse("Next".toProperty)))(Carousel.controlNextIcon)
      )
    ).render

    val jqCarousel = jQ(res).asInstanceOf[UdashCarouselJQuery]
    nestedInterceptor(new JQueryOnBinding(jqCarousel, "slide.bs.carousel", (_: Element, ev: JQueryEvent) => {
      val (idx, dir) = extractEventData(ev)
      fire(CarouselEvent(this, idx, dir, changed = false))
    }))
    nestedInterceptor(new JQueryOnBinding(jqCarousel, "slid.bs.carousel", (_: Element, ev: JQueryEvent) => {
      val (idx, dir) = extractEventData(ev)
      activeSlide.set(idx)
      fire(CarouselEvent(this, idx, dir, changed = true))
    }))

    propertyListeners += animationOptions.listen { animationOptions =>
      jqCarousel.carousel(animationOptions.native)
      if (!animationOptions.active) jqCarousel.carousel("pause")
    }

    res
  }

  /** Turn on slide transition. */
  def cycle(): Unit = jQSelector().carousel("cycle")

  /** Pause slide transition. */
  def pause(): Unit = jQSelector().carousel("pause")

  /**
    * Change active slide.
    *
    * @param slideNumber new active slide index
    */
  def goTo(slideNumber: Int): Unit = jQSelector().carousel(slideNumber)

  /** Change active slide to the next one (index order). */
  def nextSlide(): Unit = jQSelector().carousel("next")

  /** Change active slide to the previous one (index order). */
  def previousSlide(): Unit = jQSelector().carousel("prev")

  override def kill(): Unit = {
    super.kill()
    jQSelector().carousel("dispose")
  }

  private def jQSelector(): UdashCarouselJQuery =
    jQ(render).asInstanceOf[UdashCarouselJQuery]
}

object UdashCarousel {
  /**
   * Creates a carousel component.
   * More: <a href="http://getbootstrap.com/docs/4.1/components/carousel/">Bootstrap Docs</a>.
   *
   * @param slides              A SeqProperty of carousel slides.
   * @param showIndicators      If true, the component shows carousel slide indicators.
   * @param animationOptions    A carousel animation options.
   * @param srTexts             Optional properties for previous and next arrows aria.label texts.
   * @param activeSlide         An active carousel slide index.
   * @param componentId         The arousel DOM element id.
   * @param slideContentFactory Creates content of a slide.
   *                            Use the provided interceptor to properly clean up bindings inside the content.
   * @tparam ItemType A single element's type in the `items` sequence.
   * @tparam ElemType A type of a property containing an element in the `items` sequence.
   * @return A `UdashCarousel` component, call `render` to create a DOM element representing this button.
   */
  def apply[ItemType, ElemType <: ReadableProperty[ItemType]](
    slides: seq.ReadableSeqProperty[ItemType, ElemType],
    showIndicators: ReadableProperty[Boolean] = UdashBootstrap.True,
    animationOptions: ReadableProperty[AnimationOptions] = AnimationOptions().toProperty,
    srTexts: Option[(ReadableProperty[String], ReadableProperty[String])] = None,
    activeSlide: Property[Int] = Property(0),
    componentId: ComponentId = ComponentId.generate()
  )(
    slideContentFactory: (ElemType, Binding.NestedInterceptor) => Modifier
  ): UdashCarousel[ItemType, ElemType] = {
    new UdashCarousel(slides, showIndicators, animationOptions, srTexts, activeSlide, componentId)(slideContentFactory)
  }

  /**
   * Creates the UdashCarousel component consisting of `UdashCarouselSlide`.
   * More: <a href="http://getbootstrap.com/docs/4.1/components/carousel/">Bootstrap Docs</a>.
   *
   * @param slides              A SeqProperty of carousel slides.
   * @param showIndicators      If true, the component shows carousel slide indicators.
   * @param animationOptions    A carousel animation options.
   * @param srTexts             Optional properties for previous and next arrows aria.label texts.
   * @param activeSlide         An active carousel slide index.
   * @param componentId         The arousel DOM element id.
   * @param slideContentFactory Creates content of a slide.
   *                            Use the provided interceptor to properly clean up bindings inside the content.
   * @return A `UdashCarousel` component, call `render` to create a DOM element representing this button.
   */
  def default(
    slides: ReadableSeqProperty[UdashCarouselSlide],
    showIndicators: ReadableProperty[Boolean] = UdashBootstrap.True,
    animationOptions: ReadableProperty[AnimationOptions] = AnimationOptions().toProperty,
    srTexts: Option[(ReadableProperty[String], ReadableProperty[String])] = None,
    activeSlide: Property[Int] = Property(0),
    componentId: ComponentId = ComponentId.generate()
  )(
    slideContentFactory: (ReadableProperty[UdashCarouselSlide], Binding.NestedInterceptor) => Modifier =
    (slide, nested) => nested(produce(slide)(_.render))
  ): UdashCarousel[UdashCarouselSlide, ReadableProperty[UdashCarouselSlide]] = {
    new UdashCarousel(slides, showIndicators, animationOptions, srTexts, activeSlide, componentId)(slideContentFactory)
  }

  /**
   * Event emitted by [[UdashCarousel]] on slide change transition start
   *
   * @param source      The [[UdashCarousel]] emitting the event.
   * @param targetIndex The index of the slide source transitioned to.
   * @param direction   The animation direction. Either `CarouselEvent.Direction.Left` or `CarouselEvent.Direction.Right`.
   */
  final case class CarouselEvent[ItemType, ElemType <: ReadableProperty[ItemType]](
    source: UdashCarousel[ItemType, ElemType], targetIndex: Int, direction: Direction, changed: Boolean
  ) extends AbstractCase with ListenableEvent

  object CarouselEvent {
    /** Carousel animation direction. */
    final class Direction(implicit enumCtx: EnumCtx) extends AbstractValueEnum
    object Direction extends ValueEnumCompanion[Direction] {
      final val Left, Right: Value = new Direction
      /** Animation direction from carousel.js that neither left nor right. */
      final val Unknown: Value = new Direction
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
  case class AnimationOptions(
    interval: Duration = 5 seconds, pause: PauseOption = PauseOption.Hover, wrap: Boolean = true,
    keyboard: Boolean = true, active: Boolean = true
  ) {
    private[UdashCarousel] def native: CarouselOptionsJS = {
      val options = js.Object().asInstanceOf[CarouselOptionsJS]
      options.interval = interval.toMillis.toInt
      options.pause = pause.name
      options.wrap = wrap
      options.keyboard = keyboard
      options
    }
  }

  object AnimationOptions {
    final class PauseOption(name: String)(implicit enumCtx: EnumCtx) extends AbstractValueEnum

    object PauseOption extends AbstractValueEnumCompanion[PauseOption] {
      /** Pauses the cycling of the carousel on mouseenter and resumes the cycling of the carousel on mouseleave. */
      final val Hover: Value = new PauseOption("hover")
      final val False: Value = new PauseOption("false")
    }
  }
}

/**
  * [[UdashCarousel]] slide.
  *
  * @param imgSrc  Slide image source url.
  * @param caption Slide caption content.
  */
case class UdashCarouselSlide(imgSrc: Url)(caption: Modifier*) {
  import io.udash.css.CssView._

  lazy val render: Node = {
    Seq(
      img(src := imgSrc.value, BootstrapStyles.Sizing.width100),
      div(BootstrapStyles.Carousel.caption)(
        caption
      )
    ).render
  }
}