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

  import scalacss.ScalatagsCss._

  protected def modifiers = Seq(
    barStyle, role := "progressbar", aria.valuenow.bind(progress.transform(_.toString)),
    progress.reactiveApply((el, pct) => jQ(el).width(s"$pct%")),
    aria.valuemin := minValue, aria.valuemax := maxValue, minWidth := s"${minWidthEm}em"
  )

  lazy val stringifiedValue: ReadableProperty[String] = progress.transform(valueStringifier)

  override lazy val render: Element = div(BootstrapStyles.ProgressBar.progress)(
    div(modifiers)(
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

  def percentValueStringifier(min: Int, max: Int): ValueStringifier = value => ((value - min) * 100 / (max - min)) + "%"

  def apply(progress: Property[Int] = Property(0), showPercentage: Property[Boolean] = Property(true),
            barStyle: ProgressBarStyle = Default, minValue: Int = 0, maxValue: Int = 100, minWidth: Int = 2
           )(valueStringifier: ValueStringifier = percentValueStringifier(minValue, maxValue))(implicit ec: ExecutionContext): UdashProgressBar =
    new UdashProgressBar(progress, showPercentage, barStyle, minValue, maxValue, minWidth, valueStringifier)

  def animated(progress: Property[Int] = Property(0), showPercentage: Property[Boolean] = Property(true), animate: Property[Boolean],
               barStyle: ProgressBarStyle = Default, minValue: Int = 0, maxValue: Int = 100, minWidth: Int = 2
              )(valueStringifier: ValueStringifier = percentValueStringifier(minValue, maxValue))(implicit ec: ExecutionContext): AnimatedUdashProgressBar =
    new AnimatedUdashProgressBar(progress, showPercentage, animate, barStyle, minValue, maxValue, minWidth, valueStringifier)

}
