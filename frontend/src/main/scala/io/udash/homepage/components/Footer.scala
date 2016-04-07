package io.udash.homepage.components

import io.udash.homepage.styles.GlobalStyles
import io.udash.homepage.styles.partials.{FooterStyles, HomepageStyles}
import org.scalajs.dom.raw.Element

import scalatags.JsDom.all._
import scalacss.ScalatagsCss._

class Footer() {
  def getTemplate: Element = {
    footer(FooterStyles.footer)(
      div(GlobalStyles.body, GlobalStyles.clearfix)(
        a(FooterStyles.footerLogo, href := "/")(
          img(GlobalStyles.block, src := "assets/images/udash_logo.png", alt := "Udash Guide")
        ),
        div(FooterStyles.footerLinks)(
          p(FooterStyles.footerMore)("See more"),
          ul(
            li(FooterStyles.footerLinkWrapper)(
              a(href := "https://github.com/UdashFramework/udash-demos", target := "_blank", FooterStyles.footerLink)("Github demo")
            ),
            // TODO: unccoment
            /*li(UdashGuideStyles.linkWrapper)(
              a(target := "_blank", UdashGuideStyles.link)("Todomvc.com demo")
            ),*/
            li(FooterStyles.footerLinkWrapper)(
              a(href := "http://stackoverflow.com/questions/tagged/udash", target := "_blank", FooterStyles.footerLink)("StackOverflow questions")
            )
          ),
          p(FooterStyles.footerCopyrights)("Udash was made with great support of ", a(FooterStyles.footerAvsystem, href := "http://www.avsystem.com/")("AVSystem"))
        )
      )
    ).render
  }
}

object Footer {
  def apply(): Footer = {
    new Footer()
  }
}

