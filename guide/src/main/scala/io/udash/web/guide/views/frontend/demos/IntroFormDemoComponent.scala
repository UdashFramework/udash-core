package io.udash.web.guide.views.frontend.demos

import java.lang.Float

import io.udash._
import io.udash.bootstrap.UdashBootstrap.ComponentId
import io.udash.bootstrap.button.{ButtonStyle, UdashButton}
import io.udash.bootstrap.form.UdashInputGroup
import io.udash.core.Presenter
import io.udash.web.commons.views.Component
import io.udash.web.guide.styles.partials.GuideStyles

import scala.collection.mutable
import scala.util.Random
import scalatags.JsDom
import scalatags.JsDom.all._

case class IntroFormDemoModel(minimum: Int, between: Int, maximum: Int)

class IntroFormDemoComponent extends Component {
  override def getTemplate: Modifier = new IntroFormDemoViewFactory().create()._1.getTemplate

  class FormDemoState extends State {
    override type HierarchyRoot = FormDemoState
    override def parentState = None
  }

  /** Prepares model, view and presenter for demo component */
  class IntroFormDemoViewFactory extends ViewFactory[FormDemoState] {
    // Context object is a recommended place to keep things like
    // `ExecutionContext` or server RPC connector
    import io.udash.web.guide.Context._

    override def create(): (View, Presenter[FormDemoState]) = {
      val model = ModelProperty(
        IntroFormDemoModel(0, 10, 42)
      )

      model.addValidator((element: IntroFormDemoModel) => {
        val errors = mutable.ArrayBuffer[String]()
        if (element.minimum > element.maximum)
          errors += "Minimum is bigger than maximum!"
        if (element.minimum > element.between)
          errors += "Minimum is bigger than your value!"
        if (element.between > element.maximum)
          errors += "Maximum is smaller than your value!"

        if (errors.isEmpty) Valid
        else Invalid(errors.map(DefaultValidationError))
      })

      val presenter = new IntroFormDemoPresenter(model)
      val view = new IntroFormDemoView(model, presenter)

      (view, presenter)
    }
  }

  class IntroFormDemoPresenter(model: ModelProperty[IntroFormDemoModel])
    extends Presenter[FormDemoState] {

    override def handleState(state: FormDemoState): Unit = {}

    /** Sets random values in demo model */
    def randomize() =
      model.set(IntroFormDemoModel(
        Random.nextInt(100) - 25,
        Random.nextInt(100),
        Random.nextInt(100) + 25
      ))
  }

  class IntroFormDemoView(model: ModelProperty[IntroFormDemoModel],
                          presenter: IntroFormDemoPresenter) extends FinalView {
    import io.udash.web.guide.Context._

    import JsDom.all._

    private val i2s = (i: Int) => i.toString
    private val s2i = (s: String) => Float.parseFloat(s).toInt

    private val minimum = model.subProp(_.minimum).transform(i2s, s2i)
    private val between = model.subProp(_.between).transform(i2s, s2i)
    private val maximum = model.subProp(_.maximum).transform(i2s, s2i)

    val randomizeButton = UdashButton(
      buttonStyle = ButtonStyle.Primary,
      componentId = ComponentId("randomize")
    )("Randomize")

    randomizeButton.listen {
      case UdashButton.ButtonClickEvent(_) =>
        presenter.randomize()
    }

    def getTemplate: Modifier = div(id := "frontend-intro-demo", GuideStyles.frame, GuideStyles.useBootstrap)(
      UdashInputGroup()(
        UdashInputGroup.input(
          NumberInput.debounced(minimum)(id := "minimum").render
        ),
        UdashInputGroup.addon(" <= "),
        UdashInputGroup.input(
          NumberInput.debounced(between)(id := "between").render
        ),
        UdashInputGroup.addon(" <= "),
        UdashInputGroup.input(
          NumberInput.debounced(maximum)(id := "maximum").render
        ),
        UdashInputGroup.buttons(
          randomizeButton.render
        )
      ).render,
      h3("Is valid?"),
      p(id := "valid")(
        valid(model) {
          case Valid => span("Yes").render
          case Invalid(errors) => Seq(
            span("No, because:"),
            ul(GuideStyles.defaultList)(
              errors.map(e => li(e.message))
            )
          ).map(_.render)
        }
      )
    )
  }
}
