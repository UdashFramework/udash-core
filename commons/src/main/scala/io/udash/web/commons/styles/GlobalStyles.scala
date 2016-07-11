package io.udash.web.commons.styles

import io.udash.web.commons.styles.utils._

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import scalacss.Attr
import scalacss.Defaults._

object GlobalStyles extends StyleSheet.Inline {
  import dsl._

  val clearfix = style(
    &.before (
      content := "\" \"",
      display.table
    ),

    &.after (
      content := "\" \"",
      display.table,
      clear.both
    )
  )

  val main = style(
    position.relative
  )

  val body = style(
    position.relative,
    width(StyleConstants.Sizes.BodyWidth px),
    height(100 %%),
    margin(0 px, auto),

    MediaQueries.tabletLandscape(
      style(
        width(100 %%),
        paddingLeft(2 rem),
        paddingRight(2 rem)
      )
    ),

    MediaQueries.phone(
      style(
        paddingLeft(3 %%),
        paddingRight(3 %%)
      )
    )
  )

  val col = style(
    position.relative,
    display.inlineBlock,
    verticalAlign.top,
    height(100 %%)
  )

  val block = style(
    display.block
  )

  val table = style(
    display.table
  )

  val red = style(
    color(StyleConstants.Colors.Red).important
  )

  val grey = style(
    color(StyleConstants.Colors.Grey).important
  )

  val width100 = style(
    width(100 %%)
  )

  val width50 = style(
    width(50 %%)
  )

  val width33 = style(
    width(100 / 3 %%)
  )

  val width66 = style(
    width(100 * 2 / 3 %%)
  )

  val textLeft = style(
    textAlign.left
  )

  val textRight = style(
    textAlign.right
  )

  val inline = style(
    display.inline
  )

  val hidden = style(
    visibility.hidden
  )

  val noMargin = style(
    margin(`0`).important
  )

  val smallMargin = style(
    margin(5 px).important
  )

  val underlineLink = style(
    position.relative,
    display.block,
    color.white,

    &.after(
      StyleUtils.transition(transform, 250 milliseconds),
      position.absolute,
      top(100 %%),
      left(`0`),
      content := "\" \"",
      width(100 %%),
      borderBottomColor.white,
      borderBottomWidth(1 px),
      borderBottomStyle.solid,
      transform := "scaleX(0)",
      transformOrigin := "100% 50%"
    ),

    &.hover(
      cursor.pointer,
      textDecoration := "none",

      &.after (
        transformOrigin := "0 50%",
        transform := "scaleX(1)"
      )
    )
  )

  val centerBlock = style(
    display.block,
    textAlign.center,
    margin(`0`, auto)
  )
}

