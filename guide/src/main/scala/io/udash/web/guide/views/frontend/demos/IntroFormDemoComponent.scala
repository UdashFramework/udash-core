package io.udash.web.guide.views.frontend.demos

import java.lang.Float

import io.udash._
import io.udash.bootstrap.BootstrapStyles
import io.udash.bootstrap.UdashBootstrap.ComponentId
import io.udash.bootstrap.button.{ButtonStyle, UdashButton}
import io.udash.bootstrap.form.UdashInputGroup
import io.udash.web.guide.styles.partials.GuideStyles
import org.scalajs.dom.{Element, MouseEvent}

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random
import scalatags.JsDom
import scalatags.JsDom.all._
import io.udash.web.commons.views.Component

case class IntroFormDemoModel(minimum: Int, between: Int, maximum: Int)

class IntroFormDemoComponent extends Component {
  override def getTemplate: Modifier = IntroFormDemoViewPresenter()

  /** Prepares model, view and presenter for demo component */
  object IntroFormDemoViewPresenter {
    import io.udash.web.guide.Context._
    def apply(): Modifier = {
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
      new IntroFormDemoView(model, presenter).render
    }
  }

  class IntroFormDemoPresenter(model: ModelProperty[IntroFormDemoModel]) {
    private val random = new Random()

    /** Sets random values in demo model */
    def randomize() =
      model.set(IntroFormDemoModel(
        random.nextInt(100) - 25,
        random.nextInt(100),
        random.nextInt(100) + 25
      ))
  }

  class IntroFormDemoView(model: ModelProperty[IntroFormDemoModel], presenter: IntroFormDemoPresenter) {
    import io.udash.web.guide.Context._

    import JsDom.all._
    import scalacss.ScalatagsCss._

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

    def render: Modifier = div(id := "frontend-intro-demo", GuideStyles.get.frame, GuideStyles.get.useBootstrap)(
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
            ul(GuideStyles.get.defaultList)(
              errors.map(e => li(e.message))
            )
          ).map(_.render)
        }
      )
    )
  }
}
