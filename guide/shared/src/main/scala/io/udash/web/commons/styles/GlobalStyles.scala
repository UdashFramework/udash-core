package io.udash.web.commons.styles

import io.udash.css._
import io.udash.web.commons.styles.utils._

import scala.language.postfixOps

object GlobalStyles extends CssBase {
  import dsl._

  val clearfix: CssStyle = style(
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

  val main: CssStyle = style(
    position.relative
  )

  val body: CssStyle = style(
    position.relative,
    lineHeight(1.5 rem),
    height(100 %%),
    margin(0 px, auto),

    MediaQueries.desktop(
      padding(StyleConstants.Sizes.BodyPaddingPx px),
    ),

    MediaQueries.tabletLandscape(
      width(100 %%),
      paddingLeft(1.25 rem),
      paddingRight(1.25 rem)
    ),

    MediaQueries.phone(
      paddingLeft(3 %%),
      paddingRight(3 %%)
    )
  )

  val col: CssStyle = mixin(
    position.relative,
    display.inlineBlock,
    verticalAlign.top,
    height(100 %%)
  )

  val block: CssStyle = style(
    display.block
  )

  val red: CssStyle = style(
    color(StyleConstants.Colors.Red).important
  )

  val grey: CssStyle = style(
    color(StyleConstants.Colors.Grey).important
  )

  val inline: CssStyle = style(
    display.inline
  )

  val noMargin: CssStyle = style(
    margin(`0`).important
  )

  val smallMargin: CssStyle = style(
    margin(5 px).important
  )

  val underlineLink: CssStyle = style(
    position.relative,
    display.block,
    color.white,

    &.after(
      CommonStyleUtils.transition(transform),
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
      textDecoration := none,

      &.after (
        transformOrigin := "0 50%",
        transform := "scaleX(1)"
      )
    )
  )

  val centerBlock: CssStyle = style(
    display.block,
    textAlign.center,
    margin(`0`, auto)
  )
}

