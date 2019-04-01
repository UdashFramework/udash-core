package io.udash.web.guide.styles.demo

import java.util.concurrent.TimeUnit

import io.udash.css.CssBase
import io.udash.web.commons.styles.attributes.Attributes

import scala.concurrent.duration.FiniteDuration

object ExampleStyles extends CssBase {
  import dsl._

  import scala.language.postfixOps

  val btn = style(
    display.inlineBlock,
    padding(6 px, 12 px),
    fontSize(14 px),
    fontWeight._400,
    textAlign.center,
    whiteSpace.nowrap,
    verticalAlign.middle,
    cursor.pointer,
    borderWidth(1 px),
    borderStyle.solid,
    borderColor.transparent,
    borderRadius(4 px),
    userSelect := "none",
    overflow.hidden
  )

  val btnDefault = style(
    color(c"#000000"),
    backgroundColor(c"#FFFFFF"),
    borderColor(c"#CCCCCC"),

    &.hover (
      color(c"#333333"),
      backgroundColor(c"#E6E6E6"),
      borderColor(c"#ADADAD"),
      textDecoration := "none"
    )
  )

  val btnSuccess = style(
    color(c"#FFFFFF"),
    backgroundColor(c"#5CB85C"),
    borderColor(c"#4CAE4C"),

    &.hover (
      color(c"#FFFFFF"),
      backgroundColor(c"#449D44"),
      borderColor(c"#398439")
    )
  )

  val btnAnimated = style(
    &.hover {
      ExampleMixins.animation(ExampleKeyframes.colorPulse, FiniteDuration(750, TimeUnit.MILLISECONDS))
    }
  )

  val innerOff = style(
    padding(6 px, 12 px),
    borderBottomWidth(1 px),
    borderBottomStyle.solid,
    borderBottomColor(c"#CCCCCC")
  )

  val innerOn = style(
    padding(6 px, 12 px),
    color(c"#FFFFFF"),
    backgroundColor(c"#5CB85C"),
    borderTopWidth(1 px),
    borderTopStyle.solid,
    borderTopColor(c"#4CAE4C")
  )

  val swither = style(
    display.inlineBlock,
    borderWidth(1 px),
    borderStyle.solid,
    borderRadius(4 px),
    borderColor(c"#CCCCCC"),
    cursor.pointer,
    userSelect := "none",

    &.hover (
      textDecoration := "none"
    ),

    &.attr(Attributes.data(Attributes.State), "on") (
      unsafeChild(s".${innerOff.className}") (
        visibility.hidden
      ),
      unsafeChild(s".${innerOn.className}") (
        visibility.visible
      )
    ),
    &.attr(Attributes.data(Attributes.State), "off") (
      unsafeChild(s".${innerOff.className}") (
        visibility.visible
      ),
      unsafeChild(s".${innerOn.className}") (
        visibility.hidden
      )
    )
  )

  val mediaDesktop = style(
    backgroundColor(c"#E6E6E6"),
    media.maxWidth(769 px) (
      display.none
    )
  )

  val mediaTablet = style(
    display.none,
    backgroundColor(c"#5CB85C"),

    media.maxWidth(768 px) (
      display.block
    )
  )

  val mediaContainer = style(
    position.relative,
    fontSize(28 px),
    textAlign.center,
    padding(40 px, 0 px),
    borderWidth(2 px),
    borderStyle.solid,
    borderColor(c"#000000")
  )
}
