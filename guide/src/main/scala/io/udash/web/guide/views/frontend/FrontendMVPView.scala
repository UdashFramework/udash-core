package io.udash.web.guide.views.frontend

import io.udash._
import io.udash.web.commons.components.CodeBlock
import io.udash.web.commons.views.{ClickableImageFactory, ImageFactoryPrefixSet}
import io.udash.web.guide.{Context, _}
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.views.References
import org.scalajs.dom

import scalatags.JsDom

case object FrontendMVPViewPresenter extends DefaultViewPresenterFactory[FrontendMVPState.type](() => new FrontendMVPView)

class FrontendMVPView extends View {
  import Context._

  import JsDom.all._
  import scalacss.ScalatagsCss._

  override def getTemplate: Modifier = div(
    h2("Model, View, Presenter & ViewPresenter"),
    p("A single page in Udash app is based on four elements:"),
    ul(GuideStyles.get.defaultList)(
      li(
        "Model - based on the ", a(href := FrontendPropertiesState.url)("Properties"), " mechanism, ",
        "it provides one and two-ways bindings to DOM elements."
      ),
      li(
        "View - extends ", i("View"), " and creates a ", a(href := References.ScalatagsHomepage)("Scalatags"), " template ",
        "with a method getting a child view to render."
      ),
      li(
        "Presenter - extends ", i("Presenter"), ", it should contain a business logic of the connected view. It also handles ",
        "application state changes."
      ),
      li(
        "ViewPresenter - extends ", i("ViewPresenter"), ", it was discussed in detail ",
        a(href := FrontendRoutingState(None).url)("Routing"), " chapter. ViewPresenter is responsible for creating a View and a Presenter. "
      )
    ),
    ClickableImageFactory(ImageFactoryPrefixSet.Frontend, "mvp.png", "MVP in the Udash", GuideStyles.get.imgBig, GuideStyles.get.frame),
    h3("ViewPresenter"),
    p(
      "The ViewPresenter responsibility is simple. It has to prepare Model, View, Presenter and then link them together. ",
      "If you want to create a static view, then you can use ", i("DefaultViewPresenterFactory"), " witch ",
      "will create ", i("EmptyPresenter"), " for your view."
    ),
    h3("Model"),
    p(
      "The Udash framework brings a powerful Properties mechanism, ",
      "which is used as Model in Udash-based applications. All you have to do is:"
    ),
    CodeBlock(
      """trait NumbersInRange {
        |  def minimum: Int
        |  def maximum: Int
        |  def numbers: Seq[Int]
        |}
        |
        |val numbers: ModelProperty[NumbersInRange] = ModelProperty[NumbersInRange]
        |numbers.subProp(_.minimum).set(0)
        |numbers.subProp(_.maximum).set(42)
        |
        |val s: SeqProperty[Int] = numbers.subSeq(_.numbers)
        |s.set(Seq(3,7,20,32))
        |s.replace(idx = 1, amount = 2, values = Seq(8,9,10))
        |""".stripMargin
    )(GuideStyles),
    p("The Properties system is described in the ", a(href := FrontendPropertiesState.url)("Properties"), " chapter."),
    h3("Presenter"),
    p(
      "The Presenter should contain all business logic of a view: user interaction callbacks and server communication. ",
      "It should not call any methods of a View class. The View and the Presenter should communicate via Model properties. ",
      "When implementing a presenter, you should remember, that the ", i("handleState"), " method does not have to be called only on ",
      "view initialization. For example:"
    ),
    CodeBlock(
      """class ExamplePresenter(model: Property[Int]) extends Presenter[SomeState] {
        |  override def handleState(state: SomeState) =
        |    model.set(state.initValue)
        |
        |  def incButtonClick(): Unit =
        |    model.set(model.get + 1)
        |
        |  def decButtonClick(): Unit =
        |    model.set(model.get - 1)
        |}""".stripMargin
    )(GuideStyles),
    h3("View"),
    p(
      "The View implementation usually gets the Model and the Presenter as constructor arguments. They can be used ",
      "in the ", a(href := References.ScalatagsHomepage)("Scalatags"), " template of a view as user interaction callbacks. ",
      "The Model can be bound to a template and will automatically update on the Model changes."
    ),
    CodeBlock(
      """class ExampleView(model: Property[Int], presenter: ExamplePresenter) extends View {
        |  import io.udash.guide.Context._
        |
        |  import JsDom.all._
        |
        |  private val child = div().render
        |
        |  override def getTemplate: dom.Element = div(
        |    h1("Example view"),
        |    p("This is example view with buttons..."),
        |    h3("Model bind example"),
        |    div(
        |      button(onclick :+= (ev => {
        |        presenter.decButtonClick()
        |        true
        |      }))("-"),
        |      button(onclick :+= (ev => {
        |        presenter.incButtonClick()
        |        true
        |      }))("-"),
        |      bind(model)
        |    ),
        |    h3("Below you can find my child view!"),
        |    child
        |  ).render
        |
        |  override def renderChild(childView: View): Unit = {
        |    import io.udash.wrappers.jquery.jQ
        |    jQ(child).html(childView.getTemplate)
        |  }
        |}""".stripMargin
    )(GuideStyles),
    h2("What's next?"),
    p(
      "Take a look at the ", a(href := FrontendTemplatesState.url)("Scalatags & ScalaCSS"), " chapter to ",
      "learn more about creating view templates and styling them in Udash. Visit the ",
      a(href := FrontendPropertiesState.url)("Properties"), " chapter to read about data model in Udash applications."
    )
  )

  override def renderChild(view: View): Unit = {}
}