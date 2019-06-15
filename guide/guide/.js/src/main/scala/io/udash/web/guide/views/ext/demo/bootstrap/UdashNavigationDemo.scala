package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash._
import io.udash.bootstrap.dropdown.UdashDropdown
import io.udash.bootstrap.nav.{UdashNav, UdashNavbar}
import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.css.CssView
import io.udash.logging.CrossLogging
import io.udash.properties.seq.SeqProperty
import io.udash.web.guide.components.{MenuContainer, MenuEntry, MenuLink}
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom

object UdashNavigationDemo extends AutoDemo with CrossLogging with CssView {

  import JsDom.all._
  import io.udash.web.guide.Context._

  private val (rendered, source) = {
    def linkFactory(l: MenuLink, dropdown: Boolean = true) =
      a(
        href := l.state.url,
        BootstrapStyles.Dropdown.item.styleIf(dropdown),
        BootstrapStyles.Navigation.link.styleIf(!dropdown)
      )(span(l.name)).render

    val panels = SeqProperty[MenuEntry](mainMenuEntries.slice(0, 4))

    div(
      UdashNavbar(
        darkStyle = true.toProperty,
        backgroundStyle = BootstrapStyles.Color.Dark.toProperty
      )(_ => UdashNav(panels)(
        elemFactory = (panel, nested) => div(nested(produce(panel) {
          case MenuContainer(name, children) =>
            val childrenProperty = SeqProperty(children)
            UdashDropdown(childrenProperty, buttonToggle = false.toProperty)(
              (item: Property[MenuLink], _) => linkFactory(item.get),
              _ => span(name, " ")
            ).render.setup(
              _.firstElementChild.applyTags(BootstrapStyles.Navigation.link)
            )
          case link: MenuLink => linkFactory(link, dropdown = false)
        })).render,
        isDropdown = _.transform {
          case MenuContainer(_, _) => true
          case MenuLink(_, _, _) => false
        }
      ),
        span("Udash")
      )
    )
  }.withSourceCode

  override protected def demoWithSource(): (JsDom.all.Modifier, Iterator[String]) = {
    (div(GuideStyles.frame)(rendered), source.linesIterator.drop(1))
  }
}

