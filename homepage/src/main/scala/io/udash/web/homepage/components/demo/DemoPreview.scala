package io.udash.web.homepage.components.demo

import io.udash.web.commons.styles.GlobalStyles
import io.udash.web.commons.styles.components.FooterStyles
import io.udash.web.homepage.styles.partials.DemoStyles
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
      TextInput.debounced(name, maxlength := 16, DemoStyles.demoInlineField, GlobalStyles.width100),
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
      TextInput.debounced(input, `type` := "text", placeholder := "Type a number and press enter...", maxlength := 6, pattern := "\\d*", DemoStyles.demoInput, GlobalStyles.width100)(
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
      TextInput.debounced(email, maxlength := 32, DemoStyles.demoInlineField, GlobalStyles.width100),
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

  def i18n: Element = {
    import io.udash._
    import io.udash.i18n._

    import scalajs.concurrent.JSExecutionContext.Implicits
    import Implicits.queue
    import scalacss.ScalatagsCss._
    import scalatags.JsDom.all._

    val name = Property("World")

    object Translations {
      import TranslationKey._
      object udash {
        val hello = key("udash.hello")
        val withArg = key1[String]("udash.withArg")
      }
    }

    object FrontendTranslationProvider {
      private val translations = Map(
        Lang("en") -> Bundle(BundleHash("enHash"), Map(
          "udash.hello" -> "Hello, Udash!",
          "udash.withArg" -> "Hello, {}!"
        )),
        Lang("pl") -> Bundle(BundleHash("plHash"), Map(
          "udash.hello" -> "Witaj, Udash!",
          "udash.withArg" -> "Witaj, {}!"
        )),
        Lang("de") -> Bundle(BundleHash("deHash"), Map(
          "udash.hello" -> "Hallo, Udash!",
          "udash.withArg" -> "Hallo, {}!"
        )),
        Lang("sp") -> Bundle(BundleHash("spHash"), Map(
          "udash.hello" -> "Hola, Udash!",
          "udash.withArg" -> "Hola, {}!"
        ))
      )
      def apply(): LocalTranslationProvider =
        new LocalTranslationProvider(translations)
    }

    implicit val translationProvider: TranslationProvider = FrontendTranslationProvider()
    implicit val lang: Property[Lang] = Property(Lang("en"))

    def changeLang(l: Lang): Unit =
      lang.set(l)

    div(DemoStyles.demoIOWrapper)(
      TextInput.debounced(name, `type` := "text", placeholder := "Type your name...", DemoStyles.demoInput, GlobalStyles.width100),
      div(DemoStyles.demoOutput)(
        span(translatedDynamic(Translations.udash.hello)(_.apply()))
      ),
      div(DemoStyles.demoOutput)(
        produce(name)(n => span(translatedDynamic(Translations.udash.withArg)(_.apply(n))).render)
      ),
      div(DemoStyles.demoOutput)(
        ul(
          li(DemoStyles.navItem)(a(DemoStyles.underlineLink, onclick := (() => changeLang(Lang("en"))))("EN")),
          li(DemoStyles.navItem)(a(DemoStyles.underlineLink, onclick := (() => changeLang(Lang("pl"))))("PL")),
          li(DemoStyles.navItem)(a(DemoStyles.underlineLink, onclick := (() => changeLang(Lang("de"))))("DE")),
          li(DemoStyles.navItem)(a(DemoStyles.underlineLink, onclick := (() => changeLang(Lang("sp"))))("SP"))
        )
      )
    ).render
  }
}
