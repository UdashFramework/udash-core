package io.udash.web.commons.styles.components

/**
  * Created by malchik on 2016-07-01.
  */

import io.udash.css.{CssBase, CssStyle}
import io.udash.web.commons.styles.attributes.Attributes
import io.udash.web.commons.styles.utils.{CommonStyleUtils, MediaQueries, StyleConstants}

import scala.language.postfixOps

object MobileMenuStyles extends CssBase {
  import dsl._

  private val lineHeight = 4
  val btnMobileLines: CssStyle = style(
    CommonStyleUtils.absoluteCenter,
    width(60 %%)
  )

  private val btnMobileLine: CssStyle = mixin(
    CommonStyleUtils.transition(),
    position.relative,
    display.block,
    height(lineHeight px),
    width(100 %%),
    marginTop(lineHeight px),
    marginBottom(lineHeight px),
    backgroundColor(StyleConstants.Colors.Red)
  )

  val btnMobileLineTop: CssStyle = style(
    btnMobileLine,
    transformOrigin := s"50% calc(50% + ${lineHeight * 2}px)"
  )

  val btnMobileLineMiddle: CssStyle = style(
    btnMobileLine
  )

  val btnMobileLineBottom: CssStyle = style(
    btnMobileLine,
    transformOrigin := s"50% calc(50% - ${lineHeight * 2}px)"
  )

  private val btnMobileActive = mixin(
    unsafeChild(s".${btnMobileLineTop.className}") (
      transform := s"rotate(45deg) translateY(${lineHeight * 2}px)"
    ),

    unsafeChild(s".${btnMobileLineBottom.className}") (
      transform := s"rotate(-45deg) translateY(-${lineHeight * 2}px)"
    ),

    unsafeChild(s".${btnMobileLineMiddle.className}") (
      opacity(0)
    )
  )

  val btnMobile: CssStyle = style(
    display.none,
    width(StyleConstants.Sizes.GuideHeaderHeightMobile + 1 px),
    height(StyleConstants.Sizes.GuideHeaderHeightMobile  px),
    zIndex(9),

    MediaQueries.tabletPortrait(
      display.inlineBlock,
      verticalAlign.middle
    ),

    &.attr(Attributes.data(Attributes.Active), "true") (
      btnMobileActive
    )
  )
}
