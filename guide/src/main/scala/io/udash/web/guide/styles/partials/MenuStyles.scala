package io.udash.web.guide.styles.partials

import java.util.concurrent.TimeUnit

import io.udash.web.commons.styles.utils.StyleConstants
import io.udash.web.guide.components.GuideMenu
import io.udash.web.guide.styles.fonts.{FontWeight, UdashFonts}
import io.udash.web.guide.styles.utils.{MediaQueries, StyleUtils}

import scala.concurrent.duration.FiniteDuration
import scala.language.postfixOps
import scalacss.Compose
import scalacss.Defaults._

object MenuStyles extends StyleSheet.Inline {
  import dsl._

  val guideMenu = style(
    UdashFonts.acumin(FontWeight.SemiBold),
    StyleUtils.border(),
    width(100 %%),

    MediaQueries.phone(
      style(
        width(320 - StyleConstants.Sizes.MobileMenuButton px),
        height(100 %%),
        overflowY.auto
      )
    )
  )

  private val menuLink = style(
    StyleUtils.transition(),
    position.relative,
    display.block,
    width(100 %%),
    padding(2 rem, `0`, 2 rem, 4.5 rem),
    fontSize(2.2 rem),
    color.black,
    textAlign.left,

    &.visited (
      color.black
    ),

    &.hover (
      color.black,
      textDecoration := "none"
    ),

    MediaQueries.tabletLandscape(
      style(
        fontSize(1.8 rem)
      )
    )
  )

  val link = style(
    menuLink,

    &.attr(GuideMenu.DataActiveAttribute, "true") (
      color(StyleConstants.Colors.Red),

      unsafeChild(s".${linkText.htmlClass}") (
        linkTextHover,
        &.after (
          borderColor(StyleConstants.Colors.Red)
        )
      )
    )
  )

  val subToggle = style(
    menuLink,

    &.attr(GuideMenu.DataActiveAttribute, "true") (
      unsafeChild(s".${icon.htmlClass}") (
        transform := "rotate(90deg)"
      )
    )
  )

  lazy val linkText = style(
    position.relative
  )

  val item = style(
    borderBottomColor(StyleConstants.Colors.GreyExtra),
    borderBottomStyle.solid,
    borderBottomWidth(1 px),

    &.lastChild (
      border.none
    )
  )

  val subItem = style(
    borderBottomColor(StyleConstants.Colors.GreyExtra),
    borderBottomStyle.solid,
    borderBottomWidth(1 px),

    &.lastChild (
      border.none
    )
  )

  lazy val icon = style(
    StyleUtils.transition(new FiniteDuration(100, TimeUnit.MILLISECONDS)),
    position.absolute,
    display.block,
    width(7 px),
    top(50 %%),
    left(2.5 rem),
    transform := "translateX(-50%) translateY(-50%)",
    transformOrigin := "50% 25%"
  )

  val subList = style(
    display.none,
    paddingLeft(5 rem),
    paddingBottom(1.5 rem),

    unsafeChild(s".${item.htmlClass}") (
      border.none.important,
      paddingTop(1 rem),
      paddingBottom(1 rem)
    ),

    unsafeChild(s".${link.htmlClass}") (
      UdashFonts.acumin(FontWeight.Bold),
      padding(`0`, `0`, `0`, 1.5 rem),
      fontSize(1.6 rem),

      &.before(
        position.absolute,
        left(`0`),
        top(`0`),
        content := "\"•\""
      ),

      &.hover(
        unsafeChild(s".${linkText.htmlClass}") (
          linkTextHover
        )
      ),

      MediaQueries.tabletLandscape(
        style(
          fontSize(1.4 rem)
        )
      )
    ),

    unsafeChild(s".${linkText.htmlClass}") (
      &.after(
        StyleUtils.transition(transform, new FiniteDuration(250, TimeUnit.MILLISECONDS)),
        position.absolute,
        top(100 %%),
        left(`0`),
        content := "\" \"",
        width(100 %%),
        borderBottomColor.black,
        borderBottomWidth(1 px),
        borderBottomStyle.solid,
        transform := "scaleX(0)",
        transformOrigin := "100% 50%"
      )
    )
  )

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
    StyleUtils.border(),
    display.none,
    borderLeft.none,
    position.absolute,
    top(`0`),
    left :=! "calc(100% - 1px)",
    width(StyleConstants.Sizes.MobileMenuButton + 1 px),
    height(StyleConstants.Sizes.MobileMenuButton px),
    backgroundColor.white,

    unsafeRoot(s".${GuideStyles.menuWrapper.htmlClass}")(
      &.attr(GuideMenu.DataActiveAttribute, "true") (
        btnMobileActive
      )
    ),

    MediaQueries.tabletLandscape(
      style(
        display.block
      )
    )

  )(Compose.trust)

  private lazy val linkTextHover = style(
    &.after (
      transformOrigin := "0 50%",
      transform := "scaleX(1)"
    )
  )
}
