package io.udash.homepage.styles.partials


import io.udash.homepage.styles.constant.StyleConstants
import io.udash.homepage.styles.fonts.{FontWeight, UdashFonts}
import io.udash.homepage.styles.utils.{MediaQueries, StyleUtils}

import scala.language.postfixOps
import scalacss.Defaults._

object FooterStyles extends StyleSheet.Inline {
  import dsl._

  val footer = style(
    backgroundColor.black,
    fontSize(1.2 rem),
    padding(4 rem, `0`),
    color.white,

    MediaQueries.tabletPortrait(
      style(
        padding(2 rem, `0`)
      )
    )
  )

  val footerLogo = style(
    display.inlineBlock,
    verticalAlign.middle,
    width(48 px),
    height(56 px),
    marginRight(25 px),
    backgroundImage := "url(assets/images/udash_logo.png)",
    backgroundRepeat := "no-repeat",
    backgroundSize := "100%",

    MediaQueries.phone(
      style(
        width(48 * .8 px),
        height(56 * .8 px),
        marginBottom(10 px)
      )
    )
  )

  val footerLinks = style(
    display.inlineBlock,
    verticalAlign.middle
  )

  val footerLinkWrapper = style(
    HeaderStyles.headerLinkWrapper
  )

  val footerLink = style(
    HeaderStyles.headerLink
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
        marginTop(5 px)
      )
    )
  )

  val footerAvsystem = style(
    StyleUtils.transition(),
    color.inherit,
    textDecoration := "underline",

    MediaQueries.desktop(
      style(
        &.hover (
          color(StyleConstants.Colors.Yellow)
        )
      )
    ),

    &.visited (
      color.inherit,

      &.hover (
        color(StyleConstants.Colors.Yellow)
      )
    )
  )
}
