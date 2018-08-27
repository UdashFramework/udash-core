package io.udash.bootstrap
package dropdown

import com.avsystem.commons.misc._
import io.udash._
import io.udash.bindings.modifiers.Binding
import io.udash.bootstrap.button.UdashButton
import io.udash.bootstrap.utils._
import io.udash.properties.seq
import io.udash.properties.single.ReadableProperty
import io.udash.wrappers.jquery.JQuery
import org.scalajs.dom
import org.scalajs.dom.Event
import scalatags.JsDom.all._

import scala.scalajs.js

final class UdashDropdown[ItemType, ElemType <: ReadableProperty[ItemType]] private(
  items: seq.ReadableSeqProperty[ItemType, ElemType],
  dropDirection: ReadableProperty[UdashDropdown.Direction],
  rightAlignMenu: ReadableProperty[Boolean],
  override val componentId: ComponentId
)(
  itemFactory: (ElemType, Binding.NestedInterceptor) => dom.Element,
  buttonContent: Binding.NestedInterceptor => Modifier
) extends UdashBootstrapComponent
  with Listenable[UdashDropdown[ItemType, ElemType], UdashDropdown.DropdownEvent[ItemType, ElemType]] {

  import UdashDropdown._
  import io.udash.bootstrap.dropdown.UdashDropdown.DropdownEvent._
  import io.udash.css.CssView._
  import io.udash.wrappers.jquery._

  /** Dropdown menu list ID. */
  val menuId: ComponentId = ComponentId.newId()
  /** Dropdown button ID. */
  val buttonId: ComponentId = ComponentId.newId()

  /** Toggles menu visibility. */
  def toggle(): Unit =
    jQ(s"#${buttonId.id}").asInstanceOf[UdashDropdownJQuery].dropdown("toggle")

  /** Updates the position of an element’s dropdown. */
  def update(): Unit =
    jQ(s"#${buttonId.id}").asInstanceOf[UdashDropdownJQuery].dropdown("update")

  private def withSelectionListener(elem: dom.Element, item: ElemType): dom.Element = {
    jQ(elem).on(EventName.click, jQFire(SelectionEvent(this, item.get)))
    elem
  }

  override lazy val render: dom.Element = {
    import io.udash.bootstrap.utils.BootstrapTags._
    val el = div(
      id := componentId, BootstrapStyles.Button.group,
      nestedInterceptor(
        ((direction: Direction) => direction match {
          case Direction.Up => BootstrapStyles.Dropdown.dropup
          case Direction.Down => BootstrapStyles.Dropdown.dropdown
          case Direction.Left => BootstrapStyles.Dropdown.dropleft
          case Direction.Right => BootstrapStyles.Dropdown.dropright
        }).reactiveApply(dropDirection)
      )
    )(
      UdashButton() { nested => Seq[Modifier](
        BootstrapStyles.Dropdown.toggle, id := buttonId, dataToggle := "dropdown",
        aria.haspopup := true, aria.expanded := false,
        buttonContent(nested), span(BootstrapStyles.Dropdown.caret)
      )}.render,
      div(
        BootstrapStyles.Dropdown.menu,
        nestedInterceptor(BootstrapStyles.Dropdown.menuRight.styleIf(rightAlignMenu)),
        aria.labelledby := buttonId, id := menuId
      )(
        nestedInterceptor(
          repeatWithNested(items) { case (item, nested) =>
            withSelectionListener(itemFactory(item, nested), item)
          }
        )
      )
    ).render

    val jQEl = jQ(el)
    jQEl.on("show.bs.dropdown", jQFire(VisibilityChangeEvent(this, EventType.Show)))
    jQEl.on("shown.bs.dropdown", jQFire(VisibilityChangeEvent(this, EventType.Shown)))
    jQEl.on("hide.bs.dropdown", jQFire(VisibilityChangeEvent(this, EventType.Hide)))
    jQEl.on("hidden.bs.dropdown", jQFire(VisibilityChangeEvent(this, EventType.Hidden)))
    el
  }
}

object UdashDropdown {
  sealed abstract class DropdownEvent[ItemType, ElemType <: ReadableProperty[ItemType]](
    override val source: UdashDropdown[ItemType, ElemType],
    val tpe: DropdownEvent.EventType
  ) extends ListenableEvent[UdashDropdown[ItemType, ElemType]]

  object DropdownEvent {
    final class EventType(implicit enumCtx: EnumCtx) extends AbstractValueEnum
    object EventType extends AbstractValueEnumCompanion[EventType] {
      final val Show, Shown, Hide, Hidden, Selection: Value = new EventType
    }

    case class VisibilityChangeEvent[ItemType, ElemType <: ReadableProperty[ItemType]](
      override val source: UdashDropdown[ItemType, ElemType],
      override val tpe: DropdownEvent.EventType
    ) extends DropdownEvent(source, tpe) with CaseMethods

    case class SelectionEvent[ItemType, ElemType <: ReadableProperty[ItemType]](
      override val source: UdashDropdown[ItemType, ElemType], item: ItemType
    ) extends DropdownEvent(source, EventType.Selection) with CaseMethods
  }

  final class Direction(implicit enumCtx: EnumCtx) extends AbstractValueEnum
  object Direction extends AbstractValueEnumCompanion[Direction] {
    final val Up, Down, Left, Right: Value = new Direction()
  }

  /** Default dropdown elements. */
  sealed trait DefaultDropdownItem
  object DefaultDropdownItem {
    case class Text(text: String) extends DefaultDropdownItem
    case class Link(title: String, url: Url) extends DefaultDropdownItem
    case class Button(title: String, clickCallback: () => Any) extends DefaultDropdownItem
    case class Header(title: String) extends DefaultDropdownItem
    case class Disabled(link: Link) extends DefaultDropdownItem
    case object Divider extends DefaultDropdownItem
  }

  /** Renders DOM element for [[io.udash.bootstrap.dropdown.UdashDropdown.DefaultDropdownItem]]. */
  def defaultItemFactory(
    item: ReadableProperty[DefaultDropdownItem], nestedInterceptor: Binding.NestedInterceptor
  ): dom.Element = {
    import DefaultDropdownItem._
    import io.udash.css.CssView._
    def itemFactory(item: DefaultDropdownItem): dom.Element = item match {
      case Text(text) =>
        p(text).render
      case Link(title, url) =>
        a(BootstrapStyles.Dropdown.item, href := url.value)(title).render
      case Button(title, callback) =>
        button(BootstrapStyles.Dropdown.item, onclick :+= ((_: Event) => { callback() }))(title).render
      case Header(title) =>
        h6(BootstrapStyles.Dropdown.header)(title).render
      case Disabled(item) =>
        itemFactory(item).styles(BootstrapStyles.disabled)
      case Divider =>
        div(BootstrapStyles.Dropdown.divider, role := "separator").render
    }

    span(nestedInterceptor(produce(item)(itemFactory))).render
  }

  // TODO update all Bootstrap docs links
  /**
    * Creates dropdown component.
    * More: <a href="http://getbootstrap.com/javascript/#dropdowns">Bootstrap Docs</a>.
    *
    * @param items          Data items which will be represented as links in dropdown menu.
    * @param dropDirection  Direction of menu expansion.
    * @param rightAlignMenu If true, the menu will be aligned to the right side of button.
    * @param itemFactory    Creates DOM element which is inserted into dropdown menu.
    * @param buttonContent  Content of the element opening dropdown.
    * @tparam ItemType Single element type in `items`.
    * @tparam ElemType Type of the property containing every element in `items` sequence.
    * @return `UdashDropdown` component, call render to create DOM element.
    */
  def apply[ItemType, ElemType <: ReadableProperty[ItemType]](
    items: seq.ReadableSeqProperty[ItemType, ElemType],
    dropDirection: ReadableProperty[Direction] = Direction.Down.toProperty,
    rightAlignMenu: ReadableProperty[Boolean] = UdashBootstrap.False,
    componentId: ComponentId = ComponentId.newId()
  )(
    itemFactory: (ElemType, Binding.NestedInterceptor) => dom.Element,
    buttonContent: Binding.NestedInterceptor => Modifier
  ): UdashDropdown[ItemType, ElemType] = {
    new UdashDropdown(items, dropDirection, rightAlignMenu, componentId)(itemFactory, buttonContent)
  }

  /**
    * Creates dropdown component.
    * More: <a href="http://getbootstrap.com/javascript/#dropdowns">Bootstrap Docs</a>.
    *
    * @param items          Data items which will be represented as links in dropdown menu.
    * @param dropDirection  Direction of menu expansion.
    * @param rightAlignMenu If true, the menu will be aligned to the right side of button.
    * @param buttonContent  Content of the element opening dropdown.
    * @return `UdashDropdown` component, call render to create DOM element.
    */
  def default[ElemType <: ReadableProperty[DefaultDropdownItem]](
    items: seq.ReadableSeqProperty[DefaultDropdownItem, ElemType],
    dropDirection: ReadableProperty[Direction] = Direction.Down.toProperty,
    rightAlignMenu: ReadableProperty[Boolean] = UdashBootstrap.False,
    componentId: ComponentId = ComponentId.newId()
  )(
    buttonContent: Binding.NestedInterceptor => Modifier
  ): UdashDropdown[DefaultDropdownItem, ElemType] = {
    new UdashDropdown(items, dropDirection, rightAlignMenu, componentId)(defaultItemFactory, buttonContent)
  }

  @js.native
  private trait UdashDropdownJQuery extends JQuery {
    def dropdown(arg: String): UdashDropdownJQuery = js.native
  }
}