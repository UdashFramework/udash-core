package io.udash.web.commons.styles.components

import io.udash.css.{CssBase, CssStyle}
import io.udash.web.commons.styles.utils._
import scalacss.internal.Literal

import scala.language.postfixOps

/**
  * Created by malchik on 2016-04-04.
  */
object FooterStyles extends CssBase {
  import dsl._

  val footer: CssStyle = style(
    backgroundColor.black,
    height(StyleConstants.Sizes.FooterHeight px),
    fontSize(.75 rem),
    color.white,

    MediaQueries.phone(
      height.auto,
      padding(1.25 rem, `0`)
    )
  )

  val footerInner: CssStyle = style(
    CommonStyleUtils.relativeMiddle,

    MediaQueries.phone(
      top.auto,
      transform := none
    )
  )

  val footerLogo: CssStyle = style(
    display.inlineBlock,
    verticalAlign.middle,
    width(50 px),
    marginRight(25 px)
  )

  val footerLinks: CssStyle = style(
    display.inlineBlock,
    verticalAlign.middle
  )

  val footerMore: CssStyle = style(
    UdashFonts.roboto(FontWeight.Bold),
    marginBottom(.9375 rem),
    fontSize(1.375 rem)
  )

  val footerCopyrights: CssStyle = style(
    position.absolute,
    right(`0`),
    bottom(`0`),
    fontSize.inherit,

    MediaQueries.tabletPortrait(
      position.relative,
      textAlign.right
    )
  )

  val footerAvsystemLink: CssStyle = style(
    CommonStyleUtils.transition(),
    color.inherit,
    textDecoration := Literal.underline,

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

  val navItem: CssStyle = style(
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
      )
    )
  )
}
