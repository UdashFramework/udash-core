package io.udash.web.guide.views.frontend.demos

import io.udash.web.guide.styles.partials.GuideStyles
import io.udash._
import io.udash.wrappers.jquery._
import org.scalajs.dom
import org.scalajs.dom.Element

import scala.util.Random
import scalatags.JsDom
import scalacss.ScalatagsCss._

class ProduceDemoComponent extends Component {
  import io.udash.web.guide.Context._
  import JsDom.all._
  import scalacss.ScalatagsCss._

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

  override def getTemplate: Element = {
    div(id := "produce-demo", GuideStyles.frame)(
      p(
        "Name: ",
        produce(name)(value => b(id := "produce-demo-name")(value).render), br,
        "Integers: ",
        produce(integers)((seq: Seq[Int]) => span(id := "produce-demo-integers")(seq.map(p => span(GuideStyles.highlightRed)(s"$p, ")): _*).render), br,
        "Integers (patching): ",
        produce(integers,
          (seq: Seq[Property[Int]]) => span(id := "produce-demo-integers-patching")(seq.map(p => span(GuideStyles.highlightRed)(id := p.hashCode())(s"${p.get}, ")): _*).render,
          (patch: Patch[Property[Int]], el: Element) => {
            val insertBefore = jQ(el).children().at(patch.idx)
            patch.added.foreach(p => jQ(span(id := p.hashCode(), GuideStyles.highlightRed)(s"${p.get}, ").render).insertBefore(insertBefore))
            patch.removed.foreach(p => jQ(s"#${p.hashCode()}").remove())
          }
        )
      )
    ).render
  }
}
