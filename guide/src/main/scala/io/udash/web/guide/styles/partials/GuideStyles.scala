package io.udash.web.guide.styles.partials

import io.udash.web.commons.styles.GlobalStyles
import io.udash.web.commons.styles.components.CodeBlockStyles
import io.udash.web.commons.styles.utils.{FontWeight, StyleConstants, UdashFonts}
import io.udash.web.guide.components.GuideMenu
import io.udash.web.guide.styles.utils.{MediaQueries, StyleUtils}

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import scalacss.Compose
import scalacss.Defaults._

object GuideStyles extends StyleSheet.Inline with CodeBlockStyles {
  import dsl._

  val main = style(
    position.relative,
    minHeight :=! s"calc(100vh - ${StyleConstants.Sizes.HeaderHeight}px - ${StyleConstants.Sizes.FooterHeight}px)"
  )

  val imgSmall = style(
    StyleUtils.border(),
    maxWidth(40 %%),
    maxHeight(200 px),
    padding(1.5 rem),
    margin(`0`, 2 rem, 2 rem, 2 rem)
  )

  val imgMedium = style(
    StyleUtils.border(),
    maxWidth(70 %%),
    maxHeight(350 px),
    padding(1.5 rem),
    margin(`0`, 2 rem, 2 rem, 2 rem)
  )

  val imgBig = style(
    maxWidth(100 %%),
    maxHeight(750 px)
  )

  val imgLeft = style(float.left)
  val imgRight = style(
    float.right,
    MediaQueries.phone(style(
      float.none,
      width(100 %%),
      maxWidth.none,
      maxHeight.none,
      margin(2 rem, `0`)
    ))
  )

  val highlightRedKeyframes = keyframes(
    (0 %%) -> keyframe(
      color.black
    ),
    (50 %%) -> keyframe(
      color.red
    ),
    (100 %%) -> keyframe(
      color.black
    )
  )

  val highlightRed = style(
    animationName(highlightRedKeyframes),
    animationIterationCount.count(1),
    animationDuration(2 seconds)
  )

  val menuWrapper = style(
    GlobalStyles.col,
    StyleUtils.transition(),
    width(StyleConstants.Sizes.MenuWidth px),
    paddingTop(7 rem),
    borderBottomColor(StyleConstants.Colors.GreyExtra),
    borderBottomWidth(1 px),
    borderBottomStyle.solid,
    transform := "none",

    MediaQueries.tabletLandscape(
      style(
        position.fixed,
        right(100 %%),
        top(`0`),
        height(100 %%),
        paddingTop(`0`),
        backgroundColor.white,
        zIndex(999),

        &.attr(GuideMenu.DataActiveAttribute, "true") {
          transform := "translateX(100%)"
        }
      )
    ),

    MediaQueries.phone(
      style(
        width.auto
      )
    )
  )

  val contentWrapper = style(
    GlobalStyles.col,
    width :=! s"calc(100% - ${StyleConstants.Sizes.MenuWidth}px)",
    paddingLeft(4 rem),
    paddingBottom(5 rem),

    unsafeChild("a")(
      color(StyleConstants.Colors.Red),

      &.hover (
        color(StyleConstants.Colors.Red)
      ),

      &.visited (
        color(StyleConstants.Colors.Red)
      )
    ),

    MediaQueries.tabletLandscape(
      style(
        width(100 %%),
        paddingLeft(`0`)
      )
    )
  )

  private val liStyle = style(
    position.relative,
    paddingLeft(2 rem),
    margin(.5 rem, `0`, .5 rem, 4.5 rem),

    MediaQueries.phone(
      style(
        marginLeft(1.5 rem)
      )
    )
  )

  private val liBulletStyle = style(
    position.absolute,
    left(`0`),
    top(`0`)
  )

  val defaultList = style(
    unsafeChild("li") (
      liStyle,

      &.before(
        liBulletStyle,
        content := "\"•\""
      )
    )
  )

  val innerList = style(
    unsafeChild("li") (
      liStyle,

      &.before(
        liBulletStyle,
        content := "\"‣\""
      )
    )
  )

  val stepsList = style(
    counterReset := "steps",
    unsafeChild("li") (
      liStyle,

      &.before(
        liBulletStyle,
        counterIncrement := "steps",
        content := "counters(steps, '.')\".\""
      )
    )
  )

  val codeWrapper = style(
    marginTop(1.5 rem),
    marginBottom(1.5 rem),
    paddingTop(1 rem),
    paddingBottom(1 rem)
  )

  val codeBlock = style(
    counterReset := "code",
    listStyleType := "decimal",
    listStylePosition.outside,
    fontFamily :=! "Consolas, Monaco, 'Andale Mono', 'Ubuntu Mono', monospace",
    fontSize(1.6 rem),
    color(StyleConstants.Colors.GreySemi),
    paddingLeft(6 rem),
    marginBottom(`0`),

    unsafeChild(":not(pre) > code[class*=\"language-\"]") (
      whiteSpace.pre
    )
  )

  val frame = style(
    StyleUtils.border(),
    display.block,
    padding(1.5 rem),
    margin(2 rem, `0`)
  )

  val blockOnMobile = style(
    MediaQueries.phone(
      style(
        display.block,
        width(100 %%)
      )
    )
  )

  val section = style(
    position.relative,
    width(100 %%),
    overflow.hidden
  )

  val sectionError = style(
    section,
    height :=! s"calc(100vh - 200px)",
    color.white,
    backgroundColor.black,
    backgroundImage := "url(assets/images/intro_bg.jpg)",
    backgroundSize := "cover",

    media.minHeight(1 px).maxHeight(StyleConstants.Sizes.MinSiteHeight - 1 px)(
      height.auto,
      paddingTop(80 px),
      paddingBottom(100 px)
    ),

    MediaQueries.tabletLandscape(
      style(
        height.auto,
        paddingTop(80 px),
        paddingBottom(100 px)
      )
    )
  )(Compose.trust)

  val errorInner = style(
    StyleUtils.relativeMiddle,
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
      style(
        top(auto),
        transform := "translateY(0)"
      )
    )
  )(Compose.trust)

  val errorHead = style(
    UdashFonts.acumin(FontWeight.SemiBold),
    fontSize(8.8 rem),
    lineHeight(1.1),
    transform := "translate3d(0, 0, 1)",
    textShadow := "0 0 15px black",

    &.after(
      content := "\" \"",
      position.absolute,
      bottom(-110 px),
      right(-30 px),
      width(400 px),
      height(213 px),
      backgroundImage := "url(assets/images/intro_bird.png)",
      backgroundSize := "100%",

      media.minHeight(1 px).maxHeight(850 px)(
        width(400 * .7 px),
        height(213 * .7 px),
        bottom(-80 px)
      ),

      MediaQueries.tabletLandscape(
        style(
          width(400 * .7 px),
          height(213 * .7 px),
          bottom(-80 px)
        )
      ),

      MediaQueries.tabletLandscape(
        style(
          display.none
        )
      )
    ),

    media.minHeight(StyleConstants.Sizes.MinSiteHeight px).maxHeight(850 px)(
      marginTop(2 rem),
      marginBottom(3 rem)
    ),

    media.minHeight(751 px).maxHeight(850 px)(
      marginTop(2 rem),
      marginBottom(3 rem),
      fontSize(8 rem)
    ),

    media.minHeight(651 px).maxHeight(750 px)(
      fontSize(6 rem)
    ),

    media.minHeight(StyleConstants.Sizes.MinSiteHeight px).maxHeight(650 px)(
      fontSize(5 rem)
    ),

    media.minHeight(1 px).maxHeight(StyleConstants.Sizes.MinSiteHeight - 1 px)(
      fontSize(6 rem),
      marginTop(4 rem),
      marginBottom(6 rem)
    ),

    MediaQueries.tabletLandscape(
      style(
        fontSize(8 rem).important
      )
    ),

    MediaQueries.tabletLandscape(
      style(
        marginTop(3 rem).important,
        marginBottom(6 rem).important,
        fontSize(6 rem).important
      )
    ),

    MediaQueries.phone(
      style(
        fontSize(4 rem).important,
        lineHeight(1.2)
      )
    )
  )(Compose.trust)
}
