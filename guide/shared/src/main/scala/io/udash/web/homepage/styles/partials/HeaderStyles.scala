package io.udash.web.homepage.styles.partials

import io.udash.css.{CssBase, CssStyle}
import io.udash.web.commons.styles.attributes.Attributes
import io.udash.web.commons.styles.components.{HeaderButtonsStyles, HeaderNavStyles}
import io.udash.web.commons.styles.utils.{CommonStyleUtils, MediaQueries, StyleConstants}
import scalacss.internal.Literal

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

object HeaderStyles extends CssBase with HeaderButtonsStyles with HeaderNavStyles {
  import dsl._

  val header: CssStyle = style(
    position.absolute,
    top(`0`),
    left(`0`),
    width(100 %%),
    height(StyleConstants.Sizes.LandingPageHeaderHeight px),
    fontSize(1 rem),
    zIndex(999),

    &.attr(Attributes.data(Attributes.Pinned), "true")(
      position.fixed,
      height(StyleConstants.Sizes.HeaderHeightPin px),
      backgroundColor.black,
      animationName(headerAnimation),
      animationIterationCount.count(1),
      animationDuration(300 milliseconds),

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

  private lazy val headerAnimation: CssStyle = keyframes(
    0d -> keyframe(
      transform := "translateY(-100%)"
    ),

    100d -> keyframe(
      transform := "translateY(0)"
    )
  )

  val headerLeft: CssStyle = style(
    position.relative,
    float.left,
    height(100 %%)
  )

  lazy val headerLogo: CssStyle = style(
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
      backgroundPosition := Literal.bottom,
      transform := none,
      top.auto
    )
  )

  lazy val btnMobile: CssStyle = style(
    position.relative
  )
}
