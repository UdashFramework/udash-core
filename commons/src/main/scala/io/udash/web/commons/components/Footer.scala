package io.udash.web.commons.components

import io.udash.web.commons.config.ExternalUrls
import io.udash.web.commons.styles.GlobalStyles
import io.udash.web.commons.styles.components.FooterStyles
import io.udash.web.commons.views.Image
import org.scalajs.dom.raw.Element

import scalacss.ScalatagsCss._
import scalatags.JsDom.all._

object Footer {
  private val styles = FooterStyles.get
  private lazy val template = footer(styles.footer)(
    div(GlobalStyles.body)(
      div(styles.footerInner)(
        a(styles.footerLogo, href := ExternalUrls.homepage)(
          Image("udash_logo.png", "Udash Framework", GlobalStyles.block)
        ),
        div(styles.footerLinks)(
          p(styles.footerMore)("See more"),
          ul(
            li(styles.navItem)(
              a(href := ExternalUrls.udashDemos, target := "_blank", GlobalStyles.underlineLink)("Github demo")
            ),
            // TODO: unccoment
            /*li(UdashGuideStyles.linkWrapper)(
              a(href := ExternalUrls.todoMvc, target := "_blank", UdashGuideStyles.link)("Todomvc.com demo")
            ),*/
            li(styles.navItem)(
              a(href := ExternalUrls.stackoverflow, target := "_blank", GlobalStyles.underlineLink)("StackOverflow questions")
            )
          )
        ),
        p(styles.footerCopyrights)("Proudly made by ", a(styles.footerAvsystemLink, href := ExternalUrls.avsystem, target := "_blank")("AVSystem"))
      )
    )
  )

  def getTemplate: Modifier = template
}
