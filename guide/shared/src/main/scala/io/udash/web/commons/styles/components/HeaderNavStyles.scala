package io.udash.web.commons.styles.components

import io.udash.css.{CssBase, CssStyle}
import io.udash.web.commons.styles.attributes.Attributes
import io.udash.web.commons.styles.utils.{CommonStyleUtils, MediaQueries}

import scala.language.postfixOps

trait HeaderNavStyles extends CssBase {
  import dsl._

  val headerNav: CssStyle = style(
    CommonStyleUtils.relativeMiddle,
    display.inlineBlock,
    verticalAlign.top,
    color.white,

    MediaQueries.tabletPortrait(
      CommonStyleUtils.transition(),
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
    )
  )

  val headerLinkList: CssStyle = style(
    MediaQueries.tabletPortrait(
      CommonStyleUtils.center,
      position.absolute
    )
  )

  val headerLinkWrapper: CssStyle = style(
    position.relative,
    display.inlineBlock,
    verticalAlign.middle,
    paddingLeft(1.125 rem),
    paddingRight(1.125 rem),

    &.firstChild (
      paddingLeft(0 px)
    ),

    &.lastChild (
      paddingRight(0 px)
    ),

    &.before.not(_.firstChild)(
      CommonStyleUtils.absoluteMiddle,
      content := "\"|\"",
      left(`0`),

      &.hover(
        textDecoration := none
      ),

      MediaQueries.tabletPortrait(
        content := "\"\""
      )
    ),

    MediaQueries.tabletPortrait(
      display.block,
      padding(.625 rem, `0`),
      textAlign.center
    )
  )

  val headerLink: CssStyle = style(
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
      textDecoration := none
    ),

    MediaQueries.desktop(
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
}
