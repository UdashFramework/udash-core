package io.udash.homepage.components.demo

import io.udash.homepage.styles.GlobalStyles
import io.udash.homepage.styles.partials.DemoStyles
import org.scalajs.dom.raw.Element

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

object DemoPreview {
  def helloWorldDemo: Element = {
    import io.udash._

    import scalacss.ScalatagsCss._
    import scalajs.concurrent.JSExecutionContext.Implicits.queue
    import scalatags.JsDom.all._

    val name = Property("World")

    div(DemoStyles.demoIOWrapper, GlobalStyles.table)(
      TextInput(name, maxlength := 16, DemoStyles.demoInlineField, GlobalStyles.width100),
      produce(name)(name => h3(DemoStyles.demoInlineField, DemoStyles.demoOutput, GlobalStyles.width50)(s"Hello, $name!").render)
    ).render
  }

  def propertiesDemo: Element = {
    import io.udash._
    import org.scalajs.dom._

    import scalacss.ScalatagsCss._
    import scalajs.concurrent.JSExecutionContext.Implicits.queue
    import scalatags.JsDom.all._

    def isEven(n: Int): Boolean =
      n % 2 == 0

    def renderer(n: ReadableProperty[Int]): Element =
      span(s"${n.get}, ").render

    val input = Property("")
    val numbers = SeqProperty[Int](Seq.empty)
    val odds = numbers.filter(n => !isEven(n))
    val evens = numbers.filter(isEven)

    div(DemoStyles.demoIOWrapper)(
      TextInput(input, `type` := "text", placeholder := "Type a number and press enter...", maxlength := 6, pattern := "\\d*", DemoStyles.demoInput, GlobalStyles.width100)(
        onkeyup := ((ev: KeyboardEvent) => if (ev.keyCode == ext.KeyCode.Enter) {
          val n: Try[Int] = Try(input.get.toInt)
          if (n.isSuccess) {
            numbers.append(n.get)
            input.set("")
          }
        })
      ), br,
      div(DemoStyles.demoOutput)(
        span(DemoStyles.demoOutpuLabel)("Numbers: "),
        span(GlobalStyles.col, GlobalStyles.width66, GlobalStyles.textLeft)(repeat(numbers)(renderer))
      ),
      div(DemoStyles.demoOutput)(
        span(DemoStyles.demoOutpuLabel)("Evens: "),
        span(GlobalStyles.col, GlobalStyles.width66, GlobalStyles.textLeft)(repeat(evens)(renderer))
      ),
      div(DemoStyles.demoOutput)(
        span(DemoStyles.demoOutpuLabel)("Odds: "),
        span(GlobalStyles.col, GlobalStyles.width66, GlobalStyles.textLeft)(repeat(odds)(renderer))
      )
    ).render
  }

  def validationDemo: Element = {
    import io.udash._

    import scalajs.concurrent.JSExecutionContext.Implicits
    import Implicits.queue
    import scalacss.ScalatagsCss._
    import scalatags.JsDom.all._

    val emailRegex = "([\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,})".r

    val email = Property("example@mail.com")
    email.addValidator(new Validator[String] {
      def apply(element: String)(implicit ec: ExecutionContext): Future[ValidationResult] = Future {
        element match {
          case emailRegex(text) => Valid
          case _ => Invalid(Seq("It's not an email!"))
        }
      }
    })

    div(DemoStyles.demoIOWrapper, GlobalStyles.table)(
      TextInput(email, maxlength := 32, DemoStyles.demoInlineField, GlobalStyles.width100),
      span(DemoStyles.demoInlineField, DemoStyles.demoOutput, GlobalStyles.width50)(
        "Valid: ", bindValidation(email,
          _ => span("Wait...").render,
          {
            case Valid => span("Yes").render
            case Invalid(_) => span("No").render
          },
          _ => span("ERROR").render
        )
      )
    ).render
  }
}
