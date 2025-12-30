package io.udash.web.homepage.components

import io.udash.web.commons.components.{HeaderButtons, HeaderNav}
import io.udash.web.commons.config.ExternalUrls
import io.udash.web.commons.styles.GlobalStyles
import io.udash.web.commons.styles.attributes.Attributes
import io.udash.web.commons.styles.components.{HeaderButtonsStyles, HeaderNavStyles, MobileMenuStyles}
import io.udash.web.homepage.Context.*
import io.udash.web.homepage.HelloState
import io.udash.web.homepage.styles.partials.{HeaderStyles, HomepageStyles}
import io.udash.wrappers.jquery.*
import org.scalajs.dom.{Element, document}
import scalatags.JsDom.all.*

object Header extends HeaderButtons with HeaderNav {
  import io.udash.css.CssView.*

  override val buttonStyles: HeaderButtonsStyles = HeaderStyles
  override val navStyles: HeaderNavStyles = HeaderStyles

  private lazy val btnMobileMenu = a(href := "#", MobileMenuStyles.btnMobile, HeaderStyles.btnMobile)(
    div(MobileMenuStyles.btnMobileLines)(
      span(MobileMenuStyles.btnMobileLineTop),
      span(MobileMenuStyles.btnMobileLineMiddle),
      span(MobileMenuStyles.btnMobileLineBottom)
    )
  ).render

  private lazy val navElement = navigation(
    NavItem(ExternalUrls.guide, "Documentation"),
    NavItem(ExternalUrls.releases, "Changelog"),
    NavItem(ExternalUrls.license, "License")
  )

  private lazy val template = header(HeaderStyles.header)(
    div(GlobalStyles.body, GlobalStyles.clearfix, HomepageStyles.body)(
      div(HeaderStyles.headerLeft)(
        btnMobileMenu,
        a(HeaderStyles.headerLogo, href := HelloState.url)(),
        navElement
      ),
      buttons
    )
  ).render

  locally {
    jQ(org.scalajs.dom.window).on("scroll", onScroll)

    jQ(btnMobileMenu).on(EventName.click, (_: Element, jqEvent: JQueryEvent) => {
      jqEvent.preventDefault()
      toggleBooleanAttribute(navElement, Attributes.data(Attributes.Active))
      toggleBooleanAttribute(btnMobileMenu, Attributes.data(Attributes.Active))
    })
  }

  private def onScroll(el: Element, ev: JQueryEvent): Unit = {
    val pinnedAttr: String = Attributes.data(Attributes.Pinned)

    val pin = template.attributes.get(pinnedAttr).exists(_.value == "true")
    val scrollTop = org.scalajs.dom.window.scrollY
    val introHeight = document.querySelector(s".${HomepageStyles.sectionIntro.className}").getBoundingClientRect().height
    if (scrollTop >= introHeight && !pin) {
      template.setAttribute(pinnedAttr, "true")
    } else if (scrollTop < introHeight && pin) {
      template.setAttribute(pinnedAttr, "false")
    }
  }

  def getTemplate: Modifier = template

  private def toggleBooleanAttribute(element: Element, attribute: String): Unit = {
    val activeOption = element.attributes.get(attribute).map(_.value)
    val newValue = activeOption.isEmpty || !activeOption.get.toBoolean
    element.setAttribute(attribute, newValue.toString)
  }
}
