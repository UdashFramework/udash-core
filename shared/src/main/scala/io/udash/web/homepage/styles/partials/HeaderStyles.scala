package io.udash.web.homepage.styles.partials

import java.util.concurrent.TimeUnit

import io.udash.css.CssBase
import io.udash.web.commons.styles.attributes.Attributes
import io.udash.web.commons.styles.components.{HeaderButtonsStyles, HeaderNavStyles}
import io.udash.web.commons.styles.utils.{MediaQueries, StyleConstants, CommonStyleUtils}

import scala.concurrent.duration.FiniteDuration
import scala.language.postfixOps

object HeaderStyles extends CssBase with HeaderButtonsStyles with HeaderNavStyles {
  import dsl._

  val header = style(
    position.absolute,
    top(`0`),
    left(`0`),
    width(100 %%),
    height(StyleConstants.Sizes.LandingPageHeaderHeight px),
    fontSize(1.6 rem),
    zIndex(999),

    &.attr(Attributes.data(Attributes.Pinned), "true")(
      position.fixed,
      height(StyleConstants.Sizes.HeaderHeightPin px),
      backgroundColor.black,
      animationName(headerAnimation),
      animationIterationCount.count(1),
      animationDuration(new FiniteDuration(300, TimeUnit.MILLISECONDS)),

      MediaQueries.tabletLandscape(
        height(StyleConstants.Sizes.HeaderHeightPin * .85 px)
      ),

      unsafeChild(s".${headerLogo.className}")(
        width(48 px),
        height(56 px),
        backgroundImage := "url(/assets/images/udash_logo.png)",

        MediaQueries.tabletPortrait(
          display.none
        )
      ),

      unsafeChild(s".${btnMobile.className}")(
        CommonStyleUtils.middle
      )
    ),

    MediaQueries.tabletPortrait(
      height(StyleConstants.Sizes.HeaderHeight * .9 px)
    )
  )

  lazy val headerAnimation = keyframes(
    0d -> keyframe(
      transform := "translateY(-100%)"
    ),

    100d -> keyframe(
      transform := "translateY(0)"
    )
  )

  val headerLeft = style(
    position.relative,
    float.left,
    height(100 %%)
  )

  lazy val headerLogo = style(
    CommonStyleUtils.relativeMiddle,
    display.inlineBlock,
    verticalAlign.middle,
    width(65 px),
    height(96 px),
    marginRight(25 px),
    backgroundImage := "url(/assets/images/udash_logo_l.png)",
    backgroundRepeat.noRepeat,
    backgroundSize := "100%",

    MediaQueries.tabletPortrait(
      display.block,
      width(StyleConstants.Sizes.GuideHeaderHeightMobile px),
      height(14 px),
      backgroundPosition := "bottom",
      transform := "none",
      top.auto
    )
  )

  lazy val btnMobile = style(
    position.relative
  )
}
