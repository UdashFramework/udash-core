package io.udash.bootstrap
package progressbar

import io.udash._
import io.udash.bootstrap.progressbar.ProgressBarStyle.Default
import io.udash.bootstrap.progressbar.UdashProgressBar.ValueStringifier
import io.udash.wrappers.jquery._
import org.scalajs.dom.Element

import scala.concurrent.ExecutionContext
import scalatags.JsDom.all._

class UdashProgressBar private[progressbar](val progress: Property[Int], val showPercentage: Property[Boolean], barStyle: ProgressBarStyle,
                                            minValue: Int, maxValue: Int, minWidthEm: Int, valueStringifier: ValueStringifier)
                                           (implicit ec: ExecutionContext) extends UdashBootstrapComponent {

  protected def modifiers = Seq(
    barStyle, role := "progressbar", aria.valuenow.bind(progress.transform(_.toString)),
    progress.reactiveApply((el, pct) => jQ(el).width(s"$pct%")),
    aria.valuemin := minValue, aria.valuemax := maxValue, minWidth := s"${minWidthEm}em"
  )

  override val componentId = UdashBootstrap.newId()
  lazy val stringifiedValue: ReadableProperty[String] = progress.transform(valueStringifier)

  override lazy val render: Element =
    div(BootstrapStyles.ProgressBar.progress)(
      div(id := componentId, modifiers)(
      produce(showPercentage)(shouldShow =>
        (
          if (shouldShow) div(bind(stringifiedValue))
          else span(BootstrapStyles.Visibility.srOnly)(bind(stringifiedValue))
        ).render
      )
    )
  ).render

}

class AnimatedUdashProgressBar private[progressbar](progress: Property[Int], showPercentage: Property[Boolean], val animate: Property[Boolean],
                                                    barStyle: ProgressBarStyle, minValue: Int, maxValue: Int, minWidthEm: Int, valueStringifier: ValueStringifier)
                                                   (implicit ec: ExecutionContext)
  extends UdashProgressBar(progress, showPercentage, barStyle, minValue, maxValue, minWidthEm, valueStringifier) {
  override protected lazy val modifiers: Seq[Modifier] = super.modifiers ++ Seq(BootstrapStyles.active.styleIf(animate), ProgressBarStyle.Striped)
}

object UdashProgressBar {
  import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

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
    * @param valueStringifier Converts progress to string displayed inside component.
    * @return `UdashProgressBar` component, call render to create DOM element.
    */
  def apply(progress: Property[Int] = Property(0), showPercentage: Property[Boolean] = Property(true),
            barStyle: ProgressBarStyle = Default, minValue: Int = 0, maxValue: Int = 100, minWidth: Int = 2)
           (valueStringifier: ValueStringifier = percentValueStringifier(minValue, maxValue))(implicit ec: ExecutionContext): UdashProgressBar =
    new UdashProgressBar(progress, showPercentage, barStyle, minValue, maxValue, minWidth, valueStringifier)

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
    * @param valueStringifier Converts progress to string displayed inside component.
    * @return `UdashProgressBar` component, call render to create DOM element.
    */
  def animated(progress: Property[Int] = Property(0), showPercentage: Property[Boolean] = Property(true), animate: Property[Boolean] = Property(true),
               barStyle: ProgressBarStyle = Default, minValue: Int = 0, maxValue: Int = 100, minWidth: Int = 2)
              (valueStringifier: ValueStringifier = percentValueStringifier(minValue, maxValue))(implicit ec: ExecutionContext): AnimatedUdashProgressBar =
    new AnimatedUdashProgressBar(progress, showPercentage, animate, barStyle, minValue, maxValue, minWidth, valueStringifier)

}
