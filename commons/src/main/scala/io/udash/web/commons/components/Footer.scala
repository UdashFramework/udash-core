package io.udash.web.commons.components

import io.udash.web.commons.config.ExternalUrls
import io.udash.web.commons.styles.GlobalStyles
import io.udash.web.commons.styles.components.FooterStyles
import io.udash.web.commons.views.Image
import org.scalajs.dom.raw.Element

import scalacss.ScalatagsCss._
import scalatags.JsDom.all._

object Footer {
  private lazy val template = footer(FooterStyles.footer)(
    div(GlobalStyles.body)(
      div(FooterStyles.footerInner)(
        a(FooterStyles.footerLogo, href := ExternalUrls.homepage)(
          Image("udash_logo.png", "Udash Framework", GlobalStyles.block)
        ),
        div(FooterStyles.footerLinks)(
          p(FooterStyles.footerMore)("See more"),
          ul(
            li(FooterStyles.navItem)(
              a(href := ExternalUrls.udashDemos, target := "_blank", GlobalStyles.underlineLink)("Github demo")
            ),
            // TODO: unccoment
            /*li(UdashGuideStyles.linkWrapper)(
              a(href := ExternalUrls.todoMvc, target := "_blank", UdashGuideStyles.link)("Todomvc.com demo")
            ),*/
            li(FooterStyles.navItem)(
              a(href := ExternalUrls.stackoverflow, target := "_blank", GlobalStyles.underlineLink)("StackOverflow questions")
            )
          )
        ),
        p(FooterStyles.footerCopyrights)("Proudly made by ", a(FooterStyles.footerAvsystemLink, href := ExternalUrls.avsystem, target := "_blank")("AVSystem"))
      )
    )
  ).render

  def getTemplate: Element = template
}
