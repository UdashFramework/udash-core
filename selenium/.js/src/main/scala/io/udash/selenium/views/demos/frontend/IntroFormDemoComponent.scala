package io.udash.selenium.views.demos.frontend

import java.{lang => jl}

import io.udash._
import io.udash.bootstrap.UdashBootstrap.ComponentId
import io.udash.bootstrap.button.{ButtonStyle, UdashButton}
import io.udash.bootstrap.form.UdashInputGroup
import io.udash.css.CssView
import scalatags.JsDom.all._

import scala.collection.mutable
import scala.util.Random

/** The form's model structure. */
case class IntroFormDemoModel(minimum: Int, between: Int, maximum: Int)
object IntroFormDemoModel extends HasModelPropertyCreator[IntroFormDemoModel]

class IntroFormDemoComponent extends CssView {
  def getTemplate: Modifier = new IntroFormDemoViewFactory().create()._1.getTemplate

  object IntroFormDemoState extends State {
    override type HierarchyRoot = IntroFormDemoState.type
    override def parentState = None
  }

  /** Prepares model, view and presenter for demo view. */
  class IntroFormDemoViewFactory extends ViewFactory[IntroFormDemoState.type] {
    override def create(): (View, Presenter[IntroFormDemoState.type]) = {
      // Main model of the view
      val model = ModelProperty(
        IntroFormDemoModel(0, 10, 42)
      )

      // model validation
      model.addValidator { (element: IntroFormDemoModel) =>
        val errors = mutable.ArrayBuffer[String]()
        if (element.minimum > element.maximum)
          errors += "Minimum is bigger than maximum!"
        if (element.minimum > element.between)
          errors += "Minimum is bigger than your value!"
        if (element.between > element.maximum)
          errors += "Maximum is smaller than your value!"
        if (errors.isEmpty) Valid
        else Invalid(errors.map(DefaultValidationError))
      }

      val presenter = new IntroFormDemoPresenter(model)
      val view = new IntroFormDemoView(model, presenter)
      (view, presenter)
    }
  }

  /** Contains the business logic of this view. */
  class IntroFormDemoPresenter(model: ModelProperty[IntroFormDemoModel])
    extends Presenter[IntroFormDemoState.type] {

    /** We don't need any initialization, so it's empty. */
    override def handleState(state: IntroFormDemoState.type): Unit = {}

    /** Sets random values in demo model */
    def randomize(): Unit =
      model.set(IntroFormDemoModel(
        Random.nextInt(100) - 25,
        Random.nextInt(100),
        Random.nextInt(100) + 25
      ))
  }

  class IntroFormDemoView(
    model: ModelProperty[IntroFormDemoModel],
    presenter: IntroFormDemoPresenter
  ) extends FinalView {
    import scalatags.JsDom.all._

    private val i2s = (i: Int) => i.toString
    private val s2i = (s: String) => jl.Float.parseFloat(s).toInt

    // String representations of the model subproperties
    // These values are synchronised with the original value
    private val minimum = model.subProp(_.minimum).transform(i2s, s2i)
    private val between = model.subProp(_.between).transform(i2s, s2i)
    private val maximum = model.subProp(_.maximum).transform(i2s, s2i)

    // Button from Udash Bootstrap wrapper
    private val randomizeButton = UdashButton(
      buttonStyle = ButtonStyle.Primary,
      componentId = ComponentId("randomize")
    )("Randomize")

    // on button click calls `randomize` method from presenter
    randomizeButton.listen {
      case UdashButton.ButtonClickEvent(_, _) =>
        presenter.randomize()
    }

    def getTemplate: Modifier = div(id := "frontend-intro-demo")(
      // load Bootstrap styles from CDN
      // UdashBootstrap.loadBootstrapStyles(),
      // another wrapped Bootstrap component
      UdashInputGroup()(
        UdashInputGroup.input(
          // input synchronised with the model
          NumberInput(minimum)(id := "minimum").render
        ),
        UdashInputGroup.addon(" <= "),
        UdashInputGroup.input(
          NumberInput(between)(id := "between").render
        ),
        UdashInputGroup.addon(" <= "),
        UdashInputGroup.input(
          NumberInput(maximum)(id := "maximum").render
        ),
        UdashInputGroup.buttons(
          randomizeButton.render
        )
      ).render,
      h3("Is valid?"),
      p(id := "valid")(
        // validation binding - waits for model changes and updates the view
        valid(model) {
          case Valid => span("Yes").render
          case Invalid(errors) => Seq(
            span("No, because:"),
            ul(errors.map(e => li(e.message)))
          ).map(_.render)
        }
      )
    )
  }
}
