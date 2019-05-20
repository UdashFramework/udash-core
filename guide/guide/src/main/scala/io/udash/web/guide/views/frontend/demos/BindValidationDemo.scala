package io.udash.web.guide.views.frontend.demos

import io.udash._
import io.udash.css.CssView
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.views.ext.demo.AutoDemo
import org.scalajs.dom
import scalatags.JsDom

import scala.util.Random

object BindValidationDemo extends AutoDemo with CssView {
  import JsDom.all._

  private val (rendered, source) = {
    val integers: SeqProperty[Int] = SeqProperty[Int](1, 2, 3, 4)
    integers.addValidator((element: Seq[Int]) => {
      val zipped = element.toStream
        .slice(0, element.size - 1)
        .zip(element.toStream.drop(1))
      if (zipped.forall { case (x: Int, y: Int) => x <= y }) Valid
      else Invalid("Sequence is not sorted!")
    })

    val element = Seq[Modifier](
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

    dom.window.setInterval(() => {
      val s: Int = integers.get.size
      val idx = Random.nextInt(s)
      val amount = Random.nextInt(s - idx) + 1
      val count = Random.nextInt(5)
      integers.replace(idx, amount, Stream.range(idx, idx + amount * count + 1, amount): _*)
    }, 2000)

    element
  }.withSourceCode

  override protected def demoWithSource(): (JsDom.all.Modifier, Iterator[String]) = {
    (div(id := "validation-demo", GuideStyles.frame)(rendered), source.lines.slice(1, source.lines.size - 10))
  }
}
