package io.udash.web.homepage.styles.partials

import java.util.concurrent.TimeUnit

import io.udash.web.commons.styles.components.HeaderButtonsStyles
import io.udash.web.homepage.components.Header
import io.udash.web.commons.styles.utils.{MediaQueries, StyleConstants, StyleUtils}

import scala.concurrent.duration.FiniteDuration
import scala.language.postfixOps
import scalacss.Defaults._

object HeaderStyles extends StyleSheet.Inline with HeaderButtonsStyles {
  import dsl._

  val header = style(
    position.absolute,
    top(`0`),
    left(`0`),
    width(100 %%),
    height(StyleConstants.Sizes.LandingPageHeaderHeight px),
    fontSize(1.6 rem),
    zIndex(999),

    &.attr(Header.PinAttribute, "true")(
      position.fixed,
      height(StyleConstants.Sizes.HeaderHeightPin px),
      backgroundColor.black,
      animationName(headerAnimation),
      animationIterationCount.count(1),
      animationDuration(new FiniteDuration(300, TimeUnit.MILLISECONDS)),

      MediaQueries.phone(
        style(
          height(StyleConstants.Sizes.HeaderHeightPin * .85 px)
        )
      ),

      unsafeChild(s".${headerLogo.htmlClass}")(
        width(130 px),
        height(56 px),
        backgroundImage := "url(assets/images/udash_logo_m.png)",

        MediaQueries.tabletPortrait(
          style(
            width(130 * .8 px),
            height(56 * .8 px)
          )
        )
      )
    ),

    MediaQueries.tabletPortrait(
      style(
        height(StyleConstants.Sizes.HeaderHeight * .7 px)
      )
    )
  )

  lazy val headerAnimation = keyframes(
    (0 %%) -> style(
      transform := "translateY(-100%)"
    ),

    (100 %%) -> style(
      transform := "translateY(0)"
    )
  )

  val headerLeft = style(
    position.relative,
    float.left,
    height(100 %%)
  )

  val headerNav = style(
    StyleUtils.relativeMiddle,
    display.inlineBlock,
    verticalAlign.top,
    color.white
  )

  lazy val headerLogo = style(
    StyleUtils.relativeMiddle,
    display.inlineBlock,
    verticalAlign.top,
    width(65 px),
    height(96 px),
    marginRight(25 px),
    backgroundImage := "url(assets/images/udash_logo_l.png)",
    backgroundRepeat := "no-repeat",
    backgroundSize := "100%",

    MediaQueries.tabletPortrait(
      style(
        width(65 * .7 px),
        height(96 * .7 px)
      )
    )
  )

  val headerLinkWrapper = style(
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

  val headerLink = style(
    position.relative,
    display.block,
    color.white,

    &.after(
      StyleUtils.transition(transform, new FiniteDuration(250, TimeUnit.MILLISECONDS)),
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
      textDecoration := "none"
    ),

    MediaQueries.desktop(
      style(
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
  )
}
