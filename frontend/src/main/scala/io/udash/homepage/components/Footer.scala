package io.udash.homepage.components

import io.udash.homepage.IndexState
import io.udash.homepage.styles.GlobalStyles
import io.udash.homepage.styles.partials.FooterStyles
import org.scalajs.dom.raw.Element

import scalacss.ScalatagsCss._
import scalatags.JsDom.all._
import io.udash.homepage.Context._
import io.udash.homepage.config.ExternalUrls
import io.udash.homepage.views.Image

object Footer {
  def getTemplate: Element = {
    footer(FooterStyles.footer)(
      div(GlobalStyles.body, GlobalStyles.clearfix)(
        a(FooterStyles.footerLogo, href := IndexState(None).url)(
          Image("udash_logo.png", "Udash Framework", GlobalStyles.block)
        ),
        div(FooterStyles.footerLinks)(
          p(FooterStyles.footerMore)("See more"),
          ul(
            li(FooterStyles.footerLinkWrapper)(
              a(href := ExternalUrls.udashDemos, target := "_blank", FooterStyles.footerLink)("Github demos")
            ),
            // TODO: unccoment
            /*li(UdashGuideStyles.linkWrapper)(
              a(target := "_blank", UdashGuideStyles.link)("Todomvc.com demo")
            ),*/
            li(FooterStyles.footerLinkWrapper)(
              a(href := ExternalUrls.stackoverflow, target := "_blank", FooterStyles.footerLink)("StackOverflow questions")
            )
          ),
          p(FooterStyles.footerCopyrights)("Proudly made by ", a(FooterStyles.footerAvsystem, href := ExternalUrls.avsystem)("AVSystem"))
        )
      )
    ).render
  }
}
