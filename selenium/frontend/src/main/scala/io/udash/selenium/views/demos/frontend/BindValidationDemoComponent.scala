package io.udash.selenium.views.demos.frontend

import io.udash._
import io.udash.css.CssView
import org.scalajs.dom
import scalatags.JsDom

import scala.util.Random

class BindValidationDemoComponent extends CssView {
  import JsDom.all._

  val integers: SeqProperty[Int] = SeqProperty[Int](1,2,3,4)
  integers.addValidator((element: Seq[Int]) => {
    val zipped = element.toStream
      .slice(0, element.size-1)
      .zip(element.toStream.drop(1))
    if (zipped.forall { case (x: Int, y: Int) => x <= y } ) Valid
    else Invalid("Sequence is not sorted!")
  })

  dom.window.setInterval(() => {
    val s: Int = integers.get.size
    val idx = Random.nextInt(s)
    val amount = Random.nextInt(s - idx) + 1
    val count = Random.nextInt(5)
    integers.replace(idx, amount, Stream.range(idx, idx + amount * count + 1, amount).toSeq: _*)
  }, 2000)

  def getTemplate: Modifier = div(id := "validation-demo")(
    "Integers: ",
    span(
      id := "validation-demo-integers",
      (attr("data-valid") := true).attrIf(integers.valid.transform(_ == Valid))
    )(
      repeat(integers)(p => span(s"${p.get}, ").render)
    ), br,
    "Is sorted: ",
    valid(integers)(
      {
        case Valid => span(id := "validation-demo-result")("Yes").render
        case Invalid(_) => span(id := "validation-demo-result")("No").render
      },
      progressBuilder = _ => span("Validation in progress...").render,
      errorBuilder = _ => span("Validation error...").render
    )
  )
}
