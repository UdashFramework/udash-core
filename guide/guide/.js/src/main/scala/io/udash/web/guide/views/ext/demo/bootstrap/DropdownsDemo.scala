package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash.bootstrap.dropdown.UdashDropdown
import io.udash.bootstrap.dropdown.UdashDropdown.DropdownEvent
import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.bootstrap.utils.BootstrapStyles.{Color, Side, SpacingSize}
import io.udash.css.CssView
import io.udash.logging.CrossLogging
import io.udash.properties.seq.SeqProperty
import io.udash.web.guide.components.BootstrapUtils.wellStyles
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.{BootstrapExtState, IntroState}
import io.udash.{produce, _}
import org.scalajs.dom.window
import scalatags.JsDom

object DropdownsDemo extends AutoDemo with CrossLogging with CssView {

  import JsDom.all._
  import io.udash.web.guide.Context._

  private val (rendered, source) = {
    val url = Url(BootstrapExtState.url)
    val items = SeqProperty[UdashDropdown.DefaultDropdownItem](Seq(
      UdashDropdown.DefaultDropdownItem.Header("Start"),
      UdashDropdown.DefaultDropdownItem.Link("Intro", Url(IntroState.url)),
      UdashDropdown.DefaultDropdownItem.Disabled(
        UdashDropdown.DefaultDropdownItem.Link("Test Disabled", url)
      ),
      UdashDropdown.DefaultDropdownItem.Divider,
      UdashDropdown.DefaultDropdownItem.Header("Dynamic")
    ))

    val clicks = SeqProperty[String](Seq.empty)
    var i = 1
    val appendHandler = window.setInterval(() => {
      items.append(
        UdashDropdown.DefaultDropdownItem.Link(s"Test $i", url)
      )
      i += 1
    }, 5000)
    window.setTimeout(() => window.clearInterval(appendHandler), 60000)

    val dropdown = UdashDropdown(items)(
      UdashDropdown.defaultItemFactory,
      _ => Seq[Modifier](
        "Dropdown ",
        BootstrapStyles.Button.color(Color.Primary)
      )
    )
    val dropup = UdashDropdown(
      items,
      UdashDropdown.Direction.Up.toProperty
    )(
      UdashDropdown.defaultItemFactory, _ => "Dropup "
    )

    Seq(dropdown, dropup).foreach(_.listen {
      case UdashDropdown.DropdownEvent.SelectionEvent(_, item) =>
        clicks.append(item.toString)
      case ev: DropdownEvent[_, _] =>
        logger.info(ev.toString)
    })

    div(
      div(
        BootstrapStyles.Grid.row,
        BootstrapStyles.Spacing.margin(
          side = Side.Bottom,
          size = SpacingSize.Normal
        )
      )(
        div(BootstrapStyles.Grid.col(6))(dropdown),
        div(BootstrapStyles.Grid.col(6))(dropup)
      ),
      h4("Clicks: "),
      produce(clicks)(seq =>
        ul(wellStyles)(seq.map(click =>
          li(click)
        ): _*).render
      )
    )
  }.withSourceCode

  override protected def demoWithSource(): (JsDom.all.Modifier, Iterator[String]) = {
    (div(GuideStyles.frame)(rendered), source.linesIterator.drop(1))
  }
}

