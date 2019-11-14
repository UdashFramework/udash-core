package io.udash.web.homepage.components

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
import scalatags.JsDom.all._

object Header extends HeaderButtons with HeaderNav {

  override val buttonStyles: HeaderButtonsStyles = HeaderStyles
  override val navStyles: HeaderNavStyles = HeaderStyles

  private val window = jQ(org.scalajs.dom.window)
  window.on("scroll", onScroll)

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
        a(HeaderStyles.headerLogo, href := IndexState(None).url)(),
        navElement
      ),
      buttons
    )
  ).render

  private lazy val jqNav = jQ(navElement)
  private lazy val jqMobileButton = jQ(btnMobileMenu)

  jqMobileButton.on(EventName.click, (_: Element, jqEvent: JQueryEvent) => {
    jqEvent.preventDefault()
    toggleBooleanAttribute(jqNav, Attributes.data(Attributes.Active))
    toggleBooleanAttribute(jqMobileButton, Attributes.data(Attributes.Active))
  })

  private def onScroll(el: Element, ev: JQueryEvent): Unit = {
    val pinnedAttr: String = Attributes.data(Attributes.Pinned)

    val pin = jQ(template).attr(pinnedAttr).getOrElse("false").toBoolean
    val scrollTop = jQ(org.scalajs.dom.window).scrollTop()
    val introHeight = jQ(s".${HomepageStyles.sectionIntro.className}").height()

    if (scrollTop >= introHeight && !pin) {
      jQ(template).attr(pinnedAttr, "true")
    } else if (scrollTop < introHeight && pin) {
      jQ(template).attr(pinnedAttr, "false")
    }
  }

  def getTemplate: Modifier = template

  private def toggleBooleanAttribute(jqElement: JQuery, attribute: String): Unit = {
    val activeOption = jqElement.attr(attribute)
    val newValue = if (activeOption.isEmpty || !activeOption.get.toBoolean) true else false

    jqElement.attr(Attributes.data(Attributes.Active), newValue.toString)
  }
}
