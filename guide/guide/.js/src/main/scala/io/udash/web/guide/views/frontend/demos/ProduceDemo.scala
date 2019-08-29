package io.udash.web.guide.views.frontend.demos

import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

object ProduceDemo extends AutoDemo {

  private val (rendered, source) = {
    import io.udash._
    import io.udash.css.CssView._
    import org.scalajs.dom.window
    import scalatags.JsDom.all._

    import scala.util.Random

    val names = Stream.continually(Stream("John", "Amy", "Bryan", "Diana")).flatten.iterator
    val name = Property(names.next())
    val integers = SeqProperty(1, 2, 3, 4)

    window.setInterval(() => {
      name.set(names.next())

      val size = integers.get.size
      val idx = Random.nextInt(size)
      val amount = Random.nextInt(size - idx) + 1
      val count = Random.nextInt(5)
      integers.replace(idx, amount, Stream.range(idx, idx + amount * count + 1, amount): _*)
    }, 2000)

    p(
      "Name: ",
      produce(name)(value => b(id := "produce-demo-name")(value).render), br,
      "Integers: ",
      span(id := "produce-demo-integers")(
        produce(integers) { seq: Seq[Int] =>
          span(GuideStyles.highlightRed)(seq.mkString(",")).render
        }
      )
    ).render
  }.withSourceCode

  override protected def demoWithSource(): (Modifier, Iterator[String]) = {
    import io.udash.css.CssView._
    (
      div(
        id := "produce-demo",
        GuideStyles.frame
      )(rendered),
      source.linesIterator
    )
  }
}
