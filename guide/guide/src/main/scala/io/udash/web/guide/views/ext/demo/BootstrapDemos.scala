package io.udash.web.guide.views.ext.demo

import io.udash._
import io.udash.bindings.modifiers.Binding
import io.udash.bootstrap._
import io.udash.bootstrap.badge.UdashBadge
import io.udash.bootstrap.button._
import io.udash.bootstrap.card.UdashCard
import io.udash.bootstrap.carousel.UdashCarousel.AnimationOptions
import io.udash.bootstrap.carousel.{UdashCarousel, UdashCarouselSlide}
import io.udash.bootstrap.collapse.{UdashAccordion, UdashCollapse}
import io.udash.bootstrap.list.UdashListGroup
import io.udash.bootstrap.modal.UdashModal
import io.udash.bootstrap.tooltip.{UdashPopover, UdashTooltip}
import io.udash.bootstrap.utils._
import io.udash.css.CssView
import io.udash.logging.CrossLogging
import io.udash.properties.seq.SeqProperty
import io.udash.web.commons.styles.GlobalStyles
import io.udash.web.guide.styles.partials.GuideStyles
import org.scalajs.dom
import scalatags.JsDom

import scala.language.postfixOps
import scala.util.Random

object BootstrapDemos extends CrossLogging with CssView {

  import JsDom.all._
  import io.udash.web.guide.components.BootstrapUtils._
  import org.scalajs.dom._

  def listGroup(): dom.Element = {
    val news = SeqProperty[String]("Title 1", "Title 2", "Title 3")

    def newsStyle(newsProperty: Property[String]): ReadableProperty[String] = {
      newsProperty.transform(_.last match {
        case '1' => BootstrapStyles.active.className
        case '2' => BootstrapStyles.disabled.className
        case '3' => BootstrapStyles.List.color(BootstrapStyles.Color.Success).className
        case '4' => BootstrapStyles.List.color(BootstrapStyles.Color.Danger).className
        case '5' => BootstrapStyles.List.color(BootstrapStyles.Color.Info).className
        case '6' => BootstrapStyles.List.color(BootstrapStyles.Color.Warning).className
      })
    }
    val listGroup = UdashListGroup(news)((news, nested) =>
      li(nested(cls.bind(newsStyle(news))))(nested(bind(news))).render
    )

    var i = 1
    val appendHandler = window.setInterval(() => {
      news.append(s"Dynamic $i")
      i += 1
    }, 2000)
    window.setTimeout(() => window.clearInterval(appendHandler), 20000)

    div(GuideStyles.frame)(
      listGroup.render
    ).render
  }

  def cards(): dom.Element = {
    val news = SeqProperty[String]("Title 1", "Title 2", "Title 3")
    div(GuideStyles.frame)(
      UdashCard(
        borderColor = Some(BootstrapStyles.Color.Success).toProperty,
        textColor = Some(BootstrapStyles.Color.Primary).toProperty,
      )(factory => Seq(
        factory.header("Card heading"),
        factory.body("Some default panel content here. Nulla vitae elit libero, a pharetra augue. Aenean lacinia bibendum nulla sed consectetur. Aenean eu leo quam. Pellentesque ornare sem lacinia quam venenatis vestibulum. Nullam id dolor id nibh ultricies vehicula ut id elit."),
        factory.listGroup(nested => {
          val group = UdashListGroup(news)((news, nested) => li(nested(bind(news))).render)
          nested(group)
          group
        }),
        factory.footer("Card footer")
      )).render
    ).render
  }

  def responsiveEmbed(): dom.Element = {
    div(GuideStyles.frame)(
      div(BootstrapStyles.EmbedResponsive.responsive, BootstrapStyles.EmbedResponsive.embed16by9, GlobalStyles.smallMargin)(
        iframe(BootstrapStyles.EmbedResponsive.item, src := "http://www.youtube.com/embed/zpOULjyy-n8?rel=0")
      ),
      div(BootstrapStyles.EmbedResponsive.responsive, BootstrapStyles.EmbedResponsive.embed4by3, GlobalStyles.smallMargin)(
        iframe(BootstrapStyles.EmbedResponsive.item, src := "http://www.youtube.com/embed/zpOULjyy-n8?rel=0")
      )
    ).render
  }

  def simpleModal(): dom.Element = {
    val events = SeqProperty.blank[UdashModal.ModalEvent]
    val header = (_: Binding.NestedInterceptor) => div("Modal events").render
    val body = (nested: Binding.NestedInterceptor) => div(wellStyles, BootstrapStyles.Spacing.margin())(
      ul(nested(repeat(events)(event => li(event.get.toString).render)))
    ).render
    val footer = (_: Binding.NestedInterceptor) => div(
      UdashButton()(_ => Seq[Modifier](UdashModal.CloseButtonAttr, "Close")).render,
      UdashButton(buttonStyle = BootstrapStyles.Color.Primary.toProperty)("Something...").render
    ).render

    val modal = UdashModal(modalSize = Some(BootstrapStyles.Size.Large).toProperty)(
      headerFactory = Some(header),
      bodyFactory = Some(body),
      footerFactory = Some(footer)
    )
    modal.listen { case ev => events.append(ev) }

    val openModalButton = UdashButton(buttonStyle = BootstrapStyles.Color.Primary.toProperty)("Show modal...")
    openModalButton.listen { case UdashButton.ButtonClickEvent(_, _) =>
      modal.show()
    }
    val openAndCloseButton = UdashButton()("Open and close after 2 seconds...")
    openAndCloseButton.listen { case UdashButton.ButtonClickEvent(_, _) =>
      modal.show()
      window.setTimeout(() => modal.hide(), 2000)
    }
    div(GuideStyles.frame)(
      modal.render,
      UdashButtonGroup()(
        openModalButton.render,
        openAndCloseButton.render
      ).render
    ).render
  }

  def tooltips(): dom.Element = {
    import scala.concurrent.duration.DurationInt
    val tooltipContainerId = ComponentId("tooltip-container")
    val label1 = UdashBadge()(_ => Seq[Modifier]("Tooltip on hover with delay", GlobalStyles.smallMargin)).render
    UdashTooltip(
      trigger = Seq(UdashTooltip.Trigger.Hover),
      delay = UdashTooltip.Delay(500 millis, 250 millis),
      title = (_) => "Tooltip...",
      container = Option(s"#$tooltipContainerId")
    )(label1)

    val label2 = UdashBadge()(_ => Seq[Modifier]("Tooltip on click", GlobalStyles.smallMargin)).render
    UdashTooltip(
      trigger = Seq(UdashTooltip.Trigger.Click),
      delay = UdashTooltip.Delay(0 millis, 250 millis),
      placement = (_, _) => Seq(UdashTooltip.Placement.Bottom),
      title = (_) => "Tooltip 2...",
      container = Option(s"#$tooltipContainerId")
    )(label2)

    val label3 = UdashBadge()(_ => Seq[Modifier]("Tooltip with JS toggler", GlobalStyles.smallMargin)).render
    val label3Tooltip = UdashTooltip(
      trigger = Seq(UdashTooltip.Trigger.Manual),
      placement = (_, _) => Seq(UdashTooltip.Placement.Right),
      title = (_) => "Tooltip 3...",
      container = Option(s"#$tooltipContainerId")
    )(label3)

    val button = UdashButton()("Toggle tooltip")
    button.listen { case _ => label3Tooltip.toggle() }

    div(GuideStyles.frame, id := tooltipContainerId)(
      label1, label2, label3, button.render
    ).render
  }

  def popovers(): dom.Element = {
    import scala.concurrent.duration.DurationInt
    val popoverContainerId = ComponentId("popover-container")
    val label1 = UdashBadge()(_ => Seq[Modifier]("Popover on hover with delay", GlobalStyles.smallMargin)).render
    UdashPopover(
      trigger = Seq(UdashPopover.Trigger.Hover),
      delay = UdashPopover.Delay(500 millis, 250 millis),
      title = (_) => "Popover...",
      content = (_) => "Content...",
      container = Option(s"#$popoverContainerId")
    )(label1)

    val label2 = UdashBadge()(_ => Seq[Modifier]("Popover on click", GlobalStyles.smallMargin)).render
    UdashPopover(
      trigger = Seq(UdashPopover.Trigger.Click),
      delay = UdashPopover.Delay(0 millis, 250 millis),
      placement = (_, _) => Seq(UdashPopover.Placement.Bottom),
      title = (_) => "Popover 2...",
      content = (_) => "Content...",
      container = Option(s"#$popoverContainerId")
    )(label2)

    val label3 = UdashBadge()(_ => Seq[Modifier]("Popover with JS toggler", GlobalStyles.smallMargin)).render
    val label3Tooltip = UdashPopover(
      trigger = Seq(UdashPopover.Trigger.Manual),
      placement = (_, _) => Seq(UdashPopover.Placement.Left),
      html = true,
      title = (_) => "Popover 3...",
      content = (_) => {
        import scalatags.Text.all._
        Seq(
          p("HTML content..."),
          ul(li("Item 1"), li("Item 2"), li("Item 3"))
        ).render
      },
      container = Option(s"#$popoverContainerId")
    )(label3)

    val button = UdashButton()("Toggle popover")
    button.listen { case _ => label3Tooltip.toggle() }

    div(GuideStyles.frame, id := popoverContainerId)(
      label1, label2, label3, button.render
    ).render
  }

  def simpleCollapse(): dom.Element = {
    val events = SeqProperty.blank[UdashCollapse.CollapseEvent]
    val collapse = UdashCollapse()(
      div(wellStyles)(
        ul(repeat(events)(event => li(event.get.toString).render))
      )
    )
    collapse.listen { case ev => events.append(ev) }

    val toggleButton = UdashButton(buttonStyle = BootstrapStyles.Color.Primary.toProperty)(
      _ => Seq[Modifier](collapse.toggleButtonAttrs(), "Toggle...")
    )
    val openAndCloseButton = UdashButton()("Open and close after 2 seconds...")
    openAndCloseButton.listen { case _ =>
      collapse.show()
      window.setTimeout(() => collapse.hide(), 2000)
    }

    div(GuideStyles.frame)(
      UdashButtonGroup(justified = true.toProperty)(
        toggleButton.render,
        openAndCloseButton.render
      ).render,
      collapse.render
    ).render
  }

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
