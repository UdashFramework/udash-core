package io.udash.web.commons.styles.components

import java.util.concurrent.TimeUnit

import io.udash.web.commons.styles.attributes.Attributes
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
    color.white,

    MediaQueries.tabletPortrait(style(
      StyleUtils.transition(),
      position.fixed,
      left(`0`),
      top(`0`),
      width(100 %%),
      height(100 %%),
      backgroundColor(c"rgba(0,0,0,.9)"),
      transform := "translateX(-100%)",

      &.attr(Attributes.data(Attributes.Active), "true") (
        transform := "translateX(0)"
      )
    ))
  )

  val headerLinkList = style(
    MediaQueries.tabletPortrait(style(
      StyleUtils.center,
      position.absolute
    ))
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
      ),

      MediaQueries.tabletPortrait(style(
        content := "\"\""
      ))
    ),

    MediaQueries.tabletPortrait(style(
      display.block,
      padding(1 rem, `0`),
      textAlign.center
    ))
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
