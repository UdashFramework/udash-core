package io.udash.web.guide.styles.demo

import io.udash.css.{CssBase, CssStyle}
import scalacss.internal.AV

import scala.concurrent.duration.FiniteDuration

object ExampleMixins extends CssBase {
  import dsl._

  def animation(keyframes: CssStyle, duration: FiniteDuration,
                iterationCount: AV = animationIterationCount.infinite,
                easing: AV = animationTimingFunction.easeInOut): CssStyle = mixin(
    animationName(keyframes),
    iterationCount,
    animationDuration(duration),
    easing
  )
}
