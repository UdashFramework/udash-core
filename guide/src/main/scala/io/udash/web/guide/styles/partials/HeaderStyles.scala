package io.udash.web.guide.styles.partials

import java.util.concurrent.TimeUnit

import io.udash.web.guide.styles.constant.StyleConstants
import io.udash.web.guide.styles.utils.{MediaQueries, StyleUtils}

import scala.concurrent.duration.FiniteDuration
import scala.language.postfixOps
import scalacss.Defaults._

/**
  * Created by malchik on 2016-04-04.
  */
object HeaderStyles extends StyleSheet.Inline {
  import dsl._

  val header = style(
    position.relative,
    backgroundColor.black,
    height(StyleConstants.Sizes.HeaderHeight px),
    fontSize(1.6 rem),
    zIndex(99),

    MediaQueries.tabletPortrait(
      style(
        height(StyleConstants.Sizes.HeaderHeight * .7 px)
      )
    )
  )

  val headerLeft = style(
    position.relative,
    float.left,
    height(100 %%)
  )

  val headerRight = style(
    position.relative,
    float.right,
    height(100 %%)
  )

  val headerLogo = style(
    StyleUtils.relativeMiddle,
    display.inlineBlock,
    verticalAlign.top,
    width(130 px),
    marginRight(25 px),

    MediaQueries.tabletLandscape(
      style(
        marginLeft(StyleConstants.Sizes.MobileMenuButton px)
      )
    ),

    MediaQueries.tabletPortrait(
      style(
        width(130 * .8 px)
      )
    )
  )

  val headerNav = style(
    StyleUtils.relativeMiddle,
    display.inlineBlock,
    verticalAlign.top,
    color.white
  )

  val headerSocial = style(
    StyleUtils.relativeMiddle
  )

  val headerSocialItem = style(
    display.inlineBlock,
    marginLeft(2 rem)
  )

  private val socialLink = style(
    position.relative,
    display.block,
    width(33 px),

    unsafeChild("svg") (
      StyleUtils.transition()
    ),

    MediaQueries.tabletPortrait(
      style(
        width(25 px)
      )
    )
  )

  val headerSocialLink = style(
    socialLink,

    unsafeChild("svg") (
      svgFill := c"#fff"
    ),

    &.hover (
      unsafeChild("svg") (
        svgFill := StyleConstants.Colors.Red
      )
    )
  )

  val headerSocialLinkYellow = style(
    socialLink,

    unsafeChild("svg") (
      svgFill := StyleConstants.Colors.Yellow
    ),

    &.hover (
      unsafeChild(s".${tooltip.htmlClass}")(
        visibility.visible,
        opacity(1)
      ),

      unsafeChild(s".${tooltipTop.htmlClass}")(
        transitionDelay(new FiniteDuration(0, TimeUnit.MILLISECONDS)),
        transform := "scaleX(1)"
      ),

      unsafeChild(s".${tooltipTextInner.htmlClass}")(
        transitionDelay(new FiniteDuration(350, TimeUnit.MILLISECONDS)),
        transform := "translateY(0)"
      )
    )
  )

  lazy val tooltip = style(
    StyleUtils.transition(new FiniteDuration(150, TimeUnit.MILLISECONDS)),
    position.absolute,
    top :=! "calc(100% + 10px)",
    right(`0`),
    fontSize(1.2 rem),
    color.black,
    textAlign.center,
    visibility.hidden,
    opacity(0),
    pointerEvents := "none",

    MediaQueries.tabletLandscape(
      style(
        display.none
      )
    )
  )

  lazy val tooltipTop = style(
    StyleUtils.transition(new FiniteDuration(350, TimeUnit.MILLISECONDS)),
    transitionDelay(new FiniteDuration(200, TimeUnit.MILLISECONDS)),
    position.relative,
    width(100 %%),
    backgroundColor(StyleConstants.Colors.Red),
    height(4 px),
    transformOrigin := "calc(100% - 9px) 0",
    transform := "scaleX(.2)",
    zIndex(9),

    &.after(
      content := "\" \"",
      position.absolute,
      bottom :=! "calc(100% - 1px)",
      right(9 px),
      marginLeft(-6 px),
      width(`0`),
      height(`0`),
      borderBottomWidth(6 px),
      borderBottomStyle.solid,
      borderBottomColor(StyleConstants.Colors.Red),
      borderRightWidth(6 px),
      borderRightStyle.solid,
      borderRightColor.transparent,
      borderLeftWidth(6 px),
      borderLeftStyle.solid,
      borderLeftColor.transparent
    )
  )

  val tooltipText = style(
    position.relative,
    width(100 %%),
    overflow.hidden
  )

  lazy val tooltipTextInner = style(
    StyleUtils.transition(new FiniteDuration(200, TimeUnit.MILLISECONDS)),
    position.relative,
    width(100 %%),
    padding(10 px, 15 px),
    color.white,
    backgroundColor(StyleConstants.Colors.RedLight),
    whiteSpace.nowrap,
    transform := "translateY(-100%)"
  )
}
