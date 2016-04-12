package io.udash.guide.views.frontend

import io.udash._
import io.udash.guide.{Context, _}
import io.udash.guide.components.CodeBlock
import io.udash.guide.styles.partials.GuideStyles
import io.udash.guide.views.References
import io.udash.guide.views.frontend.demos.IntroFormDemoComponent
import org.scalajs.dom

import scalatags.JsDom
import scalacss.ScalatagsCss._

case object FrontendIntroViewPresenter extends DefaultViewPresenterFactory[FrontendIntroState.type](() => new FrontendIntroView)


class FrontendIntroView extends View {
  import Context._

  import JsDom.all._

  override def getTemplate: dom.Element = div(
    h2("Introduction"),
    p(
      "At present JavaScript is an undisputed market leader of the frontend development. With frameworks like AngularJS, ReactJS ",
      "or jQuery development of small, modern and responsive websites is quite easy and fast. On the other hand, ",
      "JavaScript is typeless and not so easy to master. This leads to huge maintenance costs of JavaScript based projects ",
      "and tears of developers working on such codebase. "
    ),
    h3("New hope"),
    p("Let's start with a quote:"),
    blockquote(
      "JS leads to frustration, frustration leads to anger, anger leads to Scala.js."
    ),
    p(
      "The ", a(href := References.scalaJsHomepage)("Scala.js"), " project tries to make developers lives easier. It brings us ",
      "power of the ", a(href := References.scalaHomepage)("Scala language"), " and compiles it to JavaScript. Thanks to this, ",
      "we can develop in a type safe, modern, developer friendly language and publish a project as a website like with JavaScript. "
    ),
    p(
      "The Udash framework provides tools to make web applications development with ",
      a(href := References.scalaJsHomepage)("Scala.js"), " fast and easy. You might have already read about the Udash ",
      a(href := RpcIntroState.url)("RPC"), " system. In this part of the guide you will read about: "
    ),
    ul(GuideStyles.defaultList)(
      li("Routing in Udash based applications."),
      li("The powerful Properties system for an application model."),
      li(
        a(href := References.scalatagsHomepage)("Scalatags"), " and ", a(href := References.scalaCssHomepage)("ScalaCSS"),
        " usage as HTML and CSS replacement."
      ),
      li("The properties bindings for ", a(href := References.scalatagsHomepage)("Scalatags"), ".")
    ),
    p("All these features will make your life as a frontend developer pleasant."),
    h4("Example"),
    p(
      "Take a look at this simple form with a validation. We will not discuss the implementation here, because ",
      "it is quite self descriptive. All those elements will be described in detail in the following chapters."
    ),
    new IntroFormDemoComponent(),
    CodeBlock(
      """/** Demo model interface */
        |trait IntroFormDemoModel {
        |  def minimum: Int
        |  def between: Int
        |  def maximum: Int
        |}
        |
        |class IntroFormDemoComponent extends Component {
        |  override def getTemplate: Element = IntroFormDemoViewPresenter()
        |
        |  /** IntroFormDemoModel validator, checks if minimum <= between <= maximum */
        |  object IntroFormDemoModelValidator extends Validator[IntroFormDemoModel] {
        |    override def apply(element: IntroFormDemoModel)
        |                      (implicit ec: ExecutionContext): Future[ValidationResult] =
        |      Future {
        |        val errors = mutable.ArrayBuffer[String]()
        |        if (element.minimum > element.maximum)
        |          errors += "Minimum is bigger than maximum!"
        |        if (element.minimum > element.between)
        |          errors += "Minimum is bigger than your value!"
        |        if (element.between > element.maximum)
        |          errors += "Maximum is smaller than your value!"
        |
        |        if (errors.isEmpty) Valid
        |        else Invalid(errors.toSeq)
        |      }
        |  }
        |
        |  /** Prepares model, view and presenter for demo component */
        |  object IntroFormDemoViewPresenter {
        |    import Context._
        |    def apply(): Element = {
        |      val model = ModelProperty[IntroFormDemoModel]
        |      model.subProp(_.minimum).set(0)
        |      model.subProp(_.between).set(10)
        |      model.subProp(_.maximum).set(42)
        |
        |      model.addValidator(IntroFormDemoModelValidator)
        |
        |      val presenter = new IntroFormDemoPresenter(model)
        |      new IntroFormDemoView(model, presenter).render
        |    }
        |  }
        |
        |  class IntroFormDemoPresenter(model: ModelProperty[IntroFormDemoModel]) {
        |    private val random = new Random()
        |
        |    /** Sets random values in demo model */
        |    def randomize() = {
        |      model.subProp(_.minimum).set(random.nextInt(100) - 25)
        |      model.subProp(_.between).set(random.nextInt(100))
        |      model.subProp(_.maximum).set(random.nextInt(100) + 25)
        |    }
        |  }
        |
        |  class IntroFormDemoView(model: ModelProperty[IntroFormDemoModel],
        |                          presenter: IntroFormDemoPresenter) {
        |    import Context._
        |    import io.udash.view.TagsBinding._
        |    import io.udash.forms._
        |
        |    import JsDom.all._
        |    import scalacss.Defaults._
        |    import scalacss.ScalatagsCss._
        |
        |    private def i2s(i: Int) = i.toString
        |    private def s2i(s: String) = Float.parseFloat(s).toInt
        |
        |    private val minimum = model.subProp(_.minimum).transform(i2s, s2i)
        |    private val between = model.subProp(_.between).transform(i2s, s2i)
        |    private val maximum = model.subProp(_.maximum).transform(i2s, s2i)
        |
        |    def render: Element = div(id := "frontend-intro-demo")(
        |      div(BootstrapStyles.inputGroup)(
        |        NumberInput(minimum)(id := "minimum", BootstrapStyles.formControl),
        |        span(BootstrapStyles.inputGroupAddon)(" <= "),
        |        NumberInput(between)(id := "between", BootstrapStyles.formControl),
        |        span(BootstrapStyles.inputGroupAddon)(" <= "),
        |        NumberInput(maximum)(id := "maximum", BootstrapStyles.formControl),
        |        div(BootstrapStyles.inputGroupBtn)(
        |          button(
        |            id := "randomize",
        |            BootstrapStyles.btn + BootstrapStyles.btnPrimary
        |          )(onclick :+= (ev => {
        |            presenter.randomize()
        |            true
        |          }))("Randomize")
        |        )
        |      ),
        |      div(
        |        b("Is valid?"), br(),
        |        bindValidation(model,
        |          _ => span("...").render,
        |          {
        |            case Valid => span("Yes").render
        |            case Invalid(errors) => span(
        |              "No, because:",
        |              ul(
        |                errors.map(e => li(e))
        |              )
        |            ).render
        |          },
        |          error => span(s"Validation error: $error").render
        |        )
        |      )
        |    ).render
        |  }
        |}
      """.stripMargin
    )(),
    h2("What's next?"),
    p(
      "Take a look at the ", a(href := FrontendRoutingState(None).url)("Routing in Udash"),
      " chapter to learn more about selecting a view based on a URL."
    )
  ).render

  override def renderChild(view: View): Unit = {}
}