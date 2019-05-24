package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash._
import io.udash.bootstrap.button.{UdashButton, UdashButtonGroup}
import io.udash.bootstrap.progressbar.UdashProgressBar
import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.bootstrap.utils.BootstrapStyles.{Side, SpacingSize}
import io.udash.css.CssView
import io.udash.logging.CrossLogging
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom

object ProgressBarDemo extends AutoDemo with CrossLogging with CssView {

  import JsDom.all._
  import io.udash.bootstrap.utils.BootstrapImplicits._

  private val (rendered, source) = {
    val showPercentage = Property(true)
    val animate = Property(true)
    val value = Property(50)

    def bottomMargin() = {
      BootstrapStyles.Spacing.margin(
        side = Side.Bottom,
        size = SpacingSize.Normal
      )
    }

    div(
      div(
        UdashButtonGroup()(
          UdashButton.toggle(
            active = showPercentage
          )("Show percentage").render,
          UdashButton.toggle(
            active = animate
          )("Animate").render
        )
      ), br,
      div(bottomMargin())(
        UdashProgressBar(
          value,
          showPercentage,
          barStyle = Some(BootstrapStyles.Color.Success).toProperty
        )()
      ),
      div(bottomMargin())(
        UdashProgressBar(
          value,
          showPercentage,
          stripped = true.toProperty
        )(
          (value, min, max, nested) => Seq[Modifier](
            nested(bind(value.combine(min)(_ - _).combine(
              max.combine(min)(_ - _))(_ * 100 / _))
            ), " percent"
          )
        )
      ),
      div(bottomMargin())(
        UdashProgressBar(
          value,
          showPercentage,
          stripped = true.toProperty, animated = animate,
          barStyle = Some(BootstrapStyles.Color.Danger).toProperty
        )(),
      ),
      div(bottomMargin())(
        NumberInput(value.transform(_.toString, Integer.parseInt))(
          BootstrapStyles.Form.control, placeholder := "Percentage"
        )
      )
    )
  }.withSourceCode

  override protected def demoWithSource(): (JsDom.all.Modifier, Iterator[String]) = {
    (div(GuideStyles.frame)(rendered), source.lines.drop(1))
  }
}

