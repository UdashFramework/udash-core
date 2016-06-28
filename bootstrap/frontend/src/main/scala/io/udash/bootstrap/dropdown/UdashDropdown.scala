package io.udash.bootstrap
package dropdown

import io.udash._
import io.udash.bootstrap.UdashBootstrap.ComponentId
import io.udash.bootstrap.button.UdashButton
import org.scalajs.dom

import scalatags.JsDom.all._

class UdashDropdown[ItemType, ElemType <: Property[ItemType]] private
                   (val items: properties.SeqProperty[ItemType, ElemType], dropup: Boolean = false, dropdownId: ComponentId = UdashBootstrap.newId())
                   (itemFactory: (ElemType) => dom.Element)(mds: Modifier*)
  extends Listenable[UdashDropdown[ItemType, ElemType], UdashDropdown.DropdownEvent[ItemType, ElemType]] {

  import UdashDropdown._
  import io.udash.wrappers.jquery._

  private def withSelectionListener(elem: dom.Element, id: Int): dom.Element = {
    jQ(elem).click(jQFire(SelectionEvent(this, items.get(id))))
    elem
  }

  lazy val render: dom.Element = {
    import BootstrapTags._
    var _id = -1
    def next(): Int = {
      _id += 1
      _id
    }
    val el = div(BootstrapStyles.Button.btnGroup, BootstrapStyles.Dropdown.dropup.styleIf(dropup))(
      UdashButton()(
        BootstrapStyles.Dropdown.dropdownToggle, id := dropdownId.id, dataToggle := "dropdown", aria.haspopup := true, aria.expanded := false,
        mds, span(BootstrapStyles.Dropdown.caret)
      ).render,
      ul(BootstrapStyles.Dropdown.dropdownMenu, aria.labelledby := dropdownId.id)(
        repeat(items)((p) => withSelectionListener(itemFactory(p), next()))
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
        BootstrapStyles.Dropdown.dropdownToggle, id := dropdownId.id, dataToggle := "dropdown",  href := "#",
        aria.haspopup := true, aria.expanded := false,
        mds, span(BootstrapStyles.Dropdown.caret)
      ),
      ul(BootstrapStyles.Dropdown.dropdownMenu, aria.labelledby := dropdownId.id)(
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

  sealed trait DefaultDropdownItem
  case class DropdownLink(title: String, url: Url) extends DefaultDropdownItem
  case class DropdownHeader(title: String) extends DefaultDropdownItem
  case object DropdownDivider extends DefaultDropdownItem
  case class DropdownDisabled(link: DropdownLink) extends DefaultDropdownItem

  def defaultItemFactory(p: Property[DefaultDropdownItem]): dom.Element = {
    import io.udash._
    def itemFactory(item: DefaultDropdownItem): dom.Element = item match {
      case DropdownLink(title, url) => li(a(href := url.value)(produce(p)(_ => span(title).render))).render
      case DropdownHeader(title) => li(BootstrapStyles.Dropdown.dropdownHeader)(produce(p)(_ => span(title).render)).render
      case DropdownDivider => li(BootstrapStyles.divider, role := "separator").render
      case DropdownDisabled(item) => itemFactory(item).styles(BootstrapStyles.disabled)
    }

    itemFactory(p.get)
  }

  def apply[ItemType, ElemType <: Property[ItemType]]
           (items: properties.SeqProperty[ItemType, ElemType])
           (itemFactory: (ElemType) => dom.Element, mds: Modifier*): UdashDropdown[ItemType, ElemType] =
    new UdashDropdown(items)(itemFactory)(mds)

  def dropup[ItemType, ElemType <: Property[ItemType]]
            (items: properties.SeqProperty[ItemType, ElemType])
            (itemFactory: (ElemType) => dom.Element, mds: Modifier*): UdashDropdown[ItemType, ElemType] =
    new UdashDropdown(items, true)(itemFactory)(mds)
}