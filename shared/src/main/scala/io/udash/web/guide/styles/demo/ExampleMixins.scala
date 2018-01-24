package io.udash.web.guide.styles.demo

import io.udash.css.{CssBase, CssStyle}

import scala.concurrent.duration.FiniteDuration
import scalacss.internal.AV

object ExampleMixins extends CssBase {
  import dsl._

  def animation(keyframes: CssStyle, duration: FiniteDuration,
                iterationCount: AV = animationIterationCount.infinite,
                easing: AV = animationTimingFunction.easeInOut): CssStyle = style(
    animationName(keyframes),
    iterationCount,
    animationDuration(duration),
    easing
  )
}
