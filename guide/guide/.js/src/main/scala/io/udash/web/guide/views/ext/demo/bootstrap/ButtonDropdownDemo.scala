package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash._
import io.udash.bootstrap.button.{UdashButton, UdashButtonGroup, UdashButtonToolbar}
import io.udash.bootstrap.dropdown.UdashDropdown
import io.udash.bootstrap.dropdown.UdashDropdown.DefaultDropdownItem
import io.udash.css.CssView
import io.udash.logging.CrossLogging
import io.udash.properties.seq.SeqProperty
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom

object ButtonDropdownDemo extends AutoDemo with CrossLogging with CssView {

  import JsDom.all._
  import io.udash.bootstrap.utils.BootstrapImplicits._

  private val (rendered, source) = {
    val items = SeqProperty[DefaultDropdownItem](
      UdashDropdown.DefaultDropdownItem.Header("Start"),
      UdashDropdown.DefaultDropdownItem.Link("Intro", Url("#")),
      UdashDropdown.DefaultDropdownItem.Disabled(
        UdashDropdown.DefaultDropdownItem.Link("Test Disabled", Url("#"))
      ),
      UdashDropdown.DefaultDropdownItem.Divider,
      UdashDropdown.DefaultDropdownItem.Header("End"),
      UdashDropdown.DefaultDropdownItem.Link("Intro", Url("#"))
    )

    div(
      UdashButtonToolbar()(
        UdashButtonGroup()(
          UdashButton()("Button").render,
          UdashDropdown(items)(
            UdashDropdown.defaultItemFactory, _ => ""
          ).render,
          UdashDropdown(items, UdashDropdown.Direction.Up.toProperty)(
            UdashDropdown.defaultItemFactory, _ => ""
          ).render
        ).render,
        UdashDropdown(items)(
          UdashDropdown.defaultItemFactory, _ => "Dropdown "
        ).render
      )
    )
  }.withSourceCode

  override protected def demoWithSource(): (JsDom.all.Modifier, Iterator[String]) = {
    (div(GuideStyles.frame)(rendered), source.linesIterator.drop(1))
  }
}

