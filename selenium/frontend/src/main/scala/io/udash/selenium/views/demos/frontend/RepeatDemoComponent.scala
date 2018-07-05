package io.udash.selenium.views.demos.frontend

import io.udash._
import io.udash.css.CssView
import org.scalajs.dom
import scalatags.JsDom

import scala.util.Random

class RepeatDemoComponent extends CssView {
  import JsDom.all._

  val integers: SeqProperty[Int] = SeqProperty[Int](1,2,3,4)

  dom.window.setInterval(() => {
    val s: Int = integers.get.size
    val idx = Random.nextInt(s)
    val amount = Random.nextInt(s - idx) + 1
    val count = Random.nextInt(5)
    integers.replace(idx, amount, Stream.range(idx, idx + amount * count + 1, amount): _*)
  }, 2000)

  def getTemplate: Modifier = div(id := "repeat-demo")(
    p(
      "Integers: ",
      span(id := "repeat-demo-integers")(repeat(integers)(p => span(s"${p.get}, ").render)), br,
      "Integers (produce): ",
      produce(integers)((seq: Seq[Int]) => span(id := "repeat-demo-integers-produce")(
        seq.map(p => span(s"$p, "))
      ).render)
    )
  )
}
