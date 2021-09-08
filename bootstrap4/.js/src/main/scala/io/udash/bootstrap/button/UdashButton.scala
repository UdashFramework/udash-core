package io.udash.bootstrap
package button

import com.avsystem.commons.Opt
import com.avsystem.commons.misc.{AbstractCase, AbstractValueEnum, AbstractValueEnumCompanion, EnumCtx}
import io.udash._
import io.udash.bindings.modifiers.Binding
import io.udash.bootstrap.button.UdashButton.{ButtonClickEvent, ButtonTag, UdashButtonJQuery}
import io.udash.bootstrap.utils._
import io.udash.wrappers.jquery.{JQuery, jQ}
import org.scalajs.dom
import org.scalajs.dom._
import scalatags.JsDom
import scalatags.JsDom.all._

import scala.scalajs.js

/**
 * Options for `UdashButton` component
 *
 * @param color           A button style, one of the standard bootstrap colors `BootstrapStyles.Color`.
 * @param size            A button size, one of the standard bootstrap sizes `BootstrapStyles.Size`.
 * @param outline         If true, selects the outline style for the button. More: <a href="http://getbootstrap.com/docs/4.1/components/buttons/#outline-buttons">Bootstrap Docs</a>.
 * @param block           If true, rendered button will be a full-width block.
 * @param tag             HTML tag used in button, one of tags defined in `ButtonTag`
 * @param href            Href attribute value, used only in case of `ButtonTag.Anchor`
 * @param customModifiers Sequence of custom modifiers.
 */
final case class UdashButtonOptions(
  color: Opt[BootstrapStyles.Color] = BootstrapStyles.Color.Secondary.opt,
  size: Opt[BootstrapStyles.Size] = Opt.empty,
  outline: Boolean = false,
  block: Boolean = false,
  tag: ButtonTag = ButtonTag.Button,
  href: Opt[String] = Opt.Empty,
  customModifiers: Seq[Modifier] = Seq.empty
) extends AbstractCase

final class UdashButton private(
  override val componentId: ComponentId,
  active: ReadableProperty[Boolean],
  disabled: ReadableProperty[Boolean],
  options: UdashButtonOptions
)(content: Binding.NestedInterceptor => Modifier) extends UdashBootstrapComponent with Listenable {

  import io.udash.css.CssView._

  override type EventType = ButtonClickEvent

  private val classes: Seq[Modifier] =
    Seq(
      BootstrapStyles.Button.btn: Modifier,
      options.color.map(if (options.outline) BootstrapStyles.Button.outline _ else BootstrapStyles.Button.color _): Modifier,
      BootstrapStyles.Button.block.styleIf(options.block),
      nestedInterceptor(BootstrapStyles.active.styleIf(active)),
      nestedInterceptor(BootstrapStyles.disabled.styleIf(disabled)),
      nestedInterceptor(JsDom.all.disabled.attrIf(disabled)),
      seqNodeFromOpt(options.size.map(size => BootstrapStyles.Button.size(size): Modifier))
    ) ++ options.customModifiers


  override val render: dom.html.Element = {
    (options.tag match {
      case ButtonTag.Button =>
        button(componentId, tpe := "button")(classes: _*)(
          onclick :+= ((me: MouseEvent) => if (!disabled.get) fire(ButtonClickEvent(this, me)))
        )
      case ButtonTag.Anchor =>
        a(componentId)(classes: _*)(
          options.href.map(href := _)
        )
      case ButtonTag.Div =>
        div(componentId)(classes: _*)(
          onclick :+= ((me: MouseEvent) => if (!disabled.get) fire(ButtonClickEvent(this, me)))
        )
    }) (content(nestedInterceptor)).render
  }

  override def kill(): Unit = {
    super.kill()
    jQSelector().button("dispose")
  }

  private def jQSelector(): UdashButtonJQuery =
    jQ(s"#$componentId").asInstanceOf[UdashButtonJQuery]
}

object UdashButton {
  final case class ButtonClickEvent(source: UdashButton, mouseEvent: MouseEvent) extends AbstractCase with ListenableEvent

  /**
   * Holds button enclosing tag options. Since buttons have their own click listeners implemented they can be enclosed
   * in various tags, e.g.:
   *
   * - Button - encloses the button in <button></button> tags
   * - Anchor - encloses the button in <a></a> tags. For this type of tags link options can be used to indicate the href
   * to redirect user on click
   * - Div    - encloses the button in <div></div> tags
   */
  final class ButtonTag()(implicit enumCtx: EnumCtx) extends AbstractValueEnum
  object ButtonTag extends AbstractValueEnumCompanion[ButtonTag] {
    final val Button: Value = new ButtonTag()
    final val Anchor: Value = new ButtonTag()
    final val Div: Value = new ButtonTag()
  }

  /**
   * Creates a button component.
   * More: <a href="http://getbootstrap.com/docs/4.1/components/buttons/">Bootstrap Docs</a>.
   *
   * @param componentId An id of the root DOM node.
   * @param active      A property indicating if the button is in the `active` state.
   * @param disabled    A property indicating if the button is disabled.
   * @param options     `UdashButton` options object
   * @param content     A content of the button.
   *                    Use the provided interceptor to properly clean up bindings inside the content.
   * @return A `UdashButton` component, call `render` to create a DOM element representing this button.
   */
  def apply(
    componentId: ComponentId = ComponentId.generate(),
    active: ReadableProperty[Boolean] = UdashBootstrap.False,
    disabled: ReadableProperty[Boolean] = UdashBootstrap.False,
    options: UdashButtonOptions = UdashButtonOptions()
  )(content: Binding.NestedInterceptor => Modifier): UdashButton =
    new UdashButton(componentId, active, disabled, options)(content)

  /**
   * Creates a toggle button component.
   * More: <a href="http://getbootstrap.com/docs/4.1/components/buttons/">Bootstrap Docs</a>.
   *
   * @param componentId An id of the root DOM node.
   * @param active      A property indicating if the button is in the `active` state.
   * @param disabled    A property indicating if the button is disabled.
   * @param options     `UdashButton` options object
   * @param content     A content of the button. Use the provided interceptor to properly clean up bindings inside the content.
   * @return A `UdashButton` component, call `render` to create a DOM element representing this button.
   */
  def toggle(
    componentId: ComponentId = ComponentId.generate(),
    active: Property[Boolean],
    disabled: ReadableProperty[Boolean] = UdashBootstrap.False,
    options: UdashButtonOptions = UdashButtonOptions()
  )(content: Binding.NestedInterceptor => Modifier): UdashButton = {
    val button = new UdashButton(componentId, active, disabled, options)(content)
    button.listen { case _ => active.set(!active.get) }
    button
  }

  @js.native
  private trait UdashButtonJQuery extends JQuery {
    def button(cmd: String): UdashButtonJQuery = js.native
  }
}
