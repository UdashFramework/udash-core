package io.udash.web.commons.styles.components

import java.util.concurrent.TimeUnit

import io.udash.web.commons.styles.utils.{MediaQueries, StyleUtils}

import scala.concurrent.duration.FiniteDuration
import scala.language.postfixOps
import scalacss.Defaults._

trait HeaderNavStyles extends StyleSheet.Inline {
  import dsl._

  val headerNav: StyleA = style(
    StyleUtils.relativeMiddle,
    display.inlineBlock,
    verticalAlign.top,
    color.white
  )

  val headerLinkWrapper: StyleA = style(
    position.relative,
    display.inlineBlock,
    verticalAlign.middle,
    paddingLeft(1.8 rem),
    paddingRight(1.8 rem),

    &.firstChild (
      paddingLeft(0 px)
    ),

    &.lastChild (
      paddingRight(0 px)
    ),

    &.before.not(_.firstChild)(
      StyleUtils.absoluteMiddle,
      content := "\"|\"",
      left(`0`),

      &.hover(
        textDecoration := "none"
      )
    )
  )

  val headerLink: StyleA = style(
    position.relative,
    display.block,
    color.white,

    &.after(
      StyleUtils.transition(transform, new FiniteDuration(250, TimeUnit.MILLISECONDS)),
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
      textDecoration := "none"
    ),

    MediaQueries.desktop(
      style(
        &.hover(
          color.white,
          cursor.pointer,

          &.after (
            transformOrigin := "0 50%",
            transform := "scaleX(1)"
          )
        )
      )
    )
  )
}
