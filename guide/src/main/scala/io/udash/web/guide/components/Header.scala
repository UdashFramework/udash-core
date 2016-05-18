package io.udash.web.guide.components

import io.udash.web.commons.components.{HeaderButtons, HeaderNav}
import io.udash.web.commons.config.ExternalUrls
import io.udash.web.commons.styles.GlobalStyles
import io.udash.web.commons.styles.components.{HeaderButtonsStyles, HeaderNavStyles}
import io.udash.web.commons.views.Image
import io.udash.web.guide.styles.partials.HeaderStyles
import org.scalajs.dom.raw.Element

import scalacss.ScalatagsCss._
import scalatags.JsDom.all._

object Header extends HeaderButtons with HeaderNav{
  private lazy val template = header(HeaderStyles.header)(
    div(GlobalStyles.body, GlobalStyles.clearfix)(
      div(HeaderStyles.headerLeft)(
        a(HeaderStyles.headerLogo, href := ExternalUrls.homepage)(
          Image("udash_logo_m.png", "Udash Framework", GlobalStyles.block)
        )/*,
        navigation(Seq(
          NavItem(ExternalUrls.guide, "Documentation"),
          NavItem(ExternalUrls.releases, "Changelog")
        ))*/
      ),
      buttons
    )
  ).render

  def getTemplate: Element = template

  override val buttonStyles: HeaderButtonsStyles = HeaderStyles
  override val navStyles: HeaderNavStyles = HeaderStyles
}
