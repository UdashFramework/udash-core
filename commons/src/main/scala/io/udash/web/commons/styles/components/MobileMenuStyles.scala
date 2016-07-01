package io.udash.web.commons.styles.components

/**
  * Created by malchik on 2016-07-01.
  */
import io.udash.web.commons.styles.UdashStylesheet
import io.udash.web.commons.styles.attributes.Attributes
import io.udash.web.commons.styles.utils.{MediaQueries, StyleConstants, StyleUtils}

import scalacss.Compose
import scalacss.Defaults._
import language.postfixOps

object MobileMenuStyles extends UdashStylesheet{
  import dsl._

  private val lineHeight = 4
  val btnMobileLines = style(
    StyleUtils.absoluteCenter,
    width(60 %%)
  )

  val btnMobileLine = style(
    StyleUtils.transition(),
    position.relative,
    display.block,
    height(lineHeight px),
    width(100 %%),
    marginTop(lineHeight px),
    marginBottom(lineHeight px),
    backgroundColor(StyleConstants.Colors.Red)
  )

  val btnMobileLineTop = style(
    btnMobileLine,
    transformOrigin := s"50% calc(50% + ${lineHeight * 2}px)"
  )

  val btnMobileLineMiddle = style(
    btnMobileLine
  )

  val btnMobileLineBottom = style(
    btnMobileLine,
    transformOrigin := s"50% calc(50% - ${lineHeight * 2}px)"
  )

  private val btnMobileActive = style(
    unsafeChild(s".${btnMobileLineTop.htmlClass}") (
      transform := s"rotate(45deg) translateY(${lineHeight * 2}px)"
    ),

    unsafeChild(s".${btnMobileLineBottom.htmlClass}") (
      transform := s"rotate(-45deg) translateY(-${lineHeight * 2}px)"
    ),

    unsafeChild(s".${btnMobileLineMiddle.htmlClass}") (
      opacity(0)
    )
  )

  val btnMobile = style(
    display.none,
    width(StyleConstants.Sizes.GuideHeaderHeightMobile + 1 px),
    height(StyleConstants.Sizes.GuideHeaderHeightMobile  px),
    zIndex(9),

    MediaQueries.tabletPortrait(
      style(
        display.inlineBlock,
        verticalAlign.middle
      )
    ),

    &.attr(Attributes.data(Attributes.Active), "true") (
      btnMobileActive
    )
  )(Compose.trust)
}
