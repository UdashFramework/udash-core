package io.udash.guide.styles.partials

import java.util.concurrent.TimeUnit

import io.udash.guide.components.GuideMenu
import io.udash.guide.styles.GlobalStyles
import io.udash.guide.styles.constant.StyleConstants
import io.udash.guide.styles.fonts.{FontWeight, UdashFonts}
import io.udash.guide.styles.utils.{MediaQueries, StyleUtils}

import scala.concurrent.duration.{DurationInt, FiniteDuration}
import scala.language.postfixOps
import scalacss.Defaults._

object GuideStyles extends StyleSheet.Inline {
  import dsl._

  val main = style(
    position.relative,
    paddingBottom(5 rem),
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
  val imgRight = style(float.right)

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

  val navItem = style(
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

  val underlineLink = style(
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
      color.white,
      cursor.pointer,
      textDecoration := "none",

      &.after (
        transformOrigin := "0 50%",
        transform := "scaleX(1)"
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
}
