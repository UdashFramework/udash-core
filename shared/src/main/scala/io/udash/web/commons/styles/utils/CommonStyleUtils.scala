package io.udash.web.commons.styles.utils

import java.util.concurrent.TimeUnit

import io.udash.css.{CssBase, CssStyle}

import scala.concurrent.duration.FiniteDuration
import scala.language.postfixOps
import scalacss.internal.Macros.Color
import scalacss.internal.{AV, Attr, Length}

object CommonStyleUtils extends CssBase {
  import dsl._

  val middle = style(
    top(50 %%),
    transform := "translateY(-50%)"
  )

  val center = style(
    top(50 %%),
    left(50 %%),
    transform := "translateY(-50%) translateX(-50%)"
  )

  val relativeMiddle = style(
    middle,
    position.relative
  )

  val absoluteMiddle = style(
    middle,
    position.absolute
  )

  val absoluteCenter = style(
    center,
    position.absolute
  )

  def transition(): CssStyle = style(
    transitionProperty := "all",
    transitionDuration(new FiniteDuration(250, TimeUnit.MILLISECONDS)),
    transitionTimingFunction.easeInOut
  )

  def transition(duration: FiniteDuration): CssStyle = style(
    transitionProperty := "all",
    transitionDuration(duration),
    transitionTimingFunction.easeInOut
  )

  def transition(duration: FiniteDuration, delay: FiniteDuration): CssStyle = style(
    transitionProperty := "all",
    transitionDuration(duration),
    transitionTimingFunction.easeInOut,
    transitionDelay(delay)
  )

  def transition(property: Attr, duration: FiniteDuration): CssStyle = style(
    transitionProperty := property.toString(),
    transitionDuration(duration),
    transitionTimingFunction.easeInOut
  )

  def border(bColor: Color = StyleConstants.Colors.GreyExtra, bWidth: Length[Double] = 1.0 px, bStyle: AV = borderStyle.solid): CssStyle = style(
    borderWidth(bWidth),
    bStyle,
    borderColor(bColor)
  )

  def bShadow(x: Int = 2, y: Int = 2, blur: Int = 5, spread: Int = 0, color: Color = c"#000000", opacity: Double = .4, inset: Boolean = false): CssStyle = style(
    boxShadow := s"${if (inset) "inset " else ""}${x}px ${y}px ${blur}px ${spread}px ${hexToRGBA(color, opacity)}"
  )

  private def hexToRGBA(color: Color, opacity: Double = 1): String = {
    val cNumber = Integer.parseInt(color.value.replace("#", ""), 16)
    val r = (cNumber.toInt >> 16) & 0xFF
    val g = (cNumber.toInt >>  8) & 0xFF
    val b = (cNumber.toInt >>  0) & 0xFF

    s"rgba($r, $g, $b, $opacity)"
  }
}
