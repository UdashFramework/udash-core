package io.udash.homepage.components

import io.udash.core.{DomWindow, Window}
import io.udash.homepage.IndexState
import io.udash.homepage.Context._
import io.udash.homepage.styles.GlobalStyles
import io.udash.homepage.styles.partials.{HeaderStyles, HomepageStyles}
import io.udash.homepage.views.{SVG, Size}
import io.udash.wrappers.jquery._
import org.scalajs.dom.raw.Element

import scalacss.ScalatagsCss._
import scalatags.JsDom.all._

class Header() {

  Window.onScroll(onScroll)

  private def onScroll(): Unit = {
    val pin = jQ(template).attr(Header.PinAttribute).getOrElse("false").toBoolean
    val scrollTop = jQ(DomWindow).scrollTop()
    val introHeight = jQ(s".${HomepageStyles.sectionIntro.htmlClass}").height()

    if (scrollTop >= introHeight && !pin) {
      jQ(template).attr(Header.PinAttribute, "true")
    } else if (scrollTop < introHeight && pin) {
      jQ(template).attr(Header.PinAttribute, "false")
    }
  }

  private lazy val template = header(HeaderStyles.header)(
    div(GlobalStyles.body, GlobalStyles.clearfix)(
      div(HeaderStyles.headerLeft)(
        a(HeaderStyles.headerLogo, href := "/")()/*,
        nav(HeaderStyles.headerNav)(
          ul(
            li(HeaderStyles.headerLinkWrapper)(
              a(href := "/", HeaderStyles.headerLink)("Documentation")
            )
          )
        )*/
      ),
      div(HeaderStyles.headerRight)(
        ul(HeaderStyles.headerSocial)(
          li(HeaderStyles.headerSocialItem)(
            a(href := "https://github.com/UdashFramework", HeaderStyles.headerSocialLink, target := "_blank")(
              SVG("github.svg#github", Size(33, 32))
            )
          ),
          /*li(HeaderStyles.headerSocialItem)(
            a(href := "#", HeaderStyles.headerSocialLink, target := "_blank")(
              SVG("todomvc.svg#todomvc", Size(34, 31))
            )
          ),*/
          li(HeaderStyles.headerSocialItem)(
            a(href := "http://stackoverflow.com/questions/tagged/udash", HeaderStyles.headerSocialLink, target := "_blank")(
              SVG("stack.svg#stack", Size(29, 33))
            )
          ),
          li(HeaderStyles.headerSocialItem)(
            a(href := "http://www.avsystem.com/", HeaderStyles.headerSocialLinkYellow, target := "_blank")(
              SVG("avsystem.svg#avsystem", Size(33, 33))
            )
          )
        )
      )
    )
  ).render

  def getTemplate: Element = template
}

object Header {
  val PinAttribute = "data-pin"

  def apply(): Header = {
    new Header()
  }
}

