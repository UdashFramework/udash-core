package io.udash.homepage.components.demo

import io.udash.core.{DomWindow, Window}
import io.udash.homepage.Context._
import io.udash.homepage.{Context, ErrorState, IndexState, RoutingState}
import io.udash.homepage.components.CodeBlock
import io.udash.homepage.styles.constant.StyleConstants
import io.udash.homepage.styles.partials.DemoStyles
import io.udash.homepage.views.Image
import io.udash.wrappers.jquery.scrollbar._
import io.udash.properties.Property
import io.udash.routing.StateChangeEvent
import io.udash.view.Component
import io.udash.wrappers.jquery._
import org.scalajs.dom.Element

import scala.scalajs.js
import scalacss.ScalatagsCss._
import scalatags.JsDom.all._

/**
  * Created by malchik on 2016-04-04.
  */

class DemoComponent(url: Property[String]) extends Component {

  url.listen(onUrlChange)
  Window.onResize(onResize)

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
    val scrollAxis = if (Window.width > StyleConstants.Sizes.BodyWidth) CustomScrollbarAxis.XY else CustomScrollbarAxis.X
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
  )()

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
  )()

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
  )()

  def demoEntries: Seq[DemoEntry] = Seq(
    DemoEntry("Hello, World!", IndexState(Option("hello")).url, DemoPreview.helloWorldDemo, helloWorldCode),
    DemoEntry("Properties", IndexState(Option("properties")).url, DemoPreview.propertiesDemo, propertiesCode),
    DemoEntry("Validation", IndexState(Option("validation")).url, DemoPreview.validationDemo, validationCode)
  )
}

case class DemoEntry(name: String, url: String, preview: Element, code: Element)
