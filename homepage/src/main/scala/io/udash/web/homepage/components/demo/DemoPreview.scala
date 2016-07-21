package io.udash.web.homepage.components.demo

import io.udash.bootstrap.UdashBootstrap
import io.udash.bootstrap.modal.ModalSize
import io.udash.web.commons.styles.GlobalStyles
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

    div(DemoStyles.get.demoIOWrapper, GlobalStyles.get.table)(
      TextInput.debounced(name, maxlength := 16, DemoStyles.get.demoInlineField, GlobalStyles.get.width100),
      produce(name)(name => h3(DemoStyles.get.demoInlineField, DemoStyles.get.demoOutput, GlobalStyles.get.width50)(s"Hello, $name!").render)
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

    div(DemoStyles.get.demoIOWrapper)(
      TextInput.debounced(input, `type` := "text", placeholder := "Type a number and press enter...", maxlength := 6, pattern := "\\d*", DemoStyles.get.demoInput, GlobalStyles.get.width100)(
        onkeyup := ((ev: KeyboardEvent) => if (ev.keyCode == ext.KeyCode.Enter) {
          val n: Try[Int] = Try(input.get.toInt)
          if (n.isSuccess) {
            numbers.append(n.get)
            input.set("")
          }
        })
      ), br,
      div(DemoStyles.get.demoOutput)(
        span(DemoStyles.get.demoOutpuLabel)("Numbers: "),
        span(GlobalStyles.get.col, GlobalStyles.get.width66, GlobalStyles.get.textLeft)(repeat(numbers)(renderer))
      ),
      div(DemoStyles.get.demoOutput)(
        span(DemoStyles.get.demoOutpuLabel)("Evens: "),
        span(GlobalStyles.get.col, GlobalStyles.get.width66, GlobalStyles.get.textLeft)(repeat(evens)(renderer))
      ),
      div(DemoStyles.get.demoOutput)(
        span(DemoStyles.get.demoOutpuLabel)("Odds: "),
        span(GlobalStyles.get.col, GlobalStyles.get.width66, GlobalStyles.get.textLeft)(repeat(odds)(renderer))
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
    email.addValidator((element: String) =>
      element match {
        case emailRegex(text) => Valid
        case _ => Invalid("It's not an email!")
      }
    )

    div(DemoStyles.get.demoIOWrapper, GlobalStyles.get.table)(
      TextInput.debounced(email, maxlength := 32, DemoStyles.get.demoInlineField, GlobalStyles.get.width100),
      span(DemoStyles.get.demoInlineField, DemoStyles.get.demoOutput, GlobalStyles.get.width50)(
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

    div(DemoStyles.get.demoIOWrapper)(
      TextInput.debounced(name, `type` := "text", placeholder := "Type your name...", DemoStyles.get.demoInput, GlobalStyles.get.width100),
      div(DemoStyles.get.demoOutput)(
        span(translatedDynamic(Translations.udash.hello)(_.apply()))
      ),
      div(DemoStyles.get.demoOutput)(
        produce(name)(n => span(translatedDynamic(Translations.udash.withArg)(_.apply(n))).render)
      ),
      div(DemoStyles.get.demoOutput)(
        ul(
          li(DemoStyles.get.navItem)(a(DemoStyles.get.underlineLink, onclick := (() => changeLang(Lang("en"))))("EN")),
          li(DemoStyles.get.navItem)(a(DemoStyles.get.underlineLink, onclick := (() => changeLang(Lang("pl"))))("PL")),
          li(DemoStyles.get.navItem)(a(DemoStyles.get.underlineLink, onclick := (() => changeLang(Lang("de"))))("DE")),
          li(DemoStyles.get.navItem)(a(DemoStyles.get.underlineLink, onclick := (() => changeLang(Lang("sp"))))("SP"))
        )
      )
    ).render
  }

  def components: Element = {
    import io.udash._

    import io.udash.bootstrap.button.UdashButton
    import io.udash.bootstrap.form.{UdashForm, UdashInputGroup}
    import io.udash.bootstrap.modal.UdashModal
    import io.udash.bootstrap.progressbar.UdashProgressBar

    import org.scalajs.dom
    import scalajs.concurrent.JSExecutionContext.Implicits
    import Implicits.queue
    import scalacss.ScalatagsCss._
    import scalatags.JsDom.all._

    val text = Property[String]("")
    val progress = Property[Int](0)
    val disableButton = Property(text.get.isEmpty)
    text.listen(s => disableButton.set(s.isEmpty))

    lazy val modal: UdashModal = UdashModal(
      backdrop = UdashModal.NoneBackdrop,
      modalSize = ModalSize.Small
    )(
      headerFactory = Some(() => h4(bind(text)).render),
      bodyFactory = Some(() => {
        div(
          h4("Closing..."),
          UdashProgressBar.animated(progress)().render
        ).render
      })
    )

    def makeProgress(): Unit = progress.get match {
      case v if v >= 100 =>
        text.set("")
        progress.set(0)
        modal.hide()
      case v =>
        progress.set(v + 25)
        dom.window.setTimeout(() => makeProgress(), 750)
    }

    modal.listen {
      case UdashModal.ModalShownEvent(_) => makeProgress()
    }

    div(DemoStyles.get.demoIOWrapper)(
      // apply Bootstrap styles
      div(cls := "bootstrap", DemoStyles.get.demoBootstrap)(
        UdashInputGroup()(
          UdashInputGroup.addon("Modal title: "),
          UdashInputGroup.input(
            TextInput.debounced(text).render
          ),
          UdashInputGroup.buttons(
            UdashButton(
              disabled = disableButton
            )("Go!", modal.openButtonAttrs()).render
          )
        ).render,
        modal.render
      )
    ).render
  }
}
