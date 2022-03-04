package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash.bootstrap.button.{UdashButton, UdashButtonOptions}
import io.udash.bootstrap.button.UdashButton.ButtonTag
import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

object NavigationDemo extends AutoDemo {

  import io.udash.web.guide.Context._
  import io.udash.web.guide.components.{MenuContainer, MenuEntry, MenuLink}

  private val (rendered, source) = {
    import io.udash._
    import io.udash.bootstrap.dropdown.UdashDropdown
    import io.udash.bootstrap.nav.{UdashNav, UdashNavbar}
    import io.udash.bootstrap.utils.BootstrapStyles._
    import io.udash.css.CssView._
    import org.scalajs.dom.html.Anchor
    import scalatags.JsDom.all._

    def linkButtonFactory(link: MenuLink, dropdown: Boolean = true) = UdashButton(
      options = UdashButtonOptions(
        tag = ButtonTag.Anchor(link.state.url),
        color = BootstrapStyles.Color.Link.opt,
        customModifiers = if (dropdown) Seq(Dropdown.item) else Seq(Navigation.link)
      )
    )(_ => link.name)

    val panels = SeqProperty(mainMenuEntries.slice(0, 4): Seq[MenuEntry])

    div(
      UdashNavbar(
        darkStyle = true.toProperty,
        backgroundStyle = Color.Dark.toProperty
      )(_ => UdashNav(panels)(
        elemFactory = (panel, nested) => div(nested(produce(panel) {
          case MenuContainer(name, children) =>
            val dropdown = UdashDropdown(children.toSeqProperty)(
              (item, _) => linkButtonFactory(item.get).render,
              _ => span(name, " "),
              buttonFactory = UdashButton(
                options = UdashButtonOptions(
                  tag = ButtonTag.Button,
                  color = BootstrapStyles.Color.Link.opt,
                  customModifiers = Seq(Navigation.link)
                )
              )
            ).render
            dropdown
          case link: MenuLink => linkButtonFactory(link, dropdown = false).render
        })).render,
        isDropdown = _.transform {
          case _: MenuContainer => true
          case _: MenuLink => false
        }
      ),
        span("Udash")
      )
    ).render
  }.withSourceCode

  override protected def demoWithSource(): (Modifier, Iterator[String]) = {
    import io.udash.css.CssView._
    (rendered.setup(_.applyTags(GuideStyles.frame)), source.linesIterator)
  }
}

