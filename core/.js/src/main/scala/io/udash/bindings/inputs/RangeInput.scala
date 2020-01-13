package io.udash.bindings.inputs

import io.udash._
import org.scalajs.dom.Event
import org.scalajs.dom.html.{Input => JSInput}
import scalatags.JsDom.all._

/** The HTML range input.*/
object RangeInput {
  /**
    * @param property Current value synchronised with the input.
    *                 The value should be between `minValue` and `maxValue`. It should be also divisible by `valueStep`.
    * @param minValue The minimum value for this input, which must not be greater than its maximum (`maxValue` attribute) value.
    * @param maxValue The maximum value for the input. Must not be less than its minimum (`minValue` attribute) value.
    * @param valueStep Limit the increments at which a numeric value can be set.
    * @param inputModifiers Additional Modifiers, don't use modifiers on value, onchange, min, max and step attributes.
    * @return HTML range input with bound Property, applied modifiers and nested options.
    */
  def apply(
    property: Property[Double],
    minValue: ReadableProperty[Double] = 0d.toProperty,
    maxValue: ReadableProperty[Double] = 100d.toProperty,
    valueStep: ReadableProperty[Double] = 1d.toProperty
  )(inputModifiers: Modifier*): InputBinding[JSInput] =
    new InputBinding[JSInput] {
      private val element = input(inputModifiers, tpe := "range").render

      element.onchange = (_: Event) => property.set(element.valueAsNumber)
      propertyListeners += property.listen(element.valueAsNumber = _, initUpdate = true)
      propertyListeners += minValue.listen { v =>
        (min := v).applyTo(element)
        property.set(element.valueAsNumber)
      }
      propertyListeners += maxValue.listen { v =>
        (max := v).applyTo(element)
        property.set(element.valueAsNumber)
      }
      propertyListeners += valueStep.listen { v =>
        (step := v).applyTo(element)
        property.set(element.valueAsNumber)
      }

      override def render: JSInput = element
    }
}
