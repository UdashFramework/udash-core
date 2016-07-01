package io.udash.web.homepage.components

import io.udash.core.DomWindow
import io.udash.web.commons.components.{HeaderButtons, HeaderNav}
import io.udash.web.commons.config.ExternalUrls
import io.udash.web.commons.styles.GlobalStyles
import io.udash.web.commons.styles.attributes.Attributes
import io.udash.web.commons.styles.components.{HeaderButtonsStyles, HeaderNavStyles, MobileMenuStyles}
import io.udash.web.homepage.Context._
import io.udash.web.homepage.IndexState
import io.udash.web.homepage.styles.partials.{HeaderStyles, HomepageStyles}
import io.udash.wrappers.jquery.{JQueryEvent, _}
import org.scalajs.dom.raw.Element

import scalacss.ScalatagsCss._
import scalatags.JsDom.all._

object Header extends HeaderButtons with HeaderNav {
  override val buttonStyles: HeaderButtonsStyles = HeaderStyles
  override val navStyles: HeaderNavStyles = HeaderStyles

  private val window = jQ(DomWindow)
  window.on("scroll", onScroll)

  private lazy val btnMobileMenu = a(href := "#", MobileMenuStyles.get.btnMobile, HeaderStyles.get.btnMobile)(
    div(MobileMenuStyles.get.btnMobileLines)(
      span(MobileMenuStyles.get.btnMobileLineTop),
      span(MobileMenuStyles.get.btnMobileLineMiddle),
      span(MobileMenuStyles.get.btnMobileLineBottom)
    )
  ).render

  private lazy val navElement =  navigation(Seq(
    NavItem(ExternalUrls.guide, "Documentation"),
    NavItem(ExternalUrls.releases, "Changelog"),
    NavItem(ExternalUrls.license, "License")
  ))

  private lazy val template = header(HeaderStyles.get.header)(
    div(GlobalStyles.get.body, GlobalStyles.get.clearfix)(
      div(HeaderStyles.get.headerLeft)(
        btnMobileMenu,
        a(HeaderStyles.get.headerLogo, href := IndexState(None).url)(),
        navElement
      ),
      buttons
    )
  ).render


  private lazy val jqNav = jQ(navElement)
  private lazy val jqMobileButton = jQ(btnMobileMenu)

  jqMobileButton.on("click", (jqThis: Element, jqEvent: JQueryEvent) => {
    jqEvent.preventDefault()
    toggleBooleanAttribute(jqNav, Attributes.data(Attributes.Active))
    toggleBooleanAttribute(jqMobileButton, Attributes.data(Attributes.Active))
  })

  private def onScroll(el: Element, ev: JQueryEvent): Unit = {
    val pin = jQ(template).attr(Attributes.data(Attributes.Pinned)).getOrElse("false").toBoolean
    val scrollTop = jQ(DomWindow).scrollTop()
    val introHeight = jQ(s".${HomepageStyles.get.sectionIntro.htmlClass}").height()

    if (scrollTop >= introHeight && !pin) {
      jQ(template).attr(Attributes.data(Attributes.Pinned), "true")
    } else if (scrollTop < introHeight && pin) {
      jQ(template).attr(Attributes.data(Attributes.Pinned), "false")
    }
  }

  def getTemplate: Element = template

  private def toggleBooleanAttribute(jqElement: JQuery, attribute: String): Unit = {
    val activeOption = jqElement.attr(attribute)
    val newValue = if (activeOption.isEmpty || !activeOption.get.toBoolean) true else false

    jqElement.attr(Attributes.data(Attributes.Active), newValue.toString)
  }
}
