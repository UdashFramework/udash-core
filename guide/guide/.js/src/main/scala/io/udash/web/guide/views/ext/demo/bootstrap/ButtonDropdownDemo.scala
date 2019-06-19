package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash.css.CssView
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

object ButtonDropdownDemo extends AutoDemo with CssView {

  import io.udash.web.guide.Context._
  import io.udash.web.guide.{BootstrapExtState, IntroState}

  private val (rendered, source) = {
    import io.udash._
    import io.udash.bootstrap.button._
    import io.udash.bootstrap.dropdown.UdashDropdown
    import io.udash.bootstrap.dropdown.UdashDropdown._
    import io.udash.bootstrap.utils.BootstrapImplicits._
    import scalatags.JsDom.all._

    val items = SeqProperty[DefaultDropdownItem](
      DefaultDropdownItem.Header("Start"),
      DefaultDropdownItem.Link("Intro", Url(IntroState.url)),
      DefaultDropdownItem.Disabled(
        DefaultDropdownItem.Link("Test Disabled", Url(BootstrapExtState.url))
      ),
      DefaultDropdownItem.Divider,
      DefaultDropdownItem.Header("End"),
    )

    div(
      UdashButtonToolbar()(
        UdashButtonGroup()(
          UdashButton()("Button").render,
          UdashDropdown(items)(
            defaultItemFactory, _ => ""
          ).render,
          UdashDropdown(items, Direction.Up.toProperty)(
            defaultItemFactory, _ => ""
          ).render
        ).render,
        UdashDropdown(items)(
          defaultItemFactory, _ => "Dropdown "
        ).render
      )
    ).render
  }.withSourceCode

  override protected def demoWithSource(): (Modifier, Iterator[String]) = {
    (rendered.setup(_.applyTags(GuideStyles.frame)), source.lines)
  }
}

