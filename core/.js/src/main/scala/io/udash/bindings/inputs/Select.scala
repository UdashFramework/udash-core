package io.udash.bindings.inputs

import io.udash._
import io.udash.properties.seq.SeqProperty
import org.scalajs.dom.Event
import org.scalajs.dom.html.Select
import org.scalajs.dom.raw.HTMLOptionElement
import scalatags.JsDom.all._

/**
  * Select of finite options for single and multi selection.
  */
object Select {
  val defaultLabel: String => Modifier = s => StringFrag(s)

  /**
    * Single select for ValueProperty.
    *
    * @param selectedItem Property to bind.
    * @param options SeqProperty of available options.
    * @param label Provides element's label.
    * @param selectModifiers Additional Modifiers for the select tag, don't use modifiers on value, onchange and selected attributes.
    * @return Binding with `select` element, which can be used as Scalatags modifier.
    */
  def apply[T](
    selectedItem: Property[T], options: ReadableSeqProperty[T]
  )(label: T => Modifier, selectModifiers: Modifier*): InputBinding[Select] = {
    new SelectBinding(options, label, None, selectModifiers)(
      opt => selectedItem.transform(_ == opt),
      opts => if (opts.nonEmpty && !opts.contains(selectedItem.get)) selectedItem.set(opts.head),
      selector => (_: Event) => selectedItem.set(options.get.apply(selector.value.toInt))
    )
  }


  /**
   * Single select for optional ValueProperty. It differs from the `apply` method since the rendered output contains
   * the empty string for the empty value.
   * It might be useful when you need to use the `required` attribute
   * https://developer.mozilla.org/en-US/docs/Web/HTML/Element/select#attr-required
   *
   * Example rendered output:
   * ```
   * <select>
   *   <option value="">Label no value</option>
   *   <option value="0">Apple</option>
   *   ...
   * </select>
   * ```
   *
   * @param selectedItem Property to bind.
   * @param options SeqProperty of available options.
   * @param labelNoValue Label for option without value
   * @param label Provides element's label.
   * @param selectModifiers Additional Modifiers for the select tag, don't use modifiers on value, onchange and selected attributes.
   * @return Binding with `select` element, which can be used as Scalatags modifier.
   */
  def optional[T](
    selectedItem: Property[Option[T]], options: ReadableSeqProperty[T], labelNoValue: Modifier
  )(label: T => Modifier, selectModifiers: Modifier*): InputBinding[Select] = {
    new SelectBinding(options, label, Some(labelNoValue), selectModifiers)(
      opt => selectedItem.transform {
        case None => false
        case Some(si) => si == opt
      },
      opts => if (!opts.exists(x => selectedItem.get.contains(x))) selectedItem.set(None),
      selector => (_: Event) => selector.value match {
        case ""  => selectedItem.set(None)
        case s:String =>  selectedItem.set(Some(options.get.apply(s.toInt)))
      }
    )
  }

  /**
    * Multi selection for SeqProperty. Bound SeqProperty will contain selected options.
    *
    * @param selectedItems Property to bind.
    * @param options SeqProperty of available options.
    * @param label Provides element label.
    * @param selectModifiers Additional Modifiers, don't use modifiers on value, onchange and selected attributes.
    * @return Binding with `select` element, which can be used as Scalatags modifier.
    */
  def apply[T, ElemType <: Property[T]](
    selectedItems: SeqProperty[T, ElemType], options: ReadableSeqProperty[T]
  )(label: T => Modifier, selectModifiers: Modifier*): InputBinding[Select] = {
    new SelectBinding(options, label, None, selectModifiers :+ (multiple := true))(
      opt => selectedItems.transform(_.contains(opt)),
      opts => selectedItems.set(selectedItems.get.filter(opts.contains)),
      selector => (_: Event) => {
        val opts = options.get
        val selectedNodes = selector.querySelectorAll("option:checked")
        val selection = (0 until selectedNodes.length).map { idx =>
          opts(selectedNodes(idx).asInstanceOf[HTMLOptionElement].value.toInt)
        }
        selectedItems.set(selection)
      }
    )
  }
}
