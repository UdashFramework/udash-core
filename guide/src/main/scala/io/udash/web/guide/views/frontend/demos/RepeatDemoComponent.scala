package io.udash.web.guide.views.frontend.demos

import io.udash._
import io.udash.web.guide.styles.partials.GuideStyles
import org.scalajs.dom
import org.scalajs.dom.Element

import scala.util.Random
import scalatags.JsDom

class RepeatDemoComponent extends Component {
  import io.udash.web.guide.Context._

  import JsDom.all._
  import scalacss.ScalatagsCss._

  val integers: SeqProperty[Int] = SeqProperty[Int](Seq(1,2,3,4))

  dom.window.setInterval(() => {
    val s: Int = integers.get.size
    val idx = Random.nextInt(s)
    val amount = Random.nextInt(s - idx) + 1
    val count = Random.nextInt(5)
    integers.replace(idx, amount, Stream.range(idx, idx + amount * count + 1, amount): _*)
  }, 2000)

  override def getTemplate: Element = div(id := "repeat-demo", GuideStyles.frame)(
    p(
      "Integers: ",
      span(id := "repeat-demo-integers")(repeat(integers)(p => span(GuideStyles.highlightRed)(s"${p.get}, ").render)), br,
      "Integers (produce): ",
      produce(integers)((seq: Seq[Int]) => span(id := "repeat-demo-integers-produce")(seq.map(p => span(GuideStyles.highlightRed)(s"$p, ")): _*).render)
    )
  ).render
}
