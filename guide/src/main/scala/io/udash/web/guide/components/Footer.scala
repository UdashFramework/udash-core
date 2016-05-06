package io.udash.web.guide.components

import io.udash.web.commons.views.Image
import io.udash.web.commons.config.ExternalUrls
import io.udash.web.guide.styles.GlobalStyles
import io.udash.web.guide.styles.partials.FooterStyles
import io.udash.web.guide.styles.partials.GuideStyles
import org.scalajs.dom.raw.Element

import scalatags.JsDom.all._
import scalacss.ScalatagsCss._

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
            li(GuideStyles.navItem)(
              a(href := ExternalUrls.udashDemos, target := "_blank", GuideStyles.underlineLink)("Github demo")
            ),
            // TODO: unccoment
            /*li(UdashGuideStyles.linkWrapper)(
              a(href := ExternalUrls.todoMvc, target := "_blank", UdashGuideStyles.link)("Todomvc.com demo")
            ),*/
            li(GuideStyles.navItem)(
              a(href := ExternalUrls.stackoverflow, target := "_blank", GuideStyles.underlineLink)("StackOverflow questions")
            )
          )
        ),
        p(FooterStyles.footerCopyrights)("Proudly made by ", a(FooterStyles.footerAvsystemLink, href := ExternalUrls.avsystem, target := "_blank")("AVSystem"))
      )
    )
  ).render

  def getTemplate: Element = template
}
