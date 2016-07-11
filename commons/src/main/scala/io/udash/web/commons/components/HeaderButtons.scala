package io.udash.web.commons.components

import io.udash.web.commons.config.ExternalUrls
import io.udash.web.commons.styles.components.HeaderButtonsStyles
import io.udash.web.commons.views.{SVG, Size}

import scalacss.ScalatagsCss._
import scalatags.JsDom.all._

trait HeaderButtons {
  val buttonStyles: HeaderButtonsStyles

  def buttons = {
    div(buttonStyles.headerRight)(
      ul(buttonStyles.headerSocial)(
        li(buttonStyles.headerSocialItem)(
          a(href := ExternalUrls.udashGitter, buttonStyles.headerSocialLink, target := "_blank")(
            SVG("gitter.svg#gitter", Size(124, 127))
          )
        ),

        li(buttonStyles.headerSocialItem)(
          a(href := ExternalUrls.udashGithub, buttonStyles.headerSocialLink, target := "_blank")(
            SVG("github.svg#github", Size(33, 32))
          )
        ),
        /*li(styles.headerSocialItem)(
          a(href := ExternalUrls.todoMvc, styles.headerSocialLink, target := "_blank")(
            SVG("todomvc.svg#todomvc", Size(34, 31))
          )
        ),*/
        li(buttonStyles.headerSocialItem)(
          a(href := ExternalUrls.stackoverflow, buttonStyles.headerSocialLink, target := "_blank")(
            SVG("stack.svg#stack", Size(29, 33))
          )
        ),
        li(buttonStyles.headerSocialItem)(
          a(href := ExternalUrls.avsystem, buttonStyles.headerSocialLinkYellow, target := "_blank")(
            SVG("avsystem.svg#avsystem", Size(33, 33)),
            div(buttonStyles.tooltip)(
              div(buttonStyles.tooltipTop),
              div(buttonStyles.tooltipText)(
                div(buttonStyles.tooltipTextInner)(
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
