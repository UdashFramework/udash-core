package io.udash.bootstrap
package button

import com.avsystem.commons.misc.{AbstractCase, AbstractValueEnum, AbstractValueEnumCompanion, EnumCtx}
import io.udash._
import io.udash.bindings.modifiers.Binding
import io.udash.bootstrap.button.UdashButton.{ButtonClickEvent, UdashButtonJQuery}
import io.udash.bootstrap.utils._
import io.udash.wrappers.jquery.{JQuery, jQ}
import org.scalajs.dom
import org.scalajs.dom._
import scalatags.JsDom
import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._

import scala.scalajs.js

final class UdashButton private(
  buttonStyle: ReadableProperty[BootstrapStyles.Color],
  size: ReadableProperty[Option[BootstrapStyles.Size]],
  outline: ReadableProperty[Boolean],
  block: ReadableProperty[Boolean],
  active: ReadableProperty[Boolean],
  disabled: ReadableProperty[Boolean],
  override val componentId: ComponentId,
  tag: UdashButton.ButtonTag
)(content: Binding.NestedInterceptor => Modifier) extends UdashBootstrapComponent with Listenable[UdashButton, ButtonClickEvent] {
  import io.udash.css.CssView._

  private val classes: List[Modifier] = {
    (BootstrapStyles.Button.btn: Modifier) ::
      nestedInterceptor(
        ((data: (BootstrapStyles.Color, Boolean)) => data match {
          case (style, false) =>
            BootstrapStyles.Button.color(style)
          case (style, true) =>
            BootstrapStyles.Button.outline(style)
        }).reactiveApply(buttonStyle.combine(outline)((_, _)))
      ) ::
      nestedInterceptor((BootstrapStyles.Button.size _).reactiveOptionApply(size)) ::
      nestedInterceptor(BootstrapStyles.Button.block.styleIf(block)) ::
      nestedInterceptor(BootstrapStyles.active.styleIf(active)) ::
      nestedInterceptor(BootstrapStyles.disabled.styleIf(disabled)) ::
      nestedInterceptor(JsDom.all.disabled.attrIf(disabled)) :: Nil
  }

  override val render: dom.html.Element = {
    tag.value(id := componentId, tpe := "button")(classes: _*)(
      //condition to support non-button tags
      onclick :+= ((me: MouseEvent) => if (!disabled.get) fire(ButtonClickEvent(this, me)))
    )(content(nestedInterceptor)).render
  }

  override def kill(): Unit = {
    super.kill()
    jQSelector().button("dispose")
  }

  private def jQSelector(): UdashButtonJQuery =
    jQ(s"#$componentId").asInstanceOf[UdashButtonJQuery]
}

object UdashButton {
  final case class ButtonClickEvent(source: UdashButton, mouseEvent: MouseEvent)
    extends AbstractCase with ListenableEvent[UdashButton]

  /**
   * Holds button enclosing tag options. Since buttons have their own click listeners implemented they can be enclosed
   * in various tags, e.g.:
   *
   * - Button - encloses the button in <button></button> tags
   * - Anchor - encloses the button in <a></a> tags. For this type of tags link options can be used to indicate the href
   * to redirect user on click
   * - Div    - encloses the button in <div></div> tags
   */
  final class ButtonTag(val value: TypedTag[dom.html.Element])(implicit enumCtx: EnumCtx) extends AbstractValueEnum
  object ButtonTag extends AbstractValueEnumCompanion[ButtonTag] {
    final val Button: Value = new ButtonTag(JsDom.all.button)
    final val Anchor: Value = new ButtonTag(JsDom.all.a)
    final val Div: Value = new ButtonTag(JsDom.all.div)
  }

  /**
    * Creates a button component.
    * More: <a href="http://getbootstrap.com/docs/4.1/components/buttons/">Bootstrap Docs</a>.
    *
    * @param buttonStyle A button style, one of the standard bootstrap colors `BootstrapStyles.Color`.
    * @param size        A button size, one of the standard bootstrap sizes `BootstrapStyles.Size`.
    * @param outline     If true, selects the outline style for the button. More: <a href="http://getbootstrap.com/docs/4.1/components/buttons/#outline-buttons">Bootstrap Docs</a>.
    * @param block       If true, rendered button will be a full-width block.
    * @param active      A property indicating if the button is in the `active` state.
    * @param disabled    A property indicating if the button is disabled.
    * @param componentId An id of the root DOM node.
    * @param content     A content of the button.
    *                    Use the provided interceptor to properly clean up bindings inside the content.
    * @return A `UdashButton` component, call `render` to create a DOM element representing this button.
    */
  def apply(
    buttonStyle: ReadableProperty[BootstrapStyles.Color] = UdashBootstrap.ColorSecondary,
    size: ReadableProperty[Option[BootstrapStyles.Size]] = UdashBootstrap.None,
    outline: ReadableProperty[Boolean] = UdashBootstrap.False,
    block: ReadableProperty[Boolean] = UdashBootstrap.False,
    active: ReadableProperty[Boolean] = UdashBootstrap.False,
    disabled: ReadableProperty[Boolean] = UdashBootstrap.False,
    componentId: ComponentId = ComponentId.generate(),
    tag: ButtonTag = ButtonTag.Button
  )(content: Binding.NestedInterceptor => Modifier): UdashButton =
    new UdashButton(buttonStyle, size, outline, block, active, disabled, componentId, tag)(content)

  /**
    * Creates a toggle button component.
    * More: <a href="http://getbootstrap.com/docs/4.1/components/buttons/">Bootstrap Docs</a>.
    *
    * @param active      A property indicating if the button is in the `active` state.
    * @param buttonStyle A button style, one of the standard bootstrap colors `BootstrapStyles.Color`.
    * @param size        A button size, one of the standard bootstrap sizes `BootstrapStyles.Size`.
    * @param outline     If true, selects the outline style for the button. More: <a href="http://getbootstrap.com/docs/4.1/components/buttons/#outline-buttons">Bootstrap Docs</a>.
    * @param block       If true, rendered button will be a full-width block.
    * @param disabled    A property indicating if the button is disabled.
    * @param componentId An id of the root DOM node.
    * @param content     A content of the button. Use the provided interceptor to properly clean up bindings inside the content.
    * @return A `UdashButton` component, call `render` to create a DOM element representing this button.
    */
  def toggle(
    active: Property[Boolean],
    buttonStyle: ReadableProperty[BootstrapStyles.Color] = UdashBootstrap.ColorSecondary,
    size: ReadableProperty[Option[BootstrapStyles.Size]] = UdashBootstrap.None,
    outline: ReadableProperty[Boolean] = UdashBootstrap.False,
    block: ReadableProperty[Boolean] = UdashBootstrap.False,
    disabled: ReadableProperty[Boolean] = UdashBootstrap.False,
    componentId: ComponentId = ComponentId.generate(),
    tag: ButtonTag = ButtonTag.Button
  )(content: Binding.NestedInterceptor => Modifier): UdashButton = {
    val button = new UdashButton(buttonStyle, size, outline, block, active, disabled, componentId, tag)(content)
    button.listen { case _ => active.set(!active.get) }
    button
  }

  @js.native
  private trait UdashButtonJQuery extends JQuery {
    def button(cmd: String): UdashButtonJQuery = js.native
  }
}
