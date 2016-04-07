package io.udash.homepage.styles.utils

import java.util.concurrent.TimeUnit

import io.udash.homepage.styles.constant.StyleConstants

import scala.concurrent.duration.FiniteDuration
import scala.language.postfixOps
import scalacss.{AV, Attr, Length, ValueT}
import scalacss.Defaults._

object StyleUtils extends StyleSheet.Inline {
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

  def transition(): StyleA = style(
    transitionProperty := "all",
    transitionDuration(new FiniteDuration(250, TimeUnit.MILLISECONDS)),
    transitionTimingFunction.easeInOut
  )

  def transition(duration: FiniteDuration): StyleA = style(
    transitionProperty := "all",
    transitionDuration(duration),
    transitionTimingFunction.easeInOut
  )

  def transition(duration: FiniteDuration, delay: FiniteDuration): StyleA = style(
    transitionProperty := "all",
    transitionDuration(duration),
    transitionTimingFunction.easeInOut,
    transitionDelay(delay)
  )

  def transition(property: Attr, duration: FiniteDuration): StyleA = style(
    transitionProperty := property.toString(),
    transitionDuration(duration),
    transitionTimingFunction.easeInOut
  )

  def border(bColor: ValueT[ValueT.Color] = StyleConstants.Colors.GreyExtra, bWidth: Length[Double] = 1.0 px, bStyle: AV = borderStyle.solid): StyleA = style(
    borderWidth(bWidth),
    bStyle,
    borderColor(bColor)
  )
}
