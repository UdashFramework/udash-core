package io.udash.bindings.inputs

import io.udash._
import io.udash.properties.PropertyCreator
import org.scalajs.dom.html.{Div, Input => JSInput}
import org.scalajs.dom.{Event, Node}
import scalatags.JsDom.all._

/**
  * Radio buttons group for finite options with one element selection.
  */
object RadioButtons {
  def inputsOnlyDecorator[T]: Seq[(JSInput, T)] => Seq[Node] =
    _.map { case (in, _) => in }
  def spanWithLabelDecorator[T](labelFactory: T => Modifier): Seq[(JSInput, T)] => Seq[Node] =
    _.map { case (in, v) => span(in, label(labelFactory(v))).render }
  def divWithLabelDecorator[T](labelFactory: T => Modifier): Seq[(JSInput, T)] => Seq[Node] =
    _.map { case (in, v) => div(in, label(labelFactory(v))).render }

  /**
    * @param selectedItem Property which is going to be bound to radio buttons group.
    * @param options Seq of available options, one radio button will be created for each option.
    * @param decorator Function creating HTML element from buttons Seq.
    *                  (Check: `RadioButtons.inputsOnlyDecorator`, `RadioButtons.spanWithLabelDecorator` and `RadioButtons.divWithLabelDecorator`)
    * @param inputModifiers Modifiers to apply on each generated checkbox.
    * @return HTML element created by decorator.
    */
  def apply[T : PropertyCreator](
    selectedItem: Property[T], options: ReadableSeqProperty[T]
  )(decorator: Seq[(JSInput, T)] => Seq[Node], inputModifiers: Modifier*): InputBinding[Div] = {
    new GroupedButtonsBinding(options, decorator, inputModifiers)(
      "radio",
      opt => selectedItem.transform(_ == opt),
      opts => if (opts.nonEmpty && !opts.contains(selectedItem.get)) selectedItem.set(opts.head),
      (_: JSInput, opt: T) => (_: Event) => selectedItem.set(opt)
    )
  }
}
