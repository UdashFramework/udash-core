package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

object ProgressBarDemo extends AutoDemo {

  private val (rendered, source) = {
    import io.udash._
    import io.udash.bootstrap._
    import BootstrapStyles._
    import io.udash.bootstrap.button._
    import io.udash.bootstrap.progressbar.UdashProgressBar
    import io.udash.css.CssStyleName
    import io.udash.css.CssView._
    import scalatags.JsDom.all._

    val showPercentage = Property(true)
    val animate = Property(true)
    val value = Property(50)

    def bottomMargin: CssStyleName = {
      Spacing.margin(
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
      div(bottomMargin)(
        UdashProgressBar(
          progress = value,
          showPercentage = showPercentage,
          barStyle = Some(Color.Success).toProperty
        )()
      ),
      div(bottomMargin)(
        UdashProgressBar(
          progress = value,
          showPercentage = showPercentage,
          stripped = true.toProperty
        )(
          (value, min, max, nested) => Seq[Modifier](
            nested(bind(value.combine(min)(_ - _).combine(
              max.combine(min)(_ - _))(_ * 100 / _))
            ), " percent"
          )
        )
      ),
      div(bottomMargin)(
        UdashProgressBar(
          progress = value,
          showPercentage = showPercentage,
          stripped = true.toProperty,
          animated = animate,
          barStyle = Some(Color.Danger).toProperty
        )(),
      ),
      div(bottomMargin)(
        NumberInput(value.bitransform(_.toString)(_.toInt))(
          Form.control, placeholder := "Percentage"
        )
      )
    ).render
  }.withSourceCode

  override protected def demoWithSource(): (Modifier, Iterator[String]) = {
    import io.udash.css.CssView._
    (rendered.setup(_.applyTags(GuideStyles.frame)), source.linesIterator)
  }
}

