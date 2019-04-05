package io.udash.web.guide.views.frontend

import io.udash._
import io.udash.css.CssView
import io.udash.web.commons.components.CodeBlock
import io.udash.web.guide.components.ForceBootstrap
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.views.References
import io.udash.web.guide.views.frontend.demos.IntroFormDemoComponent
import io.udash.web.guide.{Context, _}
import scalatags.JsDom

case object FrontendIntroViewFactory extends StaticViewFactory[FrontendIntroState.type](() => new FrontendIntroView)


class FrontendIntroView extends FinalView with CssView {
  import Context._
  import JsDom.all._

  override def getTemplate: Modifier = div(
    h2("Introduction"),
    p(
      "At present JavaScript is an undisputed market leader of frontend development. With frameworks like AngularJS, ReactJS ",
      "or jQuery development of small, modern and responsive websites is quite easy and fast. On the other hand, ",
      "JavaScript is untyped and not so easy to master. This leads to huge maintenance costs of JavaScript based projects ",
      "and tears of developers working on such codebase. "
    ),
    h3("A new hope"),
    p(
      "The ", a(href := References.ScalaJsHomepage)("Scala.js"), " project tries to make developers lives easier. It brings the ",
      "power of the ", a(href := References.ScalaHomepage)("Scala language"), " and compiles it to JavaScript. Thanks to this, ",
      "we can develop in a type-safe, modern, developer friendly language and publish a project as a website like with JavaScript. "
    ),
    p(
      "Udash framework provides tools to make web applications development with ",
      a(href := References.ScalaJsHomepage)("Scala.js"), " fast and easy. You might have already read about Udash ",
      a(href := RpcIntroState.url)("RPC"), " system. In this part of the guide you will read about: "
    ),
    ul(GuideStyles.defaultList)(
      li("Routing in Udash based applications."),
      li("The Properties mechanism for application modelling."),
      li(
        a(href := References.ScalatagsHomepage)("ScalaTags"), " and ", a(href := References.ScalaCssHomepage)("ScalaCSS"),
        " usage as HTML and CSS replacement."
      ),
      li("The property bindings for ", a(href := References.ScalatagsHomepage)("ScalaTags"), ".")
    ),
    p("All these features will make your life as a frontend developer pleasant."),
    p("To start development import Udash classes as follows:"),
    CodeBlock("""import io.udash._""".stripMargin)(GuideStyles),
    h4("Example"),
    p(
      "Take a look at this simple form with a validation. We will not discuss the implementation here, because ",
      "it is quite self-descriptive. All those elements will be described in detail in the following chapters. ",
    ),
    ForceBootstrap(new IntroFormDemoComponent()),
    p(
      "Notice that this example follows the recommended code structure with separated model, view, presenter and ",
      "factory. It also assumes that you have created ", i("IntroFormDemoState"), " somewhere in your states hierarchy. "
    ),
    CodeBlock(
      """import java.{lang => jl}
        |
        |import io.udash._
        |import io.udash.bootstrap.UdashBootstrap
        |import io.udash.bootstrap.UdashBootstrap.ComponentId
        |import io.udash.bootstrap.button.{ButtonStyle, UdashButton}
        |import io.udash.bootstrap.form.UdashInputGroup
        |
        |import scala.collection.mutable
        |import scala.util.Random
        |
        |/** The form's model structure. */
        |case class IntroFormDemoModel(minimum: Int, between: Int, maximum: Int)
        |object IntroFormDemoModel extends HasModelPropertyCreator[IntroFormDemoModel]
        |
        |/** Prepares model, view and presenter for demo view. */
        |class IntroFormDemoViewFactory extends ViewFactory[IntroFormDemoState.type] {
        |  override def create(): (View, Presenter[IntroFormDemoState.type]) = {
        |    // Main model of the view
        |    val model = ModelProperty(
        |      IntroFormDemoModel(0, 10, 42)
        |    )
        |
        |    // model validation
        |    model.addValidator { (element: IntroFormDemoModel) =>
        |      val errors = mutable.ArrayBuffer[String]()
        |      if (element.minimum > element.maximum)
        |        errors += "Minimum is bigger than maximum!"
        |      if (element.minimum > element.between)
        |        errors += "Minimum is bigger than your value!"
        |      if (element.between > element.maximum)
        |        errors += "Maximum is smaller than your value!"
        |      if (errors.isEmpty) Valid
        |      else Invalid(errors.map(DefaultValidationError))
        |    }
        |
        |    val presenter = new IntroFormDemoPresenter(model)
        |    val view = new IntroFormDemoView(model, presenter)
        |    (view, presenter)
        |  }
        |}
        |
        |/** Contains the business logic of this view. */
        |class IntroFormDemoPresenter(model: ModelProperty[IntroFormDemoModel])
        |  extends Presenter[IntroFormDemoState.type] {
        |
        |  /** We don't need any initialization, so it's empty. */
        |  override def handleState(state: IntroFormDemoState.type): Unit = {}
        |
        |  /** Sets random values in demo model */
        |  def randomize(): Unit =
        |    model.set(IntroFormDemoModel(
        |      Random.nextInt(100) - 25,
        |      Random.nextInt(100),
        |      Random.nextInt(100) + 25
        |    ))
        |}
        |
        |/** The view description. */
        |class IntroFormDemoView(
        |  model: ModelProperty[IntroFormDemoModel],
        |  presenter: IntroFormDemoPresenter
        |) extends FinalView {
        |  import scalatags.JsDom.all._
        |
        |  private val i2s = (i: Int) => i.toString
        |  private val s2i = (s: String) => jl.Float.parseFloat(s).toInt
        |
        |  // String representations of the model subproperties
        |  // These values are synchronised with the original value
        |  private val minimum = model.subProp(_.minimum).transform(i2s, s2i)
        |  private val between = model.subProp(_.between).transform(i2s, s2i)
        |  private val maximum = model.subProp(_.maximum).transform(i2s, s2i)
        |
        |  // Button from Udash Bootstrap wrapper
        |  private val randomizeButton = UdashButton(
        |    buttonStyle = ButtonStyle.Primary,
        |    componentId = ComponentId("randomize")
        |  )("Randomize")
        |
        |  // on button click calls `randomize` method from presenter
        |  randomizeButton.listen {
        |    case UdashButton.ButtonClickEvent(_, _) =>
        |      presenter.randomize()
        |  }
        |
        |  def getTemplate: Modifier = div(
        |    // load Bootstrap styles from CDN
        |    UdashBootstrap.loadBootstrapStyles(),
        |    // another wrapped Bootstrap component
        |    UdashInputGroup()(
        |      UdashInputGroup.input(
        |        // input synchronised with the model
        |        NumberInput(minimum)(id := "minimum").render
        |      ),
        |      UdashInputGroup.addon(" <= "),
        |      UdashInputGroup.input(
        |        NumberInput(between)(id := "between").render
        |      ),
        |      UdashInputGroup.addon(" <= "),
        |      UdashInputGroup.input(
        |        NumberInput(maximum)(id := "maximum").render
        |      ),
        |      UdashInputGroup.buttons(
        |        randomizeButton.render
        |      )
        |    ).render,
        |    h3("Is valid?"),
        |    p(id := "valid")(
        |      // validation binding - waits for model changes and updates the view
        |      valid(model) {
        |        case Valid => span("Yes").render
        |        case Invalid(errors) => Seq(
        |          span("No, because:"),
        |          ul(errors.map(e => li(e.message)))
        |        ).map(_.render)
        |      }
        |    )
        |  )
        |}""".stripMargin
    )(GuideStyles),
    h2("What's next?"),
    p(
      "Take a look at the ", a(href := FrontendRoutingState(None).url)("Routing in Udash"),
      " chapter to learn more about selecting a view based on a URL."
    )
  )
}