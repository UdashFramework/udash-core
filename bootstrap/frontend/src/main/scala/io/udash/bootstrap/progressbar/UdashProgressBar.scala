package io.udash.bootstrap
package progressbar

import io.udash._
import io.udash.bootstrap.progressbar.ProgressBarStyle.Default
import io.udash.bootstrap.progressbar.UdashProgressBar.ValueStringifier
import io.udash.wrappers.jquery._
import org.scalajs.dom.Element
import scalatags.JsDom.all._

sealed abstract class UdashProgressBarBase private[progressbar](val progress: ReadableProperty[Int],
                                                                val showPercentage: ReadableProperty[Boolean],
                                                                barStyle: ProgressBarStyle, minValue: Int,
                                                                maxValue: Int, minWidthEm: Int,
                                                                valueStringifier: ValueStringifier,
                                                                override val componentId: ComponentId)

  extends UdashBootstrapComponent {

  import io.udash.css.CssView._

  protected def modifiers = Seq(
    barStyle, role := "progressbar", aria.valuenow.bind(progress.transform(_.toString)),
    progress.reactiveApply((el, pct) => jQ(el).width(s"$pct%")),
    aria.valuemin := minValue, aria.valuemax := maxValue, minWidth := s"${minWidthEm}em"
  )

  override final val render: Element = {
    val stringifiedValue: ReadableProperty[String] = progress.transform(valueStringifier)
    div(BootstrapStyles.ProgressBar.progress)(
      div(id := componentId, modifiers)(
        produce(showPercentage) { shouldShow =>
          if (shouldShow) div(bind(stringifiedValue)).render
          else span(BootstrapStyles.Visibility.srOnly)(bind(stringifiedValue)).render
        }
      )
    ).render
  }

}

final class UdashProgressBar private[progressbar](progress: ReadableProperty[Int], showPercentage: ReadableProperty[Boolean],
                                                  barStyle: ProgressBarStyle, minValue: Int, maxValue: Int, minWidthEm: Int,
                                                  valueStringifier: ValueStringifier, override val componentId: ComponentId)

  extends UdashProgressBarBase(progress, showPercentage, barStyle, minValue, maxValue, minWidthEm, valueStringifier, componentId)

final class AnimatedUdashProgressBar private[progressbar](progress: ReadableProperty[Int], showPercentage: ReadableProperty[Boolean],
                                                          animate: ReadableProperty[Boolean], barStyle: ProgressBarStyle,
                                                          minValue: Int, maxValue: Int, minWidthEm: Int,
                                                          valueStringifier: ValueStringifier, override val componentId: ComponentId)

  extends UdashProgressBarBase(progress, showPercentage, barStyle, minValue, maxValue, minWidthEm, valueStringifier, componentId) {

  import io.udash.css.CssView._

  override protected def modifiers: Seq[Modifier] =
    super.modifiers ++ Seq(BootstrapStyles.active.styleIf(animate), ProgressBarStyle.Striped)
}

object UdashProgressBar {
  type ValueStringifier = Int => String
  val ToStringValueStringifier: ValueStringifier = _.toString

  /** Default method of converting progress to string. */
  def percentValueStringifier(min: Int, max: Int): ValueStringifier =
    value => ((value - min) * 100 / (max - min)) + "%"

  /**
    * Creates progress bar component.
    * More: <a href="http://getbootstrap.com/components/#progress">Bootstrap Docs</a>.
    *
    * @param progress         Property containing Integer in range `minValue` to `maxValue`.
    * @param showPercentage   If true, display progress string.
    * @param barStyle         Component style.
    * @param minValue         Minimum progress value.
    * @param maxValue         Maximum progress value.
    * @param minWidth         Minimal width of the progress indicator.
    * @param componentId Id of the root DOM node.
    * @param valueStringifier Converts progress to string displayed inside component.
    * @return `UdashProgressBar` component, call render to create DOM element.
    */
  def apply(progress: ReadableProperty[Int] = Property(0), showPercentage: ReadableProperty[Boolean] = Property(true),
            barStyle: ProgressBarStyle = Default, minValue: Int = 0, maxValue: Int = 100, minWidth: Int = 2,
            componentId: ComponentId = ComponentId.newId())
           (valueStringifier: ValueStringifier = percentValueStringifier(minValue, maxValue)): UdashProgressBar =
    new UdashProgressBar(progress, showPercentage, barStyle, minValue, maxValue, minWidth, valueStringifier, componentId)

  /**
    * Creates animated bar component.
    * More: <a href="http://getbootstrap.com/components/#progress">Bootstrap Docs</a>.
    *
    * @param progress         Property containing Integer in range `minValue` to `maxValue`.
    * @param showPercentage   If true, display progress string.
    * @param animate          If true, turns on progress bar animation
    * @param barStyle         Component style.
    * @param minValue         Minimum progress value.
    * @param maxValue         Maximum progress value.
    * @param minWidth         Minimal width of the progress indicator.
    * @param componentId Id of the root DOM node.
    * @param valueStringifier Converts progress to string displayed inside component.
    * @return `UdashProgressBar` component, call render to create DOM element.
    */
  def animated(progress: ReadableProperty[Int] = Property(0), showPercentage: ReadableProperty[Boolean] = Property(true),
               animate: ReadableProperty[Boolean] = Property(true), barStyle: ProgressBarStyle = Default, minValue: Int = 0,
               maxValue: Int = 100, minWidth: Int = 2, componentId: ComponentId = ComponentId.newId())
              (valueStringifier: ValueStringifier = percentValueStringifier(minValue, maxValue)): AnimatedUdashProgressBar =
    new AnimatedUdashProgressBar(progress, showPercentage, animate, barStyle, minValue, maxValue, minWidth, valueStringifier, componentId)

}
