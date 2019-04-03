package io.udash.web.guide.styles.demo

import io.udash.css.{CssBase, CssStyle}
import io.udash.web.commons.styles.attributes.Attributes

import scala.concurrent.duration.DurationInt

object ExampleStyles extends CssBase {
  import dsl._

  import scala.language.postfixOps

  val btn: CssStyle = style(
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
    userSelect := none,
    overflow.hidden
  )

  val btnDefault: CssStyle = style(
    color(c"#000000"),
    backgroundColor(c"#FFFFFF"),
    borderColor(c"#CCCCCC"),

    &.hover (
      color(c"#333333"),
      backgroundColor(c"#E6E6E6"),
      borderColor(c"#ADADAD"),
      textDecoration := none
    )
  )

  val btnSuccess: CssStyle = style(
    color(c"#FFFFFF"),
    backgroundColor(c"#5CB85C"),
    borderColor(c"#4CAE4C"),

    &.hover (
      color(c"#FFFFFF"),
      backgroundColor(c"#449D44"),
      borderColor(c"#398439")
    )
  )

  val btnAnimated: CssStyle = style(
    &.hover {
      ExampleMixins.animation(ExampleKeyframes.colorPulse, 750 milliseconds)
    }
  )

  val innerOff: CssStyle = style(
    padding(6 px, 12 px),
    borderBottomWidth(1 px),
    borderBottomStyle.solid,
    borderBottomColor(c"#CCCCCC")
  )

  val innerOn: CssStyle = style(
    padding(6 px, 12 px),
    color(c"#FFFFFF"),
    backgroundColor(c"#5CB85C"),
    borderTopWidth(1 px),
    borderTopStyle.solid,
    borderTopColor(c"#4CAE4C")
  )

  val swither: CssStyle = style(
    display.inlineBlock,
    borderWidth(1 px),
    borderStyle.solid,
    borderRadius(4 px),
    borderColor(c"#CCCCCC"),
    cursor.pointer,
    userSelect := none,

    &.hover (
      textDecoration := none
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

  val mediaDesktop: CssStyle = style(
    backgroundColor(c"#E6E6E6"),
    media.maxWidth(769 px) (
      display.none
    )
  )

  val mediaTablet: CssStyle = style(
    display.none,
    backgroundColor(c"#5CB85C"),

    media.maxWidth(768 px) (
      display.block
    )
  )

  val mediaContainer: CssStyle = style(
    position.relative,
    fontSize(28 px),
    textAlign.center,
    padding(40 px, 0 px),
    borderWidth(2 px),
    borderStyle.solid,
    borderColor(c"#000000")
  )
}
