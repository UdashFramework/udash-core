package io.udash.web.commons.components

import io.udash.web.commons.config.ExternalUrls
import io.udash.web.commons.styles.components.{HeaderButtonsStyles, HeaderNavStyles}
import io.udash.web.commons.views.{SVG, Size}
import org.scalajs.dom.Element

trait HeaderNav {
  import scalacss.ScalatagsCss._
  import scalatags.JsDom.all._
  import scalatags.JsDom.tags2.nav

  val navStyles: HeaderNavStyles

  case class NavItem(url: String, title: String)

  def navigation(items: NavItem*): Element =
    nav(navStyles.headerNav)(
      ul(navStyles.headerLinkList)(
        items.map(item =>
          li(navStyles.headerLinkWrapper)(
            a(href := item.url, navStyles.headerLink)(item.title)
          )
        )
      )
    ).render
}
