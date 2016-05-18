package io.udash.web.commons.styles.components

import java.util.concurrent.TimeUnit

import io.udash.web.commons.styles.utils.{MediaQueries, StyleConstants, StyleUtils}

import scala.concurrent.duration.{DurationInt, FiniteDuration}
import scala.language.postfixOps
import scalacss.Defaults._

trait HeaderButtonsStyles extends StyleSheet.Inline {
  import dsl._

  val headerRight: StyleA = style(
    position.relative,
    float.right,
    height(100 %%)
  )

  val headerSocial: StyleA = style(
    StyleUtils.relativeMiddle
  )

  val headerSocialItem: StyleA = style(
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

  val tooltip: StyleA = style(
    StyleUtils.transition(150 milliseconds),
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

  val tooltipTop: StyleA = style(
    StyleUtils.transition(350 milliseconds),
    transitionDelay(200 milliseconds),
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

  val tooltipText: StyleA = style(
    position.relative,
    width(100 %%),
    overflow.hidden
  )

  val tooltipTextInner: StyleA = style(
    StyleUtils.transition(200 milliseconds),
    position.relative,
    width(100 %%),
    padding(10 px, 15 px),
    color.white,
    backgroundColor(StyleConstants.Colors.RedLight),
    whiteSpace.nowrap,
    transform := "translateY(-100%)"
  )

  val headerSocialLink: StyleA = style(
    socialLink,

    unsafeChild("svg") (
      svgFill := c"#fff"
    ),

    MediaQueries.desktop(
      style(
        &.hover (
          unsafeChild("svg") (
            svgFill := StyleConstants.Colors.Red
          )
        )
      )
    )
  )

  val headerSocialLinkYellow: StyleA = style(
    socialLink,

    unsafeChild("svg") (
      svgFill := StyleConstants.Colors.Yellow
    ),

    MediaQueries.desktop(
      style(
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
    )
  )
}
