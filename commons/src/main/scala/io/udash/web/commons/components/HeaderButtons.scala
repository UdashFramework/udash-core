package io.udash.web.commons.components

import io.udash.web.commons.config.ExternalUrls
import io.udash.web.commons.styles.components.HeaderButtonsStyles
import io.udash.web.commons.views.{SVG, Size}

import scalacss.ScalatagsCss._
import scalatags.JsDom.all._

trait HeaderButtons {
  val styles: HeaderButtonsStyles

  def buttons = {
    div(styles.headerRight)(
      ul(styles.headerSocial)(
        li(styles.headerSocialItem)(
          a(href := ExternalUrls.udashGithub, styles.headerSocialLink, target := "_blank")(
            SVG("github.svg#github", Size(33, 32))
          )
        ),
        /*li(styles.headerSocialItem)(
          a(href := ExternalUrls.todoMvc, styles.headerSocialLink, target := "_blank")(
            SVG("todomvc.svg#todomvc", Size(34, 31))
          )
        ),*/
        li(styles.headerSocialItem)(
          a(href := ExternalUrls.stackoverflow, styles.headerSocialLink, target := "_blank")(
            SVG("stack.svg#stack", Size(29, 33))
          )
        ),
        li(styles.headerSocialItem)(
          a(href := ExternalUrls.avsystem, styles.headerSocialLinkYellow, target := "_blank")(
            SVG("avsystem.svg#avsystem", Size(33, 33)),
            div(styles.tooltip)(
              div(styles.tooltipTop),
              div(styles.tooltipText)(
                div(styles.tooltipTextInner)(
                  "Proudly made by AVSystem"
                )
              )
            )
          )
        )
      )
    )
  }
}
