package io.udash.bootstrap
package progressbar

import io.udash._
import io.udash.bindings.modifiers.Binding
import io.udash.bootstrap.utils.{BootstrapStyles, UdashBootstrapComponent}
import io.udash.component.ComponentId
import org.scalajs.dom.Element
import scalatags.JsDom.all._

final class UdashProgressBar private[progressbar](
  progress: ReadableProperty[Int],
  showPercentage: ReadableProperty[Boolean],
  barStyle: ReadableProperty[Option[BootstrapStyles.Color]],
  stripped: ReadableProperty[Boolean],
  animated: ReadableProperty[Boolean],
  minValue: ReadableProperty[Int],
  maxValue: ReadableProperty[Int],
  minWidthEm: ReadableProperty[Int],
  override val componentId: ComponentId
)(labelFactory: (ReadableProperty[Int], ReadableProperty[Int], ReadableProperty[Int], Binding.NestedInterceptor) => Modifier) extends UdashBootstrapComponent {

  import io.udash.css.CssView._

  protected def barModifiers: Seq[Modifier] = Seq[Modifier](
    role := "progressbar", BootstrapStyles.ProgressBar.progressBar,
    nestedInterceptor(
      width.bind(
        progress.combine(minValue.combine(maxValue)((_, _)))((_, _)).transform {
          case (current, (min, max)) => s"${100 * (current - min) / (max - min)}%"
        }
      )
    ),
    nestedInterceptor(aria.valuenow.bind(progress.transform(_.toString))),
    nestedInterceptor(aria.valuemin.bind(minValue.transform(_.toString))),
    nestedInterceptor(aria.valuemax.bind(maxValue.transform(_.toString))),
    nestedInterceptor(minWidth.bind(minWidthEm.transform(v => s"${v}em"))),
    nestedInterceptor((BootstrapStyles.Background.color _).reactiveOptionApply(barStyle)),
    nestedInterceptor(BootstrapStyles.ProgressBar.animated.styleIf(animated)),
    nestedInterceptor(BootstrapStyles.ProgressBar.striped.styleIf(stripped))
  )

  override val render: Element = {
    div(BootstrapStyles.ProgressBar.progress)(
      div(id := componentId, barModifiers)(
        nestedInterceptor(
          produceWithNested(showPercentage) { (shouldShow, nested) =>
            if (shouldShow) div(labelFactory(progress, minValue, maxValue, nested)).render
            else span(BootstrapStyles.Visibility.srOnly)(labelFactory(progress, minValue, maxValue, nested)).render
          }
        )
      )
    ).render
  }
}

object UdashProgressBar {
  val ToStringValueStringifier: (ReadableProperty[Int], ReadableProperty[Int], ReadableProperty[Int], Binding.NestedInterceptor) => Modifier =
    (p, _, _, nested) => nested(bind(p.transform(_.toString)))

  /** Default method of converting progress to string. */
  val PercentValueStringifier: (ReadableProperty[Int], ReadableProperty[Int], ReadableProperty[Int], Binding.NestedInterceptor) => Modifier = {
    (progress, min, max, nested) =>
      nested(bind(
        progress.combine(min.combine(max)((_, _)))((_, _)).transform {
          case (current, (min, max)) =>
            val pct = 100 * (current - min) / (max - min)
            s"$pct%"
        }
      ))
  }

  /**
    * Creates a progress bar component.
    * More: <a href="http://getbootstrap.com/docs/4.1/components/progress/">Bootstrap Docs</a>.
    *
    * @param progress       Property containing Integer in range from `minValue` to `maxValue`.
    * @param showPercentage If true, display progress string.
    * @param barStyle       A bar color. One of the standard bootstrap colors `BootstrapStyles.Color`.
    * @param stripped       If true, applies striped style to the bar.
    * @param animated       If true, applies animation style to the bar.
    * @param minValue       A minimum progress value.
    * @param maxValue       A maximum progress value.
    * @param minWidth       A minimal width of the progress indicator.
    * @param componentId    An id of the root DOM node.
    * @param labelFactory   Creates a label content from current progress, min value, max value.
    *                       Use the provided interceptor to properly clean up bindings inside the content.
    * @return A `UdashProgressBar` component, call `render` to create a DOM element.
    */
  def apply(
    progress: ReadableProperty[Int],
    showPercentage: ReadableProperty[Boolean] = UdashBootstrap.False,
    barStyle: ReadableProperty[Option[BootstrapStyles.Color]]  = UdashBootstrap.None,
    stripped: ReadableProperty[Boolean] = UdashBootstrap.False,
    animated: ReadableProperty[Boolean] = UdashBootstrap.False,
    minValue: ReadableProperty[Int] = 0.toProperty,
    maxValue: ReadableProperty[Int] = 100.toProperty,
    minWidth: ReadableProperty[Int] = 1.toProperty,
    componentId: ComponentId = ComponentId.newId()
  )(
    labelFactory: (ReadableProperty[Int], ReadableProperty[Int], ReadableProperty[Int], Binding.NestedInterceptor) => Modifier =
      PercentValueStringifier
  ): UdashProgressBar = {
    new UdashProgressBar(
      progress, showPercentage, barStyle, stripped, animated,
      minValue, maxValue, minWidth, componentId
    )(labelFactory)
  }

}
