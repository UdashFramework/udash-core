package io.udash.bootstrap
package dropdown

import com.avsystem.commons.misc._
import io.udash._
import io.udash.bindings.modifiers.Binding
import io.udash.bindings.modifiers.Binding.NestedInterceptor
import io.udash.bootstrap.button.UdashButton
import io.udash.bootstrap.utils.{BootstrapStyles, UdashBootstrapComponent}
import io.udash.properties.seq
import io.udash.properties.single.ReadableProperty
import io.udash.wrappers.jquery.JQuery
import org.scalajs.dom.{Element, Event}
import scalatags.JsDom.all._

import scala.scalajs.js

final class UdashDropdown[ItemType, ElemType <: ReadableProperty[ItemType]] private(
  items: seq.ReadableSeqProperty[ItemType, ElemType],
  dropDirection: ReadableProperty[UdashDropdown.Direction],
  rightAlignMenu: ReadableProperty[Boolean],
  buttonToggle: ReadableProperty[Boolean],
  override val componentId: ComponentId
)(
  itemFactory: ItemType => Element,
  buttonContent: Binding.NestedInterceptor => Modifier,
  buttonFactory: (NestedInterceptor => Modifier) => UdashButton
) extends UdashBootstrapComponent
  with Listenable[UdashDropdown[ItemType, ElemType], UdashDropdown.DropdownEvent[ItemType, ElemType]] {

  import UdashDropdown._
  import io.udash.bootstrap.dropdown.UdashDropdown.DropdownEvent._
  import io.udash.css.CssView._
  import io.udash.wrappers.jquery._

  /** Dropdown menu list ID. */
  val menuId: ComponentId = componentId.subcomponent("menu")
  /** Dropdown button ID. */
  val buttonId: ComponentId = componentId.subcomponent("button")

  /** Toggles menu visibility. */
  def toggle(): Unit =
    jQSelector().dropdown("toggle")

  /** Updates the position of an element’s dropdown. */
  def update(): Unit =
    jQSelector().dropdown("update")

  private def withSelectionListener(elem: Element, item: ItemType): Element = {
    nestedInterceptor(new JQueryOnBinding(jQ(elem), EventName.click, (_: Element, _: JQueryEvent) => fire(SelectionEvent(this, item))))
    elem
  }

  propertyListeners += items.listen(_ => update())

  override lazy val render: Element = {
    import io.udash.bootstrap.utils.BootstrapTags._
    val el = div(
      id := componentId,
      nestedInterceptor(
        ((direction: Direction) => direction match {
          case Direction.Up => BootstrapStyles.Dropdown.dropup
          case Direction.Down => BootstrapStyles.Dropdown.dropdown
          case Direction.Left => BootstrapStyles.Dropdown.dropleft
          case Direction.Right => BootstrapStyles.Dropdown.dropright
        }).reactiveApply(dropDirection)
      )
    )(
      nestedInterceptor(buttonFactory { nested =>
        Seq[Modifier](
          nested(BootstrapStyles.Dropdown.toggle.styleIf(buttonToggle)), id := buttonId, dataToggle := "dropdown",
          aria.haspopup := true, aria.expanded := false,
          buttonContent(nested), span(BootstrapStyles.Dropdown.caret)
        )
      }),
      div(
        BootstrapStyles.Dropdown.menu,
        nestedInterceptor(BootstrapStyles.Dropdown.menuRight.styleIf(rightAlignMenu)),
        aria.labelledby := buttonId, id := menuId
      )(nestedInterceptor(
        produce(items.transform((item: ItemType) => withSelectionListener(itemFactory(item), item)))(el => el)
      ))
    ).render

    val jQEl = jQ(el)
    nestedInterceptor(new JQueryOnBinding(jQEl, "show.bs.dropdown", (_: Element, _: JQueryEvent) => fire(VisibilityChangeEvent(this, EventType.Show))))
    nestedInterceptor(new JQueryOnBinding(jQEl, "shown.bs.dropdown", (_: Element, _: JQueryEvent) => fire(VisibilityChangeEvent(this, EventType.Shown))))
    nestedInterceptor(new JQueryOnBinding(jQEl, "hide.bs.dropdown", (_: Element, _: JQueryEvent) => fire(VisibilityChangeEvent(this, EventType.Hide))))
    nestedInterceptor(new JQueryOnBinding(jQEl, "hidden.bs.dropdown", (_: Element, _: JQueryEvent) => fire(VisibilityChangeEvent(this, EventType.Hidden))))
    el
  }

  override def kill(): Unit = {
    super.kill()
    jQSelector().dropdown("dispose")
  }

  private def jQSelector(): UdashDropdownJQuery =
    jQ(s"#${buttonId.id}").asInstanceOf[UdashDropdownJQuery]
}

object UdashDropdown {
  /** More: <a href="http://getbootstrap.com/docs/4.1/components/dropdowns/#events">Bootstrap Docs</a> */
  sealed abstract class DropdownEvent[ItemType, ElemType <: ReadableProperty[ItemType]](
    override val source: UdashDropdown[ItemType, ElemType],
    val tpe: DropdownEvent.EventType
  ) extends ListenableEvent[UdashDropdown[ItemType, ElemType]]

  object DropdownEvent {
    final class EventType(implicit enumCtx: EnumCtx) extends AbstractValueEnum
    object EventType extends AbstractValueEnumCompanion[EventType] {
      /** This event fires immediately when the show instance method is called. */
      final val Show: Value = new EventType
      /** This event is fired when the dropdown has been made visible to the user (will wait for CSS transitions, to complete). */
      final val Shown: Value = new EventType
      /** This event is fired immediately when the hide instance method has been called. */
      final val Hide: Value = new EventType
      /** This event is fired when the dropdown has finished being hidden from the user (will wait for CSS transitions, to complete). */
      final val Hidden: Value = new EventType
      /** This event is fired on selection of any (except disabled ones) element from the dropdown. */
      final val Selection: Value = new EventType
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
  sealed trait DefaultDropdownItem extends AbstractCase
  object DefaultDropdownItem {
    case class Text(text: String) extends DefaultDropdownItem
    case class Link(title: String, url: Url) extends DefaultDropdownItem
    case class Button(title: String, clickCallback: () => Any) extends DefaultDropdownItem
    case class Header(title: String) extends DefaultDropdownItem
    case class Disabled(item: DefaultDropdownItem) extends DefaultDropdownItem
    case class Raw(element: Element) extends DefaultDropdownItem
    case object Divider extends DefaultDropdownItem
  }

  /** Renders DOM element for [[io.udash.bootstrap.dropdown.UdashDropdown.DefaultDropdownItem]]. */
  def defaultItemFactory(item: DefaultDropdownItem): Element = {
    import DefaultDropdownItem._
    import io.udash.css.CssView._
    item match {
      case Text(text) =>
        span(BootstrapStyles.Dropdown.itemText, text).render
      case Link(title, url) =>
        a(BootstrapStyles.Dropdown.item, href := url.value)(title).render
      case Button(title, callback) =>
        button(BootstrapStyles.Dropdown.item, onclick :+= ((_: Event) => {
          callback()
        }))(title).render
      case Header(title) =>
        h6(BootstrapStyles.Dropdown.header)(title).render
      case Disabled(item) =>
        val res = defaultItemFactory(item).styles(BootstrapStyles.disabled)
        res.addEventListener("click", (ev: Event) => {
          ev.preventDefault();
          ev.stopPropagation()
        })
        res
      case Raw(element) => element
      case Divider =>
        div(BootstrapStyles.Dropdown.divider, role := "separator").render
    }
  }

  /**
   * Creates a dropdown component.
   * More: <a href="http://getbootstrap.com/docs/4.1/components/dropdowns/">Bootstrap Docs</a>.
   *
   * @param items          Data items which will be represented as the elements in this dropdown.
   * @param dropDirection  A direction of the menu expansion.
   * @param rightAlignMenu If true, the menu will be aligned to the right side of button.
   * @param buttonToggle   If true, the toggle arrow will be displayed.
   * @param itemFactory    Creates DOM element for each item which is inserted into the dropdown menu.
   *                       Use the provided interceptor to properly clean up bindings inside the content.
   *                       Usually you should add the `BootstrapStyles.Dropdown.item` style to your element.
   * @param buttonContent  Content of the element opening the dropdown.
   *                       Use the provided interceptor to properly clean up bindings inside the content.
   * @param buttonFactory  Allows to customize button options.
   * @tparam ItemType A single element's type in the `items` sequence.
   * @tparam ElemType A type of a property containing an element in the `items` sequence.
   * @return A `UdashDropdown` component, call `render` to create a DOM element.
   */
  def apply[ItemType, ElemType <: ReadableProperty[ItemType]](
    items: seq.ReadableSeqProperty[ItemType, ElemType],
    dropDirection: ReadableProperty[Direction] = Direction.Down.toProperty,
    rightAlignMenu: ReadableProperty[Boolean] = UdashBootstrap.False,
    buttonToggle: ReadableProperty[Boolean] = UdashBootstrap.True,
    componentId: ComponentId = ComponentId.newId()
  )(
    itemFactory: ItemType => Element,
    buttonContent: Binding.NestedInterceptor => Modifier,
    buttonFactory: (NestedInterceptor => Modifier) => UdashButton = UdashButton()
  ): UdashDropdown[ItemType, ElemType] = {
    new UdashDropdown(items, dropDirection, rightAlignMenu, buttonToggle, componentId)(itemFactory, buttonContent, buttonFactory)
  }

  /**
   * Creates a dropdown component with [[DefaultDropdownItem]] as items.
   * More: <a href="http://getbootstrap.com/docs/4.1/components/dropdowns/">Bootstrap Docs</a>.
   *
   * @param items          Data items which will be represented as the elements in this dropdown.
   * @param dropDirection  A direction of the menu expansion.
   * @param rightAlignMenu If true, the menu will be aligned to the right side of button.
   * @param buttonToggle   If true, the toggle arrow will be displayed.
   * @param buttonContent  Content of the element opening the dropdown.
   *                       Use the provided interceptor to properly clean up bindings inside the content.
   * @return A `UdashDropdown` component, call `render` to create a DOM element.
   */
  def default[ElemType <: ReadableProperty[DefaultDropdownItem]](
    items: seq.ReadableSeqProperty[DefaultDropdownItem, ElemType],
    dropDirection: ReadableProperty[Direction] = Direction.Down.toProperty,
    rightAlignMenu: ReadableProperty[Boolean] = UdashBootstrap.False,
    buttonToggle: ReadableProperty[Boolean] = UdashBootstrap.True,
    componentId: ComponentId = ComponentId.newId()
  )(
    buttonContent: Binding.NestedInterceptor => Modifier
  ): UdashDropdown[DefaultDropdownItem, ElemType] = {
    new UdashDropdown(items, dropDirection, rightAlignMenu, buttonToggle, componentId)(defaultItemFactory, buttonContent, UdashButton())
  }

  @js.native
  private trait UdashDropdownJQuery extends JQuery {
    def dropdown(arg: String): UdashDropdownJQuery = js.native
  }
}