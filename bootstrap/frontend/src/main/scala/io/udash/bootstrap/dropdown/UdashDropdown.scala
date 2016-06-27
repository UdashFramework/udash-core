package io.udash.bootstrap
package dropdown

import io.udash._
import io.udash.bootstrap.UdashBootstrap.ComponentId
import io.udash.bootstrap.button.UdashButton
import org.scalajs.dom

import scalacss.ScalatagsCss._
import scalatags.JsDom.all._

class UdashDropdown[T] private(val items: SeqProperty[T], dropup: Boolean = false, dropdownId: ComponentId = UdashBootstrap.newId())
                              (itemFactory: (T) => dom.Element)(mds: Modifier*)
  extends Listenable[UdashDropdown[T], UdashDropdown.DropdownEvent[T]] {

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
        repeat(items)((p) => withSelectionListener(itemFactory(p.get), next()))
      )
    ).render

    val jQEl = jQ(el)
    jQEl.on("show.bs.dropdown", jQFire(DropdownShowEvent(this)))
    jQEl.on("shown.bs.dropdown", jQFire(DropdownShownEvent(this)))
    jQEl.on("hide.bs.dropdown", jQFire(DropdownHideEvent(this)))
    jQEl.on("hidden.bs.dropdown", jQFire(DropdownHiddenEvent(this)))
    el
  }
}

object UdashDropdown {

  sealed trait DropdownEvent[T] extends ListenableEvent[UdashDropdown[T]]

  case class DropdownShowEvent[T](source: UdashDropdown[T]) extends DropdownEvent[T]

  case class DropdownShownEvent[T](source: UdashDropdown[T]) extends DropdownEvent[T]

  case class DropdownHideEvent[T](source: UdashDropdown[T]) extends DropdownEvent[T]

  case class DropdownHiddenEvent[T](source: UdashDropdown[T]) extends DropdownEvent[T]

  case class SelectionEvent[T](source: UdashDropdown[T], item: T) extends DropdownEvent[T]

  sealed trait DefaultDropdownItem
  case class DropdownLink(title: String, url: Url) extends DefaultDropdownItem
  case class DropdownHeader(title: String) extends DefaultDropdownItem
  case object DropdownDivider extends DefaultDropdownItem
  case class DropdownDisabled(link: DropdownLink) extends DefaultDropdownItem

  val defaultItemFactory: (DefaultDropdownItem) => dom.Element = {
    case DropdownLink(title, url) => li(a(href := url.value)(title)).render
    case DropdownHeader(title) => li(BootstrapStyles.Dropdown.dropdownHeader)(title).render
    case DropdownDivider => li(BootstrapStyles.divider, role := "separator").render
    case DropdownDisabled(item) => defaultItemFactory(item).styles(BootstrapStyles.disabled)
  }

  def apply[T](items: SeqProperty[T], itemFactory: (T) => dom.Element)(mds: Modifier*): UdashDropdown[T] =
    new UdashDropdown(items)(itemFactory)(mds)

  def apply(items: SeqProperty[DefaultDropdownItem])(mds: Modifier*): UdashDropdown[DefaultDropdownItem] =
    new UdashDropdown(items)(defaultItemFactory)(mds)

  def dropup[T](items: SeqProperty[T], itemFactory: (T) => dom.Element)(mds: Modifier*): UdashDropdown[T] =
    new UdashDropdown(items, true)(itemFactory)(mds)

  def dropup(items: SeqProperty[DefaultDropdownItem])(mds: Modifier*): UdashDropdown[DefaultDropdownItem] =
    new UdashDropdown(items, true)(defaultItemFactory)(mds)
}