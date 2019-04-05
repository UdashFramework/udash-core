package io.udash.web.commons.styles.components

import io.udash.css.{CssBase, CssStyle}
import io.udash.web.commons.styles.utils.{CommonStyleUtils, MediaQueries, StyleConstants}

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

trait HeaderButtonsStyles extends CssBase {
  import dsl._

  val headerRight: CssStyle = style(
    position.relative,
    float.right,
    height(100 %%)
  )

  val headerSocial: CssStyle = style(
    CommonStyleUtils.relativeMiddle
  )

  val headerSocialItem: CssStyle = style(
    display.inlineBlock,
    marginLeft(1.25 rem)
  )

  private val socialLink = mixin(
    position.relative,
    display.block,
    width(33 px),

    unsafeChild("svg") (
      CommonStyleUtils.transition()
    ),

    MediaQueries.tabletPortrait(
      width(25 px)
    )
  )

  val tooltip: CssStyle = style(
    CommonStyleUtils.transition(duration = 150 milliseconds),
    position.absolute,
    top :=! "calc(100% + 10px)",
    right(`0`),
    fontSize(.75 rem),
    color.black,
    textAlign.center,
    visibility.hidden,
    opacity(0),
    pointerEvents := none,

    MediaQueries.tabletLandscape(
      display.none
    )
  )

  val tooltipTop: CssStyle = style(
    CommonStyleUtils.transition(duration = 350 milliseconds),
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

  val tooltipText: CssStyle = style(
    position.relative,
    width(100 %%),
    overflow.hidden
  )

  val tooltipTextInner: CssStyle = style(
    CommonStyleUtils.transition(duration = 200 milliseconds),
    position.relative,
    width(100 %%),
    padding(10 px, 15 px),
    color.white,
    backgroundColor(StyleConstants.Colors.RedLight),
    whiteSpace.nowrap,
    transform := "translateY(-100%)"
  )

  val headerSocialLink: CssStyle = style(
    socialLink,

    unsafeChild("svg") (
      svgFill := c"#fff"
    ),

    MediaQueries.desktop(
      &.hover (
        unsafeChild("svg") (
          svgFill := StyleConstants.Colors.Red
        )
      )
    )
  )

  val headerSocialLinkYellow: CssStyle = style(
    socialLink,

    unsafeChild("svg") (
      svgFill := StyleConstants.Colors.Yellow
    ),

    MediaQueries.desktop(
      &.hover (
        unsafeChild(s".${tooltip.className}")(
          visibility.visible,
          opacity(1)
        ),

        unsafeChild(s".${tooltipTop.className}")(
          transitionDelay(0 milliseconds),
          transform := "scaleX(1)"
        ),

        unsafeChild(s".${tooltipTextInner.className}")(
          transitionDelay(350 milliseconds),
          transform := "translateY(0)"
        )
      )
    )
  )
}
