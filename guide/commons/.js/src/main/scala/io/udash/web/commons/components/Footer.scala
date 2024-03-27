package io.udash.web.commons.components

import com.avsystem.commons.Opt
import io.udash.*
import io.udash.css.CssStyle
import io.udash.web.commons.config.ExternalUrls
import io.udash.web.commons.styles.GlobalStyles
import io.udash.web.commons.styles.components.FooterStyles
import io.udash.web.commons.views.Image
import org.scalajs.dom.html.Element
import scalatags.JsDom
import scalatags.JsDom.all.*

object Footer {

  import io.udash.css.CssView.*
  private val styles = FooterStyles

  private def template(wrapperStyle: Opt[CssStyle]): JsDom.TypedTag[Element] = footer(styles.footer)(
    div(GlobalStyles.body, wrapperStyle)(
      div(styles.footerInner)(
        a(styles.footerLogo, href := ExternalUrls.homepage)(
          Image("udash_logo.png", "Udash Framework", GlobalStyles.block)
        ),
        div(styles.footerLinks)(
          p(styles.footerMore)("See more"),
          ul(
            li(styles.navItem)(
              a(href := ExternalUrls.udashDemos, target := "_blank", GlobalStyles.underlineLink)("Demos on GitHub")
            ),
            li(styles.navItem)(
              a(href := ExternalUrls.stackoverflow, target := "_blank", GlobalStyles.underlineLink)("StackOverflow questions")
            )
          )
        ),
        p(styles.footerCopyrights)("Proudly made by ", a(styles.footerAvsystemLink, href := ExternalUrls.avsystem, target := "_blank")("AVSystem"))
      )
    )
  )

  def getTemplate(wrapperStyle: Opt[CssStyle] = Opt.Empty): Modifier = template(wrapperStyle)
}
