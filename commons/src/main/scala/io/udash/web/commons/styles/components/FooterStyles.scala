package io.udash.web.commons.styles.components

import io.udash.web.commons.styles.GlobalStyles
import io.udash.web.commons.styles.utils._

import scala.language.postfixOps
import scalacss.Defaults._

/**
  * Created by malchik on 2016-04-04.
  */
object FooterStyles extends StyleSheet.Inline {
  import dsl._

  val footer = style(
    backgroundColor.black,
    height(StyleConstants.Sizes.FooterHeight px),
    fontSize(1.2 rem),
    color.white,

    MediaQueries.phone(
      style(
        height.auto,
        padding(2 rem, `0`)
      )
    )
  )

  val footerInner = style(
    StyleUtils.relativeMiddle,

    MediaQueries.phone(
      style(
        top.auto,
        transform := "none"
      )
    )
  )

  val footerLogo = style(
    display.inlineBlock,
    verticalAlign.middle,
    width(50 px),
    marginRight(25 px)
  )

  val footerLinks = style(
    display.inlineBlock,
    verticalAlign.middle
  )

  val footerMore = style(
    UdashFonts.acumin(FontWeight.SemiBold),
    marginBottom(1.5 rem),
    fontSize(2.2 rem)
  )

  val footerCopyrights = style(
    position.absolute,
    right(`0`),
    bottom(`0`),
    fontSize.inherit,

    MediaQueries.tabletPortrait(
      style(
        position.relative,
        textAlign.right
      )
    )
  )

  val footerAvsystemLink = style(
    StyleUtils.transition(),
    color.inherit,
    textDecoration := "underline",

    &.hover (
      color(StyleConstants.Colors.Yellow)
    ),

    &.visited (
      color.inherit,

      &.hover (
        color(StyleConstants.Colors.Yellow)
      )
    )
  )

  val navItem = style(
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
}
