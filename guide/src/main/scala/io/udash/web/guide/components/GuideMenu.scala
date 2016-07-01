package io.udash.web.guide.components

import io.udash.web.guide.styles.partials.{GuideStyles, MenuStyles}
import io.udash.web.guide.{Context, _}
import io.udash.properties.Property
import io.udash.web.commons.styles.attributes.Attributes
import io.udash.web.commons.styles.components.MobileMenuStyles
import io.udash.web.commons.views.{SVG, Size}
import io.udash.wrappers.jquery._

import scalacss.ScalatagsCss._
import scalatags.JsDom.TypedTag
import scalatags.JsDom.tags2._
import org.scalajs.dom.raw.Element

import scala.annotation.tailrec

sealed trait MenuEntry {
  def name: String
}

case class MenuContainer(override val name: String, children: Seq[MenuLink]) extends MenuEntry
case class MenuLink(override val name: String, state: RoutingState) extends MenuEntry

class GuideMenu(entries: Seq[MenuEntry], property: Property[String]) {
  import scalatags.JsDom.all._

  private val ClickEvent = "click"
  private val ActiveItemSelector = s".${MenuStyles.get.subToggle.htmlClass}[${Attributes.data(Attributes.Active)} = 'true']"
  private val InactiveItemSelector = s".${MenuStyles.get.subToggle.htmlClass}[${Attributes.data(Attributes.Active)} = 'false']"

  private def getMenuElementTag(entry: MenuEntry): TypedTag[Element] = {
    entry match {
      case MenuLink(name, state) =>
        li(MenuStyles.get.item)(
          a(href := state.url(Context.applicationInstance), MenuStyles.get.link, data("id") := entry.name)(
            span(MenuStyles.get.linkText)(entry.name)
          )
        )
      case MenuContainer(name, children) =>
        li(MenuStyles.get.subItem)(
          a(href := "#", MenuStyles.get.subToggle, data("id") := entry.name)(
            span(MenuStyles.get.linkText)(entry.name),
            i(MenuStyles.get.icon)(SVG("icon_submenu.svg#icon_submenu", Size(7, 11)))
          ),
          ul(MenuStyles.get.subList)(
            children.map(subEntry => getMenuElementTag(subEntry))
          )
        )
    }
  }

  private lazy val btnMenu = a(href := "#", MobileMenuStyles.get.btnMobile, MenuStyles.get.btnMobile)(
    div(MobileMenuStyles.get.btnMobileLines)(
      span(MobileMenuStyles.get.btnMobileLineTop),
      span(MobileMenuStyles.get.btnMobileLineMiddle),
      span(MobileMenuStyles.get.btnMobileLineBottom)
    )
  ).render

  private lazy val template = div(MenuStyles.get.guideMenu)(
    nav(
      ul(
        entries.map(entry => getMenuElementTag(entry))
      )
    ),
    btnMenu
  ).render

  private lazy val menuSubs = jQ(template).find(s".${MenuStyles.get.subToggle.htmlClass}")
  private lazy val menuItems = jQ(template).find(s".${MenuStyles.get.link.htmlClass}")

  private lazy val jqMobileButton = jQ(template).find(s".${MenuStyles.get.btnMobile.htmlClass}")

  def getTemplate: Element = {
    initListeners()
    template
  }

  def initListeners(): Unit = {
    Context.applicationInstance.onStateChange(event => {
      property.set(event.currentState.url(Context.applicationInstance))
    })

    property.listen(onCurrentItemUpdate)

    menuSubs.on(ClickEvent, onSubClick)

    jqMobileButton.on(ClickEvent, onMobileMenuClick)
  }

  private def onCurrentItemUpdate(update: String) = {
    val jqElement = menuItemByUrl(update)
    menuItems.not(jqElement).attr(Attributes.data(Attributes.Active), "false")
    jqElement.attr(Attributes.data(Attributes.Active), "true")

    val parentSub = jqElement.closest(s".${MenuStyles.get.subList.htmlClass}")
    parentSub.stop().slideDown()
    parentSub.parent().find(s".${MenuStyles.get.subToggle.htmlClass}").attr(Attributes.data(Attributes.Active), "true")

    jQ(s".${GuideStyles.get.menuWrapper.htmlClass}").attr(Attributes.data(Attributes.Active), "false")
  }

  @tailrec
  private def menuItemByUrl(url: String): JQuery = {
    val item = jQ(template).find(s".${MenuStyles.get.link.htmlClass}[href = '$url']")
    if (item.length > 0) item else menuItemByUrl(url.substring(0, url.lastIndexOf("/")))
  }

  private lazy val onSubClick: JQueryCallback = (jqThis: Element, jqEvent: JQueryEvent) => {
    jqEvent.preventDefault()

    toggleBooleanAttribute(jQ(jqThis), Attributes.data(Attributes.Active))

    jQ(template).find(ActiveItemSelector).parent().find(s".${MenuStyles.get.subList.htmlClass}").stop().slideDown()
    jQ(template).find(InactiveItemSelector).parent().find(s".${MenuStyles.get.subList.htmlClass}").stop().slideUp()
  }

  private lazy val onMobileMenuClick: JQueryCallback = (jqThis: Element, jqEvent: JQueryEvent) => {
    jqEvent.preventDefault()

    toggleBooleanAttribute(jQ(s".${GuideStyles.get.menuWrapper.htmlClass}"), Attributes.data(Attributes.Active))
    toggleBooleanAttribute(jqMobileButton, Attributes.data(Attributes.Active))
  }

  private def toggleBooleanAttribute(jqElement: JQuery, attribute: String): Unit = {
    val activeOption = jqElement.attr(attribute)
    val newValue = if (activeOption.isEmpty || !activeOption.get.toBoolean) true else false

    jqElement.attr(Attributes.data(Attributes.Active), newValue.toString)
  }
}

object GuideMenu {
  import Context._

  private val property = Property[String]

  def apply(): GuideMenu = {
    new GuideMenu(Context.mainMenuEntries, property)
  }
}

