package io.udash.web.guide.views.frontend.demos

import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

object RepeatDemo extends AutoDemo {

  private val (rendered, source) = {
    import io.udash._
    import io.udash.css.CssView._
    import org.scalajs.dom.window
    import scalatags.JsDom.all._

    import scala.util.Random

    val integers = SeqProperty(1, 2, 3, 4)

    window.setInterval(() => {
      val size = integers.get.size
      val idx = Random.nextInt(size)
      val amount = Random.nextInt(size - idx) + 1
      val count = Random.nextInt(5)
      integers.replace(idx, amount, Seq.range(idx, idx + amount * count + 1, amount): _*)
    }, 2000)

    p(
      "Integers: ",
      span(id := "repeat-demo-integers")(repeat(integers)(p =>
        span(GuideStyles.highlightRed)(s"${p.get}, ").render
      )), br,
      "Integers (produce): ",
      produce(integers)(seq => span(id := "repeat-demo-integers-produce")(
        seq.map(p => span(GuideStyles.highlightRed)(s"$p, "))
      ).render)
    )
  }.withSourceCode

  override protected def demoWithSource(): (Modifier, String) = {
    import io.udash.css.CssView._
    (div(id := "repeat-demo", GuideStyles.frame)(rendered), source)
  }
}
