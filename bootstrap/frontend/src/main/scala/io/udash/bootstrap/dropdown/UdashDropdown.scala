package io.udash.bootstrap
package dropdown

import io.udash._
import org.scalajs.dom

import scalacss.ScalatagsCss._
import scalatags.JsDom.all._

private class UdashDropdownGenerator[T](title: dom.Element, dropdownId: String, items: SeqProperty[T])(itemFactory: (T) => dom.Element) {
  def dropdown: dom.Element = {
    import BootstrapTags._
    div(BootstrapStyles.Dropdown.dropdown)(
      //todo remove dep
      com.karasiq.bootstrap.Bootstrap.button(BootstrapStyles.Dropdown.dropdownToggle, id := dropdownId, dataToggle := "dropdown", aria.haspopup := true, aria.expanded := false)(
        title,
        raw("&nbsp"),
        span(BootstrapStyles.Dropdown.caret)
      ),
      ul(BootstrapStyles.Dropdown.dropdownMenu, aria.labelledby := dropdownId)(
        repeat(items)((p) => itemFactory(p.get))
      )
    ).render
  }

  def dropup: dom.Element = dropdown.styles(BootstrapStyles.Dropdown.dropup)
}

object UdashDropdown {

  sealed trait DefaultDropdownItem

  case class DropdownLink(title: String, url: Url) extends DefaultDropdownItem

  case class DropdownHeader(title: String) extends DefaultDropdownItem

  case object DropdownDivider extends DefaultDropdownItem

  case class DropdownDisabled(item: DefaultDropdownItem) extends DefaultDropdownItem

  val defaultItemFactory: (DefaultDropdownItem) => dom.Element = {
    case DropdownLink(title, url) => li(a(href := url.value)(title)).render
    case DropdownHeader(title) => li(BootstrapStyles.Dropdown.dropdownHeader)(title).render
    case DropdownDivider => li(BootstrapStyles.divider, role := "separator").render
    case DropdownDisabled(item) => defaultItemFactory(item).styles(BootstrapStyles.disabled)
  }

  private def create[T](title: dom.Element, items: SeqProperty[T])(itemFactory: (T) => dom.Element): UdashDropdownGenerator[T] = {
    import com.karasiq.bootstrap.Bootstrap
    new UdashDropdownGenerator(title, Bootstrap.newId, items)(itemFactory)
  }

  def apply[T](title: dom.Element, items: SeqProperty[T])(itemFactory: (T) => dom.Element): dom.Element =
    create(title, items)(itemFactory).dropdown

  def apply[T](title: String, items: SeqProperty[T])(itemFactory: (T) => dom.Element): dom.Element =
    create(span(title).render, items)(itemFactory).dropdown

  def apply(title: String, items: SeqProperty[DefaultDropdownItem]): dom.Element =
    create(span(title).render, items)(defaultItemFactory).dropdown

  def dropup[T](title: dom.Element, items: SeqProperty[T])(itemFactory: (T) => dom.Element): dom.Element =
    create(title, items)(itemFactory).dropup

  def dropup[T](title: String, items: SeqProperty[T])(itemFactory: (T) => dom.Element): dom.Element =
    create(span(title).render, items)(itemFactory).dropup

  def dropup(title: String, items: SeqProperty[DefaultDropdownItem]): dom.Element =
    create(span(title).render, items)(defaultItemFactory).dropup


}