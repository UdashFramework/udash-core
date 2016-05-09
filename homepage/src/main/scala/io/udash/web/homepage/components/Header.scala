package io.udash.web.homepage.components

import io.udash.core.{DomWindow, Window}
import io.udash.web.commons.components.{HeaderButtons, HeaderNav}
import io.udash.web.homepage.IndexState
import io.udash.web.homepage.Context._
import io.udash.web.commons.config.ExternalUrls
import io.udash.web.commons.styles.components.{HeaderButtonsStyles, HeaderNavStyles}
import io.udash.web.homepage.styles.GlobalStyles
import io.udash.web.homepage.styles.partials.{HeaderStyles, HomepageStyles}
import io.udash.web.commons.views.{SVG, Size}
import io.udash.wrappers.jquery.{JQueryEvent, _}
import org.scalajs.dom._
import org.scalajs.dom.raw.Element

import scalacss.ScalatagsCss._
import scalatags.JsDom.all._

object Header extends HeaderButtons with HeaderNav {
  val PinAttribute = "data-pin"

  val window = jQ(DomWindow)
  window.on("scroll", onScroll)

  private def onScroll(el: Element, ev: JQueryEvent): Unit = {
    val pin = jQ(template).attr(Header.PinAttribute).getOrElse("false").toBoolean
    val scrollTop = jQ(DomWindow).scrollTop()
    val introHeight = jQ(s".${HomepageStyles.sectionIntro.htmlClass}").height()

    if (scrollTop >= introHeight && !pin) {
      jQ(template).attr(Header.PinAttribute, "true")
    } else if (scrollTop < introHeight && pin) {
      jQ(template).attr(Header.PinAttribute, "false")
    }
  }

  private lazy val template = header(HeaderStyles.header)(
    div(GlobalStyles.body, GlobalStyles.clearfix)(
      div(HeaderStyles.headerLeft)(
        a(HeaderStyles.headerLogo, href := IndexState(None).url)(),
        navigation(Seq(
          NavItem(ExternalUrls.guide, "Documentation"),
          NavItem(ExternalUrls.releases, "Changelog")
        ))
      ),
      buttons
    )
  ).render

  def getTemplate: Element = template

  override val buttonStyles: HeaderButtonsStyles = HeaderStyles
  override val navStyles: HeaderNavStyles = HeaderStyles
}
