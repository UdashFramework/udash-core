package io.udash.web.guide.components

import com.avsystem.commons.misc.OptArg
import io.udash._
import io.udash.web.commons.styles.attributes.Attributes
import io.udash.web.commons.styles.components.MobileMenuStyles
import io.udash.web.commons.views.{SVG, Size}
import io.udash.web.guide.styles.partials.{GuideStyles, MenuStyles}
import io.udash.web.guide.{Context, _}
import io.udash.wrappers.jquery._
import org.scalajs.dom.raw.Element
import scalatags.JsDom.TypedTag
import scalatags.JsDom.tags2._

import scala.annotation.tailrec

sealed trait MenuEntry {
  def name: String
}

case class MenuContainer(override val name: String, children: Seq[MenuLink]) extends MenuEntry
case class MenuLink(override val name: String, state: RoutingState, fragment: OptArg[String] = OptArg.Empty) extends MenuEntry

class GuideMenu(entries: Seq[MenuEntry], property: Property[String]) {

  import io.udash.css.CssView._
  import scalatags.JsDom.all._

  private val ClickEvent = "click"
  private val ActiveItemSelector = s".${MenuStyles.subToggle.className}[${Attributes.data(Attributes.Active)} = 'true']"
  private val InactiveItemSelector = s".${MenuStyles.subToggle.className}[${Attributes.data(Attributes.Active)} = 'false']"

  private def getMenuElementTag(entry: MenuEntry): TypedTag[Element] = entry match {
    case MenuLink(_, state, fragment) =>
      li(MenuStyles.item)(
        a(href := state.url(Context.applicationInstance) + fragment.fold("")("#" + _), MenuStyles.link, data("id") := entry.name)(
          span(MenuStyles.linkText)(entry.name)
        )
      )
    case MenuContainer(_, children) =>
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


  private lazy val btnMenu = a(href := "#", MobileMenuStyles.btnMobile, MenuStyles.btnMobile)(
    div(MobileMenuStyles.btnMobileLines)(
      span(MobileMenuStyles.btnMobileLineTop),
      span(MobileMenuStyles.btnMobileLineMiddle),
      span(MobileMenuStyles.btnMobileLineBottom)
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

  private lazy val menuSubs = jQ(template).find(s".${MenuStyles.subToggle.className}")
  private lazy val menuItems = jQ(template).find(s".${MenuStyles.link.className}")

  private lazy val jqMobileButton = jQ(template).find(s".${MenuStyles.btnMobile.className}")

  def getTemplate: Modifier = {
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

    val parentSub = jqElement.closest(s".${MenuStyles.subList.className}")
    parentSub.stop().slideDown()
    parentSub.parent().find(s".${MenuStyles.subToggle.className}").attr(Attributes.data(Attributes.Active), "true")

    jQ(s".${GuideStyles.menuWrapper.className}").attr(Attributes.data(Attributes.Active), "false")
  }

  @tailrec
  private def menuItemByUrl(url: String): JQuery = {
    if (url.trim.nonEmpty) {
      val item = jQ(template).find(s".${MenuStyles.link.className}[href = '$url']")
      if (item.length > 0) item else menuItemByUrl(url.substring(0, url.lastIndexOf("/")))
    } else jQ()
  }

  private lazy val onSubClick: JQueryCallback = (jqThis: Element, jqEvent: JQueryEvent) => {
    jqEvent.preventDefault()

    toggleBooleanAttribute(jQ(jqThis), Attributes.data(Attributes.Active))

    jQ(template).find(ActiveItemSelector).parent().find(s".${MenuStyles.subList.className}").stop().slideDown()
    jQ(template).find(InactiveItemSelector).parent().find(s".${MenuStyles.subList.className}").stop().slideUp()
  }

  private lazy val onMobileMenuClick: JQueryCallback = (jqThis: Element, jqEvent: JQueryEvent) => {
    jqEvent.preventDefault()

    toggleBooleanAttribute(jQ(s".${GuideStyles.menuWrapper.className}"), Attributes.data(Attributes.Active))
    toggleBooleanAttribute(jqMobileButton, Attributes.data(Attributes.Active))
  }

  private def toggleBooleanAttribute(jqElement: JQuery, attribute: String): Unit = {
    val activeOption = jqElement.attr(attribute)
    val newValue = if (activeOption.isEmpty || !activeOption.get.toBoolean) true else false

    jqElement.attr(Attributes.data(Attributes.Active), newValue.toString)
  }

  private val window = jQ(org.scalajs.dom.window)
  window.on("scroll", onScroll)

  private val originalMenuOffsetTop = 30

  private def onScroll(el: Element, ev: JQueryEvent): Unit = {
    val pinnedAttr: String = Attributes.data(Attributes.Pinned)

    val pin = jQ(template).attr(pinnedAttr).getOrElse("false").toBoolean
    val scrollTop = jQ(org.scalajs.dom.window).scrollTop()

    if (scrollTop >= originalMenuOffsetTop && !pin) {
      jQ(template).attr(pinnedAttr, "true")
    } else if (scrollTop < originalMenuOffsetTop && pin) {
      jQ(template).attr(pinnedAttr, "false")
    }
  }
}

object GuideMenu {

  private val property = Property.blank[String]

  def apply(): GuideMenu = {
    new GuideMenu(Context.mainMenuEntries, property)
  }
}

