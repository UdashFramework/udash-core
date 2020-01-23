package io.udash.bindings.inputs

import io.udash._
import io.udash.properties.seq.SeqProperty
import org.scalajs.dom.html.{Div, Input => JSInput}
import org.scalajs.dom.{Event, Node}
import scalatags.JsDom.all._

/** Checkboxes for finite options with many elements selection. Bound to SeqProperty. */
object CheckButtons {
  def inputsOnlyDecorator[T]: Seq[(JSInput, T)] => Seq[Node] =
    RadioButtons.inputsOnlyDecorator
  def spanWithLabelDecorator[T](labelFactory: T => Modifier): Seq[(JSInput, T)] => Seq[Node] =
    RadioButtons.spanWithLabelDecorator(labelFactory)
  def divWithLabelDecorator[T](labelFactory: T => Modifier): Seq[(JSInput, T)] => Seq[Node] =
    RadioButtons.divWithLabelDecorator(labelFactory)

  /**
   * @param selectedItems  SeqProperty which is going to be bound to checkboxes
   * @param options        Seq of available options, one checkbox will be created for each option.
   * @param decorator      Function creating HTML element from checkboxes Seq.
   * @param inputModifiers Modifiers to apply on each generated checkbox.
   * @return HTML element created by decorator.
   */
  def apply[T](
    selectedItems: SeqProperty[T, _ <: ReadableProperty[T]], options: ReadableSeqProperty[T]
  )(decorator: Seq[(JSInput, T)] => Seq[Node], inputModifiers: Modifier*): InputBinding[Div] = {
    new GroupedButtonsBinding(options, decorator, inputModifiers)(
      "checkbox",
      opt => selectedItems.transform(_.contains(opt)),
      opts => selectedItems.set(selectedItems.get.filter(opts.contains)),
      (in: JSInput, opt: T) => (_: Event) => {
        if (in.checked && !selectedItems.get.contains(opt)) selectedItems.append(opt)
        else selectedItems.remove(opt)
      }
    )
  }
}
