package io.udash.web.guide.views.frontend.demos

import io.udash._
import io.udash.web.guide.styles.partials.GuideStyles
import org.scalajs.dom
import org.scalajs.dom.Element

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random
import scalatags.JsDom
import scalacss.ScalatagsCss._

class BindValidationDemoComponent extends Component {
  import io.udash.web.guide.Context._

  import JsDom.all._

  val integers: SeqProperty[Int] = SeqProperty[Int](1,2,3,4)
  integers.addValidator(new Validator[Seq[Int]] {
    override def apply(element: Seq[Int])(implicit ec: ExecutionContext): Future[ValidationResult] = Future {
      val zipped = element.toStream.slice(0, element.size-1).zip(element.toStream.drop(1))
      if (zipped.forall { case (x: Int, y: Int) => x <= y } ) Valid
      else Invalid(Seq("Sequence is not sorted!"))
    }
  })

  dom.window.setInterval(() => {
    val s: Int = integers.get.size
    val idx = Random.nextInt(s)
    val amount = Random.nextInt(s - idx) + 1
    val count = Random.nextInt(5)
    integers.replace(idx, amount, Stream.range(idx, idx + amount * count + 1, amount).toSeq: _*)
  }, 1000)

  override def getTemplate: Element = div(id := "validation-demo", GuideStyles.frame)(
    "Integers: ",
    produce(integers)((seq: Seq[Int]) => span(id := "validation-demo-integers")(seq.map(p => span(s"$p, ")): _*).render), br,
    "Is sorted: ",
    bindValidation(integers,
      _ => span("Validation in progress...").render,
      {
        case Valid => span(id := "validation-demo-result")("Yes").render
        case Invalid(_) => span(id := "validation-demo-result")("No").render
      },
      _ => span("Validation error...").render
    )
  ).render
}
