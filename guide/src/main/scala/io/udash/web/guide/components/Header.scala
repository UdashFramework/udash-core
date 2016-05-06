package io.udash.web.guide.components

import io.udash.web.commons.components.HeaderButtons
import io.udash.web.commons.views.{Image, SVG, Size}
import io.udash.web.commons.config.ExternalUrls
import io.udash.web.commons.styles.components.HeaderButtonsStyles
import io.udash.web.guide.styles.GlobalStyles
import io.udash.web.guide.styles.partials.HeaderStyles
import org.scalajs.dom.raw.Element

import scalatags.JsDom.all._
import scalacss.ScalatagsCss._

object Header extends HeaderButtons {
  private lazy val template = header(HeaderStyles.header)(
    div(GlobalStyles.body, GlobalStyles.clearfix)(
      div(HeaderStyles.headerLeft)(
        a(HeaderStyles.headerLogo, href := ExternalUrls.homepage)(
          Image("udash_logo_m.png", "Udash Framework", GlobalStyles.block)
        )/*,
        nav(HeaderStyles.headerNav)(
          ul(
            li(HeaderStyles.headerLinkWrapper)(
              a(href := ExternalUrls.guide, HeaderStyles.headerLink)("Documentation")
            )
          )
        )*/
      ),
      buttons
    )
  ).render

  def getTemplate: Element = template

  override val styles: HeaderButtonsStyles = HeaderStyles
}
