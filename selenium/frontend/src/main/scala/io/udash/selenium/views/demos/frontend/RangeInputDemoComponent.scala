package io.udash.selenium.views.demos.frontend

import io.udash._
import io.udash.bootstrap.form.UdashForm
import io.udash.bootstrap.utils.ComponentId
import io.udash.css.CssView
import scalatags.JsDom

class RangeInputDemoComponent extends CssView {
  import JsDom.all._

  private val value = Property[Double](32)
  private val min = Property[Double](-25)
  private val max = Property[Double](65)
  private val step = Property[Double](2)

  def getTemplate: Modifier = div(id := "range-input-demo")(
    UdashForm(
      inputValidationTrigger = UdashForm.ValidationTrigger.None,
      selectValidationTrigger = UdashForm.ValidationTrigger.None
    ) { factory => Seq[Modifier](
      factory.input.formGroup(horizontal = Some(UdashForm.HorizontalLayoutSettings()))(
        input = _ => factory.input.numberInput(min.transform(_.toString, _.toDouble), inputId = ComponentId("range-min"))().render,
        labelContent = Some(_ => span("Minimal value"))
      ),
      factory.input.formGroup(horizontal = Some(UdashForm.HorizontalLayoutSettings()))(
        input = _ => factory.input.numberInput(max.transform(_.toString, _.toDouble), inputId = ComponentId("range-max"))().render,
        labelContent = Some(_ => span("Maximum value"))
      ),
      factory.input.formGroup(horizontal = Some(UdashForm.HorizontalLayoutSettings()))(
        input = _ => factory.input.numberInput(step.transform(_.toString, _.toDouble), inputId = ComponentId("range-step"))().render,
        labelContent = Some(_ => span("Step value"))
      ),
      factory.input.formGroup(horizontal = Some(UdashForm.HorizontalLayoutSettings()))(
        input = _ => factory.input.rangeInput(value, min, max, step, inputId = ComponentId("range-selector1"))().render,
        labelContent = Some(nested => span(id := "range-label1")("Range selector: ", nested(bind(value))))
      ),
      factory.input.formGroup(horizontal = Some(UdashForm.HorizontalLayoutSettings()))(
        input = _ => factory.input.rangeInput(value, min, max, step, inputId = ComponentId("range-selector2"))().render,
        labelContent = Some(nested => span(id := "range-label2")("Second selector: ", nested(bind(value))))
      ),
    )}
  )
}
