package io.udash.web.guide.styles.utils

import io.udash.css.{CssBase, CssStyle}
import io.udash.web.commons.styles.utils.StyleConstants
import scalacss.internal.Macros.Color
import scalacss.internal.{AV, Attr, Length}

import scala.concurrent.duration.{DurationInt, FiniteDuration}
import scala.language.postfixOps

object GuideStyleUtils extends CssBase {
  import dsl._

  val relativeMiddle: CssStyle = mixin(
    top(50 %%),
    transform := "translateY(-50%)",
    position.relative
  )

  def transition(property: Attr = all, duration: FiniteDuration = 250 milliseconds): CssStyle = mixin(
    transitionProperty := property.toString(),
    transitionDuration(duration),
    transitionTimingFunction.easeInOut
  )

  def border(bColor: Color = StyleConstants.Colors.GreyExtra, bWidth: Length[Double] = 1.0 px, bStyle: AV = borderStyle.solid): CssStyle = mixin(
    borderWidth(bWidth),
    bStyle,
    borderColor(bColor)
  )
}
