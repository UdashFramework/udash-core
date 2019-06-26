package io.udash.bootstrap
package dropdown

import com.avsystem.commons.misc.AbstractCase
import io.udash._
import io.udash.bootstrap.button.UdashButton
import io.udash.bootstrap.utils.UdashBootstrapComponent
import io.udash.properties.seq
import io.udash.wrappers.jquery.JQuery
import org.scalajs.dom.{Element, Node}
import scalatags.JsDom.all._

import scala.scalajs.js

final class UdashDropdown[ItemType, ElemType <: ReadableProperty[ItemType]] private
                         (val items: seq.ReadableSeqProperty[ItemType, ElemType], dropup: Boolean,
                          override val componentId: ComponentId)
                         (itemFactory: (ElemType) => Element)(content: Modifier*)
  extends UdashBootstrapComponent with Listenable[UdashDropdown[ItemType, ElemType], UdashDropdown.DropdownEvent[ItemType, ElemType]] {

  import UdashDropdown._
  import io.udash.css.CssView._
  import io.udash.wrappers.jquery._

  /** Dropdown menu list ID. */
  val menuId: ComponentId = ComponentId.newId()
  /** Dropdown button ID. */
  val buttonId: ComponentId = ComponentId.newId()

  /** Toggles menu visibility. */
  def toggle(): Unit =
    jQ(s"#$buttonId").asInstanceOf[UdashDropdownJQuery].dropdown("toggle")

  private def withSelectionListener(elem: Element, id: Int): Element = {
    jQ(elem).on(EventName.click, (_: Element, _: JQueryEvent) => fire(SelectionEvent(this, items.get(id))))
    elem
  }

  override lazy val render: Element = {
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
    jQEl.on("show.bs.dropdown", (_: Element, _: JQueryEvent) => fire(DropdownShowEvent(this)))
    jQEl.on("shown.bs.dropdown", (_: Element, _: JQueryEvent) => fire(DropdownShownEvent(this)))
    jQEl.on("hide.bs.dropdown", (_: Element, _: JQueryEvent) => fire(DropdownHideEvent(this)))
    jQEl.on("hidden.bs.dropdown", (_: Element, _: JQueryEvent) => fire(DropdownHiddenEvent(this)))
    el
  }

  lazy val linkRender: Node = {
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
  sealed abstract class DropdownEvent[ItemType, ElemType <: ReadableProperty[ItemType]](
    override val source: UdashDropdown[ItemType, ElemType]
  ) extends AbstractCase with ListenableEvent[UdashDropdown[ItemType, ElemType]]
  case class DropdownShowEvent[ItemType, ElemType <: ReadableProperty[ItemType]](dropdown: UdashDropdown[ItemType, ElemType]) extends DropdownEvent(dropdown)
  case class DropdownShownEvent[ItemType, ElemType <: ReadableProperty[ItemType]](dropdown: UdashDropdown[ItemType, ElemType]) extends DropdownEvent(dropdown)
  case class DropdownHideEvent[ItemType, ElemType <: ReadableProperty[ItemType]](dropdown: UdashDropdown[ItemType, ElemType]) extends DropdownEvent(dropdown)
  case class DropdownHiddenEvent[ItemType, ElemType <: ReadableProperty[ItemType]](dropdown: UdashDropdown[ItemType, ElemType]) extends DropdownEvent(dropdown)
  case class SelectionEvent[ItemType, ElemType <: ReadableProperty[ItemType]](dropdown: UdashDropdown[ItemType, ElemType], item: ItemType) extends DropdownEvent(dropdown)

  /** Default dropdown elements. */
  sealed trait DefaultDropdownItem
  case class DropdownLink(title: String, url: Url) extends DefaultDropdownItem
  case class DropdownHeader(title: String) extends DefaultDropdownItem
  case object DropdownDivider extends DefaultDropdownItem
  case class DropdownDisabled(link: DropdownLink) extends DefaultDropdownItem

  /** Renders DOM element for [[io.udash.bootstrap.dropdown.UdashDropdown.DefaultDropdownItem]]. */
  def defaultItemFactory(p: ReadableProperty[DefaultDropdownItem]): Element = {
    import io.udash.css.CssView._
    def itemFactory(item: DefaultDropdownItem): Element = item match {
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
  def apply[ItemType, ElemType <: ReadableProperty[ItemType]]
           (items: seq.ReadableSeqProperty[ItemType, ElemType], componentId: ComponentId = ComponentId.newId())
           (itemFactory: (ElemType) => Element)
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
  def dropup[ItemType, ElemType <: ReadableProperty[ItemType]]
            (items: seq.ReadableSeqProperty[ItemType, ElemType], componentId: ComponentId = ComponentId.newId())
            (itemFactory: (ElemType) => Element)
            (content: Modifier*): UdashDropdown[ItemType, ElemType] =
    new UdashDropdown(items, true, componentId)(itemFactory)(content)

  @js.native
  private trait UdashDropdownJQuery extends JQuery {
    def dropdown(arg: String): UdashDropdownJQuery = js.native
  }
}