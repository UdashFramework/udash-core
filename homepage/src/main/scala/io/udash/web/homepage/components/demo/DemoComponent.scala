package io.udash.web.homepage.components.demo

import io.udash.core.DomWindow
import io.udash.properties.Property
import io.udash.view.Component
import io.udash.web.commons.components.CodeBlock
import io.udash.web.commons.styles.utils.StyleConstants
import io.udash.web.homepage.Context._
import io.udash.web.homepage.IndexState
import io.udash.web.homepage.styles.partials.{DemoStyles, HomepageStyles}
import io.udash.web.commons.views.Image
import io.udash.wrappers.jquery._
import io.udash.wrappers.jquery.scrollbar._
import org.scalajs.dom.Element

import scala.scalajs.js
import scalacss.ScalatagsCss._
import scalatags.JsDom.all._

/**
  * Created by malchik on 2016-04-04.
  */

class DemoComponent(url: Property[String]) extends Component {

  url.listen(onUrlChange)

  val window = jQ(DomWindow)
  window.resize((element: Element, _: JQueryEvent) => onResize())

  private def onUrlChange(update: String) = {
    val entryOption = DemoComponent.demoEntries.find(_.url.substring(1) == update)
    val entry = entryOption.getOrElse(DemoComponent.demoEntries.head)
    val urlString = s""""${entry.url}""""
    val tab = jQ(template).find(s".${DemoStyles.demoTabsLink.htmlClass}[href=$urlString]")

    jQ(template).not(tab).find(s".${DemoStyles.demoTabsLink.htmlClass}").attr(DemoComponent.ActiveAttribute, "false")
    tab.attr(DemoComponent.ActiveAttribute, "true")

    jqPreviewContainer
      .animate(Map[String, Any]("opacity" -> 0), 150, EasingFunction.swing,
        (el: Element) => {
          jqPreviewContainer
            .html(entry.preview)
            .animate(Map[String, Any]("opacity" -> 1), 200)
        })

    jqCodeContainer
      .animate(Map[String, Any]("opacity" -> 0), 150, EasingFunction.swing,
        (el: Element) => {
          jq2CustomScrollbar(jqCodeContainer).destroy()
          jqCodeContainer
            .html(entry.code)
            .animate(Map[String, Any]("opacity" -> 1), 200)

          initCustomScroll()
          js.Dynamic.global.Prism.highlightAll()
        })
  }

  private def initCustomScroll(): Unit = {
    val scrollAxis = if (window.width > StyleConstants.Sizes.BodyWidth) CustomScrollbarAxis.XY else CustomScrollbarAxis.X
    jq2CustomScrollbar(jqCodeContainer).customScrollbar(CustomScrollbarOptions
      .axis(scrollAxis)
      .autoHideScrollbar(true)
    )
  }

  private def onResize(): Unit = {
    jq2CustomScrollbar(jqCodeContainer).destroy()
    initCustomScroll()
  }

  private val codeContainer = div(DemoStyles.demoCode).render
  private val previewContainer = div(DemoStyles.demoPreview).render
  private lazy val jqCodeContainer = jQ(codeContainer)
  private lazy val jqPreviewContainer = jQ(previewContainer)

  private lazy val template = div(DemoStyles.demoComponent)(
    Image("laptop.png", "", DemoStyles.laptopImage),
    div(DemoStyles.demoBody)(
      div(DemoStyles.demoSources)(
        ul(DemoStyles.demoTabs)(
          DemoComponent.demoEntries.map(entry =>
            li(DemoStyles.demoTabsItem)(
              a(DemoStyles.demoTabsLink, href := entry.url)(
                entry.name
              )
            )
          )
        ),
        codeContainer
      ),
      previewContainer
    )
  ).render

  override def getTemplate: Element = template
}

object DemoComponent {
  val ActiveAttribute = "data-active"

  def helloWorldCode = CodeBlock(
    """import scalajs.concurrent.JSExecutionContext.Implicits
      |import Implicits.queue
      |import org.scalajs.dom._
      |import scalatags.JsDom.all._
      |import io.udash._
      |
      |val name = Property("World")
      |
      |div(
      |  TextInput(name), br,
      |  produce(name)(name => h3(s"Hello, $name!").render)
      |).render""".stripMargin
  )(HomepageStyles)

  def propertiesCode = CodeBlock(
    """import scalajs.concurrent.JSExecutionContext.Implicits
      |import Implicits.queue
      |import org.scalajs.dom._
      |import scalatags.JsDom.all._
      |import io.udash._
      |
      |def isEven(n: Int): Boolean =
      |  n % 2 == 0
      |
      |def renderer(n: ReadableProperty[Int]): Element =
      |  span(s"${n.get}, ").render
      |
      |val input = Property("")
      |val numbers = SeqProperty[Int](Seq.empty)
      |val odds = numbers.filter(n => !isEven(n))
      |val evens = numbers.filter(isEven)
      |
      |div(
      |  TextInput(input)(
      |    onkeyup := ((ev: KeyboardEvent) =>
      |      if (ev.keyCode == ext.KeyCode.Enter) {
      |       val n: Try[Int] = Try(input.get.toInt)
      |        if (n.isSuccess) {
      |          numbers.append(n.get)
      |          input.set("")
      |        }
      |      })
      |  ), br,
      |  "Numbers: ", repeat(numbers)(renderer), br,
      |  "Evens: ", repeat(evens)(renderer), br,
      |  "Odds: ", repeat(odds)(renderer)
      |).render""".stripMargin
  )(HomepageStyles)

  def validationCode = CodeBlock(
    """import scalajs.concurrent.JSExecutionContext.Implicits
      |import Implicits.queue
      |import org.scalajs.dom._
      |import scalatags.JsDom.all._
      |import io.udash._
      |
      |val emailRegex = "([\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,})".r
      |
      |val email = Property("example@mail.com")
      |email.addValidator(new Validator[String] {
      |  def apply(element: String)
      |           (implicit ec: ExecutionContext) = Future {
      |    element match {
      |      case emailRegex(text) => Valid
      |      case _ => Invalid(Seq("It's not an email!"))
      |    }
      |  }
      |})
      |
      |div(
      |  TextInput(email), br,
      |  "Valid: ", bindValidation(email,
      |    _ => span("Wait...").render,
      |    {
      |      case Valid => span("Yes").render
      |      case Invalid(_) => span("No").render
      |    },
      |    _ => span("ERROR").render
      |  )
      |).render""".stripMargin
  )(HomepageStyles)

  def i18n = CodeBlock(
    """import scalajs.concurrent.JSExecutionContext.Implicits
      |import Implicits.queue
      |import scalacss.ScalatagsCss._
      |import scalatags.JsDom.all._
      |
      |import io.udash._
      |import io.udash.i18n._
      |
      |val name = Property("World")
      |
      |object Translations {
      |  import TranslationKey._
      |  object udash {
      |    val hello = key("udash.hello")
      |    val withArg = key1[String]("udash.withArg")
      |  }
      |}
      |
      |object FrontendTranslationProvider {
      |  private val translations = Map(
      |    Lang("en") -> Bundle(BundleHash("enHash"), Map(
      |      "udash.hello" -> "Hello, Udash!",
      |      "udash.withArg" -> "Hello, {}!"
      |    )),
      |    Lang("pl") -> Bundle(BundleHash("plHash"), Map(
      |      "udash.hello" -> "Witaj, Udash!",
      |      "udash.withArg" -> "Witaj, {}!"
      |    )),
      |    Lang("de") -> Bundle(BundleHash("deHash"), Map(
      |      "udash.hello" -> "Hallo, Udash!",
      |      "udash.withArg" -> "Hallo, {}!"
      |    )),
      |    Lang("sp") -> Bundle(BundleHash("spHash"), Map(
      |      "udash.hello" -> "Hola, Udash!",
      |      "udash.withArg" -> "Hola, {}!"
      |    ))
      |  )
      |
      |  def apply(): LocalTranslationProvider =
      |    new LocalTranslationProvider(translations)
      |}
      |
      |implicit val translationProvider: TranslationProvider =
      |  FrontendTranslationProvider()
      |
      |implicit val lang: Property[Lang] =
      |  Property(Lang("en"))
      |
      |def changeLang(l: Lang) = lang.set(l)
      |
      |div(
      |  TextInput(name, `type` := "text",
      |            placeholder := "Type your name..."),
      |  div(
      |    translatedDynamic(Translations.udash.hello)(_.apply())
      |  ),
      |  div(produce(name)(n => span(
      |    translatedDynamic(Translations.udash.withArg)(_.apply(n))
      |  ).render),
      |  ul(
      |    li(a(onclick := (() => changeLang(Lang("en"))))("EN")),
      |    li(a(onclick := (() => changeLang(Lang("pl"))))("PL")),
      |    li(a(onclick := (() => changeLang(Lang("de"))))("DE")),
      |    li(a(onclick := (() => changeLang(Lang("sp"))))("SP"))
      |  )
      |).render""".stripMargin
  )(HomepageStyles)

  def components = CodeBlock(
    """
      |import io.udash._
      |
      |import io.udash.bootstrap.button.UdashButton
      |import io.udash.bootstrap.form.{UdashForm, UdashInputGroup}
      |import io.udash.bootstrap.modal.UdashModal
      |import io.udash.bootstrap.progressbar.UdashProgressBar
      |
      |import org.scalajs.dom
      |import scalajs.concurrent.JSExecutionContext.Implicits
      |import Implicits.queue
      |import scalacss.ScalatagsCss._
      |import scalatags.JsDom.all._
      |
      |val text = Property[String]("")
      |val progress = Property[Int](0)
      |val disableButton = Property(text.get.isEmpty)
      |text.listen(s => disableButton.set(s.isEmpty))
      |
      |lazy val modal: UdashModal = UdashModal(
      |  backdrop = UdashModal.NoneBackdrop,
      |  modalSize = ModalSize.Small
      |)(
      |  headerFactory = Some(() => h4(bind(text)).render),
      |  bodyFactory = Some(() => {
      |    div(
      |      h4("Closing..."),
      |      UdashProgressBar.animated(progress)().render
      |    ).render
      |  })
      |)
      |
      |def makeProgress(): Unit = progress.get match {
      |  case v if v >= 100 =>
      |    text.set("")
      |    progress.set(0)
      |    modal.hide()
      |  case v =>
      |    progress.set(v + 25)
      |    dom.window.setTimeout(() => makeProgress(), 750)
      |}
      |
      |modal.listen {
      |  case UdashModal.ModalShownEvent(_) => makeProgress()
      |}
      |
      |div(DemoStyles.demoIOWrapper)(
      |  // apply Bootstrap styles
      |  div(cls := "bootstrap", DemoStyles.demoBootstrap)(
      |    UdashInputGroup()(
      |      UdashInputGroup.addon("Modal title: "),
      |      UdashInputGroup.input(
      |        TextInput.debounced(text).render
      |      ),
      |      UdashInputGroup.buttons(
      |        UdashButton(
      |          disabled = disableButton
      |        )("Go!", modal.openButtonAttrs()).render
      |      )
      |    ).render,
      |    modal.render
      |  )
      |).render
    """.stripMargin
  )(HomepageStyles)

  def demoEntries: Seq[DemoEntry] = Seq(
    DemoEntry("Hello World", IndexState(Option("hello")).url, DemoPreview.helloWorldDemo, helloWorldCode),
    DemoEntry("Properties", IndexState(Option("properties")).url, DemoPreview.propertiesDemo, propertiesCode),
    DemoEntry("Validation", IndexState(Option("validation")).url, DemoPreview.validationDemo, validationCode),
    DemoEntry("i18n", IndexState(Option("i18n")).url, DemoPreview.i18n, i18n),
    DemoEntry("Components", IndexState(Option("components")).url, DemoPreview.components, components)
  )
}

case class DemoEntry(name: String, url: String, preview: Element, code: Element)
