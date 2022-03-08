package io.udash.web.guide.styles.partials

import io.udash.css.{CssBase, CssStyle}
import io.udash.web.commons.styles.GlobalStyles
import io.udash.web.commons.styles.attributes.Attributes
import io.udash.web.commons.styles.components.CodeBlockStyles
import io.udash.web.commons.styles.utils.{FontWeight, StyleConstants, UdashFonts}
import io.udash.web.guide.styles.utils.{GuideStyleUtils, MediaQueries}
import scalacss.internal.Literal

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

object GuideStyles extends CssBase with CodeBlockStyles {
  import dsl._

  val main: CssStyle = style(
    paddingTop(50 px),
    position.relative,
    minHeight :=! s"calc(100vh - ${StyleConstants.Sizes.HeaderHeight}px - ${StyleConstants.Sizes.FooterHeight}px)"
  )

  val floatRight: CssStyle = style(
    float.right
  )

  val imgIntro: CssStyle = style(
    MediaQueries.phone(
      float.none,
      width(100 %%),
      maxWidth.none,
      maxHeight.none,
      margin(1.25 rem, `0`)
    )
  )

  private val highlightRedKeyframes: CssStyle = keyframes(
    0d -> keyframe(
      color.black
    ),
    50d -> keyframe(
      color.red
    ),
    100d -> keyframe(
      color.black
    )
  )

  val highlightRed: CssStyle = style(
    animationName(highlightRedKeyframes),
    animationIterationCount.count(1),
    animationDuration(2 seconds)
  )

  val menuWrapper: CssStyle = style(
    GlobalStyles.col,
    GuideStyleUtils.transition(),
    width(StyleConstants.Sizes.MenuWidth px),
    paddingTop(4.375 rem),
    borderBottomColor(StyleConstants.Colors.GreyExtra),
    borderBottomWidth(1 px),
    borderBottomStyle.solid,
    transform := none,

    MediaQueries.desktop(
      borderBottomWidth(`0`),
      position.sticky,
      top(50 px)
    ),

    MediaQueries.tabletLandscape(
      position.fixed,
      right(100 %%),
      top(`0`),
      height(100 %%),
      paddingTop(`0`),
      backgroundColor.white,
      zIndex(999),

      &.attr(Attributes.data(Attributes.Active), "true") {
        transform := "translateX(100%)"
      }
    ),

    MediaQueries.phone(
      width.auto
    )
  )

  val contentWrapper: CssStyle = style(
    GlobalStyles.col,
    width :=! s"calc(100% - ${StyleConstants.Sizes.MenuWidth}px)",
    paddingLeft(2.5 rem),
    paddingBottom(3.125 rem),

    unsafeChild("a")(
      &.not(".badge")(
        &.not(".nav-link")(
          &.not(".dropdown-item")(
            color(StyleConstants.Colors.Red),

            &.hover(
              color(StyleConstants.Colors.Red)
            ),

            &.visited(
              color(StyleConstants.Colors.Red)
            ),
          )
        )
      )
    ),

    MediaQueries.tabletLandscape(
      width(100 %%),
      paddingLeft(`0`)
    )
  )

  private val liStyle: CssStyle = mixin(
    position.relative,
    paddingLeft(1.25 rem),
    margin(.3125 rem, `0`,.3125 rem, 2.8125 rem),

    MediaQueries.phone(
      marginLeft(.9375 rem)
    )
  )

  private val liBulletStyle: CssStyle = mixin(
    position.absolute,
    left(`0`),
    top(`0`)
  )

  val defaultList: CssStyle = style(
    unsafeChild("li") (
      liStyle,

      &.before(
        liBulletStyle,
        content.string("•"),
      )
    )
  )

  val innerList: CssStyle = style(
    unsafeChild("li") (
      liStyle,

      &.before(
        liBulletStyle,
        content.string("‣"),
      )
    )
  )

  val codeWrapper: CssStyle = style(
    marginTop(.9375 rem),
    marginBottom(.9375 rem),
    paddingTop(.625 rem),
    paddingBottom(.625 rem)
  )

  val codeBlock: CssStyle = style(
    counterReset := "code",
    listStyleType := "decimal",
    listStylePosition.outside,
    fontFamily :=! "Consolas, Monaco, 'Andale Mono', 'Ubuntu Mono', monospace",
    fontSize(1 rem),
    color(StyleConstants.Colors.GreySemi),
    paddingLeft(3.75 rem),
    marginBottom(`0`),

    unsafeChild(":not(pre) > code[class*=\"language-\"]") (
      whiteSpace.pre
    )
  )

  val frame: CssStyle = style(
    GuideStyleUtils.border(),
    display.block,
    padding(.9375 rem),
    margin(1.25 rem, `0`)
  )

  val imgSmall: CssStyle = style(
    display.table,
    GuideStyleUtils.border(),
    maxWidth(40 %%),
    maxHeight(200 px),
    padding(.9375 rem),
    margin(`0`, 1.25 rem, 1.25 rem, 1.25 rem)
  )

  val imgMedium: CssStyle = style(
    display.table,
    GuideStyleUtils.border(),
    maxWidth(70 %%),
    maxHeight(350 px),
    padding(.9375 rem),
    margin(`0`, 1.25 rem, 1.25 rem, 1.25 rem)
  )

  val imgBig: CssStyle = style(
    display.table,
    maxWidth(100 %%),
    maxHeight(750 px)
  )

  val useBootstrap: CssStyle = style(
    addClassName("bootstrap")
  )

  val sectionError: CssStyle = style(
    position.relative,
    width(100 %%),
    overflow.hidden,
    height :=! s"calc(100vh - 200px)",
    color.white,
    backgroundColor.black,
    backgroundImage := "url(/assets/images/intro_bg.jpg)",
    backgroundSize := Literal.cover,

    media.minHeight(1 px).maxHeight(StyleConstants.Sizes.MinSiteHeight - 1 px)(
      height.auto,
      paddingTop(80 px),
      paddingBottom(100 px)
    ),

    MediaQueries.tabletLandscape(
      height.auto,
      paddingTop(80 px),
      paddingBottom(100 px)
    )
  )

  val errorInner: CssStyle = style(
    GuideStyleUtils.relativeMiddle,
    top(50 %%),
    transform := "translate3d(0, 0, 1)",

    media.minHeight(1 px).maxHeight(650 px)(
      top(60 %%)
    ),

    media.minHeight(1 px).maxHeight(StyleConstants.Sizes.MinSiteHeight - 1 px)(
      top(auto),
      transform := "translateY(0)"
    ),

    MediaQueries.tabletLandscape(
      top(auto),
      transform := "translateY(0)"
    )
  )

  val errorHead: CssStyle = style(
    UdashFonts.roboto(FontWeight.Bold),
    fontSize(5.5 rem),
    lineHeight(1.1),
    transform := "translate3d(0, 0, 1)",
    textShadow := "0 0 15px black",

    &.after(
      content.string(" "),
      position.absolute,
      bottom(-110 px),
      right(-30 px),
      width(400 px),
      height(213 px),
      backgroundImage := "url(/assets/images/intro_bird.png)",
      backgroundSize := "100%",

      media.minHeight(1 px).maxHeight(850 px)(
        width(400 * .7 px),
        height(213 * .7 px),
        bottom(-80 px)
      ),

      MediaQueries.tabletLandscape(
        width(400 * .7 px),
        height(213 * .7 px),
        bottom(-80 px)
      ),

      MediaQueries.tabletLandscape(
        display.none
      )
    ),

    media.minHeight(StyleConstants.Sizes.MinSiteHeight px).maxHeight(850 px)(
      marginTop(1.25 rem),
      marginBottom(1.875 rem)
    ),

    media.minHeight(751 px).maxHeight(850 px)(
      marginTop(1.25 rem),
      marginBottom(1.875 rem),
      fontSize(5 rem)
    ),

    media.minHeight(651 px).maxHeight(750 px)(
      fontSize(3.75 rem)
    ),

    media.minHeight(StyleConstants.Sizes.MinSiteHeight px).maxHeight(650 px)(
      fontSize(3.125 rem)
    ),

    media.minHeight(1 px).maxHeight(StyleConstants.Sizes.MinSiteHeight - 1 px)(
      fontSize(3.75 rem),
      marginTop(2.5 rem),
      marginBottom(3.75 rem)
    ),

    MediaQueries.tabletLandscape(
      fontSize(5 rem).important
    ),

    MediaQueries.tabletLandscape(
      marginTop(1.875 rem).important,
      marginBottom(3.75 rem).important,
      fontSize(3.75 rem).important
    ),

    MediaQueries.phone(
      fontSize(2.5 rem).important,
      lineHeight(1.2)
    )
  )
}
