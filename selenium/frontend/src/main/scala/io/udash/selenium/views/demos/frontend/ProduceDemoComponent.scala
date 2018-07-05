package io.udash.selenium.views.demos.frontend

import io.udash._
import io.udash.css.CssView
import org.scalajs.dom
import scalatags.JsDom

import scala.util.Random

class ProduceDemoComponent extends CssView {
  import JsDom.all._

  val names = Stream.continually(Stream("John", "Amy", "Bryan", "Diana")).flatten.iterator
  val name: Property[String] = Property[String](names.next())
  val integers: SeqProperty[Int] = SeqProperty[Int](1,2,3,4)

  dom.window.setInterval(() => {
    name.set(names.next())

    val s: Int = integers.get.size
    val idx = Random.nextInt(s)
    val amount = Random.nextInt(s - idx) + 1
    val count = Random.nextInt(5)
    integers.replace(idx, amount, Stream.range(idx, idx + amount * count + 1, amount).toSeq: _*)
  }, 2000)

  def getTemplate: Modifier = {
    div(id := "produce-demo")(
      p(
        "Name: ",
        produce(name)(value => b(id := "produce-demo-name")(value).render), br,
        "Integers: ",
        span(id := "produce-demo-integers")(
          produce(integers) { (seq: Seq[Int]) =>
            seq.map(p => span(s"$p, ").render)
          }
        )
      )
    )
  }
}
