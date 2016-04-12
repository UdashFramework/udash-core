package io.udash.guide.components

import io.udash.guide.config.ExternalUrls
import io.udash.guide.styles.GlobalStyles
import io.udash.guide.styles.partials.HeaderStyles
import io.udash.guide.views.{Image, SVG, Size}
import org.scalajs.dom.raw.Element

import scalatags.JsDom.all._
import scalacss.ScalatagsCss._

object Header {
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
      div(HeaderStyles.headerRight)(
        ul(HeaderStyles.headerSocial)(
          li(HeaderStyles.headerSocialItem)(
            a(href := ExternalUrls.udashGithub, HeaderStyles.headerSocialLink, target := "_blank")(
              SVG("github.svg#github", Size(33, 32))
            )
          ),
          /*li(HeaderStyles.headerSocialItem)(
            a(href := ExternalUrls.todoMvc, HeaderStyles.headerSocialLink, target := "_blank")(
              SVG("todomvc.svg#todomvc", Size(34, 31))
            )
          ),*/
          li(HeaderStyles.headerSocialItem)(
            a(href := ExternalUrls.stackoverflow, HeaderStyles.headerSocialLink, target := "_blank")(
              SVG("stack.svg#stack", Size(29, 33))
            )
          ),
          li(HeaderStyles.headerSocialItem)(
            a(href := ExternalUrls.avsystem, HeaderStyles.headerSocialLinkYellow, target := "_blank")(
              SVG("avsystem.svg#avsystem", Size(33, 33)),
              div(HeaderStyles.tooltip)(
                div(HeaderStyles.tooltipTop),
                div(HeaderStyles.tooltipText)(
                  div(HeaderStyles.tooltipTextInner)(
                    "Proudly made by AVSystem"
                  )
                )
              )
            )
          )
        )
      )
    )
  ).render

  def getTemplate: Element = template
}
