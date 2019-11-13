package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash.logging.CrossLogging
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

object DropdownsDemo extends AutoDemo with CrossLogging {

  import io.udash.web.guide.Context._
  import io.udash.web.guide.{BootstrapExtState, IntroState}

  private val (rendered, source) = {
    import io.udash._
    import io.udash.bootstrap.dropdown.UdashDropdown
    import io.udash.bootstrap.dropdown.UdashDropdown._
    import io.udash.bootstrap.utils.BootstrapStyles._
    import org.scalajs.dom.window
    import scalatags.JsDom.all._

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

    val dropdown = UdashDropdown.default(items)(_ => Seq[Modifier]("Dropdown ", Button.color(Color.Primary)))
    val dropup = UdashDropdown.default(items, UdashDropdown.Direction.Up.toProperty)(_ => "Dropup ")

    Seq(dropdown, dropup).foreach(_.listen {
      case UdashDropdown.DropdownEvent.SelectionEvent(_, item) =>
        clicks.append(item.toString)
      case ev: DropdownEvent[_, _] =>
        logger.info(ev.toString)
    })

    div(
      div(
        Grid.row,
        Spacing.margin(
          side = Side.Bottom,
          size = SpacingSize.Normal
        )
      )(
        div(Grid.col(6))(dropdown),
        div(Grid.col(6))(dropup)
      ),
      h4("Clicks: "),
      produce(clicks)(seq =>
        ul(Card.card, Card.body, Background.color(Color.Light))(seq.map(li(_))).render
      )
    ).render
  }.withSourceCode

  override protected def demoWithSource(): (Modifier, Iterator[String]) = {

    (rendered.setup(_.applyTags(GuideStyles.frame)), source.linesIterator)
  }
}

