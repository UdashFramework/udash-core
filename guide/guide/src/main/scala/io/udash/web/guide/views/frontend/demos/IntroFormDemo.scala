package io.udash.web.guide.views.frontend.demos

import java.{lang => jl}

import io.udash._
import io.udash.bootstrap.button.UdashButton
import io.udash.bootstrap.form.UdashInputGroup
import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.bootstrap.utils.BootstrapStyles.Color
import io.udash.css.CssView
import io.udash.web.guide.components.BootstrapUtils
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.views.ext.demo.AutoDemo
import scalatags.JsDom

import scala.collection.mutable
import scala.util.Random

object IntroFormDemo extends AutoDemo with CssView {

  case class IntroFormDemoModel(minimum: Int, between: Int, maximum: Int)

  object IntroFormDemoModel extends HasModelPropertyCreator[IntroFormDemoModel]

  private val (rendered, source) = {
    /** The form's model structure.
      * case class IntroFormDemoModel(minimum: Int, between: Int, maximum: Int)
      * object IntroFormDemoModel extends HasModelPropertyCreator[IntroFormDemoModel]
      */

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
        model.addValidator { element: IntroFormDemoModel =>
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

    class IntroFormDemoView(model: ModelProperty[IntroFormDemoModel], presenter: IntroFormDemoPresenter) extends FinalView {

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
        buttonStyle = Color.Primary.toProperty,
        componentId = ComponentId("randomize")
      )(_ => "Randomize")

      // on button click calls `randomize` method from presenter
      randomizeButton.listen {
        case UdashButton.ButtonClickEvent(_, _) =>
          presenter.randomize()
      }

      def getTemplate: Modifier = div(id := "frontend-intro-demo", GuideStyles.frame, GuideStyles.useBootstrap)(
        // load Bootstrap styles from CDN
        // UdashBootstrap.loadBootstrapStyles(),
        // another wrapped Bootstrap component
        div(BootstrapStyles.Spacing.margin(
          side = BootstrapStyles.Side.Bottom,
          size = BootstrapStyles.SpacingSize.Normal
        ))(UdashInputGroup()(
          UdashInputGroup.input(
            // input synchronised with the model
            NumberInput(minimum)(id := "minimum").render
          ),
          UdashInputGroup.appendText(" <= "),
          UdashInputGroup.input(
            NumberInput(between)(id := "between").render
          ),
          UdashInputGroup.appendText(" <= "),
          UdashInputGroup.input(
            NumberInput(maximum)(id := "maximum").render
          ),
          randomizeButton.render
        ).render),
        h3(BootstrapStyles.Spacing.margin(
          side = BootstrapStyles.Side.Bottom,
          size = BootstrapStyles.SpacingSize.Normal
        ))("Is valid?"),
        p(id := "valid", BootstrapUtils.wellStyles)(
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

    val (view, _) = new IntroFormDemoViewFactory().create()
    view.getTemplate
  }.withSourceCode

  override protected def demoWithSource(): (JsDom.all.Modifier, Iterator[String]) = {
    (rendered, source.lines.drop(1))
  }
}
