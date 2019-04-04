package io.udash.web.commons.styles.utils

import io.udash.css.{CssBase, CssStyle}
import scalacss.internal.Macros.Color
import scalacss.internal.{AV, Attr, Length}

import scala.concurrent.duration.{DurationInt, FiniteDuration}
import scala.language.postfixOps

object CommonStyleUtils extends CssBase {
  import dsl._

  val middle: CssStyle = mixin(
    top(50 %%),
    transform := "translateY(-50%)"
  )

  val center: CssStyle = mixin(
    top(50 %%),
    left(50 %%),
    transform := "translateY(-50%) translateX(-50%)"
  )

  val relativeMiddle: CssStyle = mixin(
    middle,
    position.relative
  )

  val absoluteMiddle: CssStyle = mixin(
    middle,
    position.absolute
  )

  val absoluteCenter: CssStyle = mixin(
    center,
    position.absolute
  )

  def transition(property: Attr = all, duration: FiniteDuration = 250 milliseconds): CssStyle = style(
    transitionProperty := property.toString(),
    transitionDuration(duration),
    transitionTimingFunction.easeInOut
  )

  def border(bColor: Color = StyleConstants.Colors.GreyExtra, bWidth: Length[Double] = 1.0 px, bStyle: AV = borderStyle.solid): CssStyle = style(
    borderWidth(bWidth),
    bStyle,
    borderColor(bColor)
  )
}
