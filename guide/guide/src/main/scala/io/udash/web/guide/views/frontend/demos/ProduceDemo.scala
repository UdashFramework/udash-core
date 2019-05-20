package io.udash.web.guide.views.frontend.demos

import io.udash._
import io.udash.css.CssView
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.views.ext.demo.AutoDemo
import org.scalajs.dom
import scalatags.JsDom

import scala.util.Random

object ProduceDemo extends AutoDemo with CssView {
  import JsDom.all._

  private val (rendered, source) = {
    val names = Stream.continually(Stream("John", "Amy", "Bryan", "Diana")).flatten.iterator
    val name: Property[String] = Property[String](names.next())
    val integers: SeqProperty[Int] = SeqProperty[Int](1,2,3,4)

    dom.window.setInterval(() => {
      name.set(names.next())

      val s: Int = integers.get.size
      val idx = Random.nextInt(s)
      val amount = Random.nextInt(s - idx) + 1
      val count = Random.nextInt(5)
      integers.replace(idx, amount, Stream.range(idx, idx + amount * count + 1, amount): _*)
    }, 2000)

    p(
      "Name: ",
      produce(name)(value => b(id := "produce-demo-name")(value).render), br,
      "Integers: ",
      span(id := "produce-demo-integers")(
        produce(integers) { seq: Seq[Int] =>
          seq.map(p => span(GuideStyles.highlightRed)(s"$p, ").render)
        }
      )
    )
  }.withSourceCode

  override protected def demoWithSource(): (JsDom.all.Modifier, Iterator[String]) = {
    (div(id := "produce-demo", GuideStyles.frame)(rendered), source.lines.slice(1, 5) ++
      source.lines.slice(source.lines.size - 11, source.lines.size - 1))
  }
}
