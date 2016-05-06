package io.udash.guide.components

import io.udash.guide.styles.partials.{GuideStyles, MenuStyles}
import io.udash.guide.views.{SVG, Size}
import io.udash.guide.{Context, _}
import io.udash.properties.Property
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
  private val ActiveItemSelector = s".${MenuStyles.subToggle.htmlClass}[${GuideMenu.DataActiveAttribute} = 'true']"
  private val InactiveItemSelector = s".${MenuStyles.subToggle.htmlClass}[${GuideMenu.DataActiveAttribute} = 'false']"

  private def getMenuElementTag(entry: MenuEntry): TypedTag[Element] = {
    entry match {
      case MenuLink(name, state) =>
        li(MenuStyles.item)(
          a(href := state.url(Context.applicationInstance), MenuStyles.link, data("id") := entry.name)(
            span(MenuStyles.linkText)(entry.name)
          )
        )
      case MenuContainer(name, children) =>
        li(MenuStyles.subItem)(
          a(href := "#", MenuStyles.subToggle, data("id") := entry.name)(
            span(MenuStyles.linkText)(entry.name),
            i(MenuStyles.icon)(SVG("icon_submenu.svg#icon_submenu", Size(7, 11)))
          ),
          ul(MenuStyles.subList)(
            children.map(subEntry => getMenuElementTag(subEntry))
          )
        )
    }
  }

  private lazy val btnMenu = a(href := "#", MenuStyles.btnMobile)(
    div(MenuStyles.btnMobileLines)(
      span(MenuStyles.btnMobileLineTop),
      span(MenuStyles.btnMobileLineMiddle),
      span(MenuStyles.btnMobileLineBottom)
    )
  ).render

  private lazy val template = div(MenuStyles.guideMenu)(
    nav(
      ul(
        entries.map(entry => getMenuElementTag(entry))
      )
    ),
    btnMenu
  ).render

  private lazy val menuSubs = jQ(template).find(s".${MenuStyles.subToggle.htmlClass}")
  private lazy val menuItems = jQ(template).find(s".${MenuStyles.link.htmlClass}")

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

    jQ(template).find(s".${MenuStyles.btnMobile.htmlClass}").on(ClickEvent, onMobileMenuClick)
  }

  private def onCurrentItemUpdate(update: String) = {
    val jqElement = menuItemByUrl(update)
    menuItems.not(jqElement).attr(GuideMenu.DataActiveAttribute, "false")
    jqElement.attr(GuideMenu.DataActiveAttribute, "true")

    val parentSub = jqElement.closest(s".${MenuStyles.subList.htmlClass}")
    parentSub.stop().slideDown()
    parentSub.parent().find(s".${MenuStyles.subToggle.htmlClass}").attr(GuideMenu.DataActiveAttribute, "true")

    jQ(s".${GuideStyles.menuWrapper.htmlClass}").attr(GuideMenu.DataActiveAttribute, "false")
  }

  @tailrec
  private def menuItemByUrl(url: String): JQuery = {
    val item = jQ(template).find(s".${MenuStyles.link.htmlClass}[href = '$url']")
    if (item.length > 0) item else menuItemByUrl(url.substring(0, url.lastIndexOf("/")))
  }

  private lazy val onSubClick: JQueryCallback = (jqThis: Element, jqEvent: JQueryEvent) => {
    jqEvent.preventDefault()

    toggleBooleanAttribute(jQ(jqThis), GuideMenu.DataActiveAttribute)

    jQ(template).find(ActiveItemSelector).parent().find(s".${MenuStyles.subList.htmlClass}").stop().slideDown()
    jQ(template).find(InactiveItemSelector).parent().find(s".${MenuStyles.subList.htmlClass}").stop().slideUp()
  }

  private lazy val onMobileMenuClick: JQueryCallback = (jqThis: Element, jqEvent: JQueryEvent) => {
    jqEvent.preventDefault()

    toggleBooleanAttribute(jQ(s".${GuideStyles.menuWrapper.htmlClass}"), GuideMenu.DataActiveAttribute)
  }

  private def toggleBooleanAttribute(jqElement: JQuery, attribute: String): Unit = {
    val activeOption = jqElement.attr(attribute)
    val newValue = if (activeOption.isEmpty || !activeOption.get.toBoolean) true else false

    jqElement.attr(GuideMenu.DataActiveAttribute, newValue.toString)
  }
}

object GuideMenu {
  import Context._

  val DataActiveAttribute = "data-active"

  private val property = Property[String]

  def apply(): GuideMenu = {
    new GuideMenu(Context.mainMenuEntries, property)
  }
}

