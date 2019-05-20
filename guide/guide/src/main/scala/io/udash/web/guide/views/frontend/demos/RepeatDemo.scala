package io.udash.web.guide.views.frontend.demos

import io.udash._
import io.udash.css.CssView
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.views.ext.demo.AutoDemo
import org.scalajs.dom
import scalatags.JsDom

import scala.util.Random

object RepeatDemo extends AutoDemo with CssView {

  import JsDom.all._

  private val (rendered, source) = {
    val integers: SeqProperty[Int] = SeqProperty[Int](1, 2, 3, 4)

    val element = p(
      "Integers: ",
      span(id := "repeat-demo-integers")(repeat(integers)(p => span(GuideStyles.highlightRed)(s"${p.get}, ").render)), br,
      "Integers (produce): ",
      produce(integers)((seq: Seq[Int]) => span(id := "repeat-demo-integers-produce")(
        seq.map(p => span(GuideStyles.highlightRed)(s"$p, "))
      ).render)
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
    (div(id := "repeat-demo", GuideStyles.frame)(rendered), source.lines.slice(1, source.lines.size - 10))
  }
}
