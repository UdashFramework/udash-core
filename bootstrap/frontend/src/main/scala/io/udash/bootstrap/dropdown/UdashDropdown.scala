package io.udash.bootstrap
package dropdown

import io.udash._
import io.udash.bootstrap.UdashBootstrap.ComponentId
import io.udash.bootstrap.button.UdashButton
import io.udash.properties.seq
import io.udash.wrappers.jquery.JQuery
import org.scalajs.dom

import scala.scalajs.js
import scalatags.JsDom.all._

class UdashDropdown[ItemType, ElemType <: Property[ItemType]] private
                   (val items: seq.SeqProperty[ItemType, ElemType], dropup: Boolean, override val componentId: ComponentId)
                   (itemFactory: (ElemType) => dom.Element)(content: Modifier*)
  extends UdashBootstrapComponent with Listenable[UdashDropdown[ItemType, ElemType], UdashDropdown.DropdownEvent[ItemType, ElemType]] {

  import UdashDropdown._
  import io.udash.wrappers.jquery._

  /** Dropdown menu list ID. */
  val menuId: ComponentId = UdashBootstrap.newId()
  /** Dropdown button ID. */
  val buttonId: ComponentId = UdashBootstrap.newId()

  /** Toggles menu visibility. */
  def toggle(): Unit =
    jQ(s"#$buttonId").asDropdown().dropdown("toggle")

  private def withSelectionListener(elem: dom.Element, id: Int): dom.Element = {
    jQ(elem).click(jQFire(SelectionEvent(this, items.get(id))))
    elem
  }

  override lazy val render: dom.Element = {
    import BootstrapTags._
    var _id = -1
    def next(): Int = {
      _id += 1
      _id
    }
    val el = div(id := componentId, BootstrapStyles.Button.btnGroup, BootstrapStyles.Dropdown.dropup.styleIf(dropup))(
      UdashButton()(
        BootstrapStyles.Dropdown.dropdownToggle, id := buttonId, dataToggle := "dropdown", aria.haspopup := true, aria.expanded := false,
        content, span(BootstrapStyles.Dropdown.caret)
      ).render,
      ul(BootstrapStyles.Dropdown.dropdownMenu, aria.labelledby := buttonId, id := menuId)(
        repeat(items)((item) => withSelectionListener(itemFactory(item), next()))
      )
    ).render

    val jQEl = jQ(el)
    jQEl.on("show.bs.dropdown", jQFire(DropdownShowEvent(this)))
    jQEl.on("shown.bs.dropdown", jQFire(DropdownShownEvent(this)))
    jQEl.on("hide.bs.dropdown", jQFire(DropdownHideEvent(this)))
    jQEl.on("hidden.bs.dropdown", jQFire(DropdownHiddenEvent(this)))
    el
  }

  lazy val linkRender: dom.Node = {
    import BootstrapTags._
    var _id = -1
    def next(): Int = {
      _id += 1
      _id
    }
    Seq(
      a(
        BootstrapStyles.Dropdown.dropdownToggle, id := buttonId, dataToggle := "dropdown", href := "#",
        aria.haspopup := true, aria.expanded := false,
        content, span(BootstrapStyles.Dropdown.caret)
      ),
      ul(BootstrapStyles.Dropdown.dropdownMenu, aria.labelledby := buttonId, id := menuId)(
        repeat(items)((p) => withSelectionListener(itemFactory(p), next()))
      )
    ).render
  }
}

object UdashDropdown {
  sealed abstract class DropdownEvent[ItemType, ElemType <: Property[ItemType]](override val source: UdashDropdown[ItemType, ElemType]) extends ListenableEvent[UdashDropdown[ItemType, ElemType]]
  case class DropdownShowEvent[ItemType, ElemType <: Property[ItemType]](dropdown: UdashDropdown[ItemType, ElemType]) extends DropdownEvent(dropdown)
  case class DropdownShownEvent[ItemType, ElemType <: Property[ItemType]](dropdown: UdashDropdown[ItemType, ElemType]) extends DropdownEvent(dropdown)
  case class DropdownHideEvent[ItemType, ElemType <: Property[ItemType]](dropdown: UdashDropdown[ItemType, ElemType]) extends DropdownEvent(dropdown)
  case class DropdownHiddenEvent[ItemType, ElemType <: Property[ItemType]](dropdown: UdashDropdown[ItemType, ElemType]) extends DropdownEvent(dropdown)
  case class SelectionEvent[ItemType, ElemType <: Property[ItemType]](dropdown: UdashDropdown[ItemType, ElemType], item: ItemType) extends DropdownEvent(dropdown)

  /** Default dropdown elements. */
  sealed trait DefaultDropdownItem
  case class DropdownLink(title: String, url: Url) extends DefaultDropdownItem
  case class DropdownHeader(title: String) extends DefaultDropdownItem
  case object DropdownDivider extends DefaultDropdownItem
  case class DropdownDisabled(link: DropdownLink) extends DefaultDropdownItem

  /** Renders DOM element for [[io.udash.bootstrap.dropdown.UdashDropdown.DefaultDropdownItem]]. */
  def defaultItemFactory(p: Property[DefaultDropdownItem]): dom.Element = {
    def itemFactory(item: DefaultDropdownItem): dom.Element = item match {
      case DropdownLink(title, url) => li(a(href := url.value)(produce(p)(_ => span(title).render))).render
      case DropdownHeader(title) => li(BootstrapStyles.Dropdown.dropdownHeader)(produce(p)(_ => span(title).render)).render
      case DropdownDivider => li(BootstrapStyles.divider, role := "separator").render
      case DropdownDisabled(item) => itemFactory(item).styles(BootstrapStyles.disabled)
    }

    itemFactory(p.get)
  }

  /**
    * Creates dropdown component.
    * More: <a href="http://getbootstrap.com/javascript/#dropdowns">Bootstrap Docs</a>.
    *
    * @param items Data items which will be represented as links in dropdown menu.
    * @param itemFactory Creates DOM element which is inserted into dropdown menu.
    * @param content Content of the element opening dropdown.
    * @tparam ItemType Single element type in `items`.
    * @tparam ElemType Type of the property containing every element in `items` sequence.
    * @return `UdashDropdown` component, call render to create DOM element.
    */
  def apply[ItemType, ElemType <: Property[ItemType]]
           (items: seq.SeqProperty[ItemType, ElemType], componentId: ComponentId = UdashBootstrap.newId())
           (itemFactory: (ElemType) => dom.Element)
           (content: Modifier*): UdashDropdown[ItemType, ElemType] =
    new UdashDropdown(items, false, componentId)(itemFactory)(content)

  /**
    * Creates dropup component.
    * More: <a href="http://getbootstrap.com/javascript/#dropdowns">Bootstrap Docs</a>.
    *
    * @param items Data items which will be represented as links in dropdown menu.
    * @param componentId Id of the root DOM node.
    * @param itemFactory Creates DOM element which is inserted into dropdown menu.
    * @param content Content of the element opening dropdown.
    * @tparam ItemType Single element type in `items`.
    * @tparam ElemType Type of the property containing every element in `items` sequence.
    * @return `UdashDropdown` component, call render to create DOM element.
    */
  def dropup[ItemType, ElemType <: Property[ItemType]]
            (items: seq.SeqProperty[ItemType, ElemType], componentId: ComponentId = UdashBootstrap.newId())
            (itemFactory: (ElemType) => dom.Element)
            (content: Modifier*): UdashDropdown[ItemType, ElemType] =
    new UdashDropdown(items, true, componentId)(itemFactory)(content)

  @js.native
  private trait UdashDropdownJQuery extends JQuery {
    def dropdown(arg: String): UdashDropdownJQuery = js.native
  }

  private implicit class JQueryDropdownExt(jQ: JQuery) {
    def asDropdown(): UdashDropdownJQuery =
      jQ.asInstanceOf[UdashDropdownJQuery]
  }
}