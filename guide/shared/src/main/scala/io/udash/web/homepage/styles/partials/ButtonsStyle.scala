package io.udash.web.homepage.styles.partials

import java.util.concurrent.TimeUnit

import io.udash.css.CssBase
import io.udash.web.commons.styles.utils._

import scala.concurrent.duration.FiniteDuration
import scala.language.postfixOps

object ButtonsStyle extends CssBase {
  import dsl._

  val btn = style(
    CommonStyleUtils.transition(),
    position.relative,
    cursor.pointer,
    whiteSpace.nowrap,
    textAlign.center,
    userSelect := "none",
    textDecoration := "none",
    overflow.hidden,

    &.hover {
      textDecoration := "none"
    }
  )

  private val btnDefaultLine = style(
    content := "\" \"",
    position.absolute,
    backgroundColor.white
  )

  private val btnDefaultLineHor = style(
    CommonStyleUtils.transition(),
    left(`0`),
    width(100 %%),
    height(2 px),
    transform := "scaleX(0)"
  )

  private val btnDefaultLineVert = style(
    CommonStyleUtils.transition(new FiniteDuration(250, TimeUnit.MILLISECONDS), new FiniteDuration(250, TimeUnit.MILLISECONDS)),
    width(2 px),
    height(100 %%),
    top(`0`),
    transform := "scaleY(0)"
  )

  val btnDefault = style(
    btn,
    UdashFonts.acumin(FontWeight.SemiBold),
    display.inlineBlock,
    color.white,
    fontSize(2.8 rem),
    color.white,
    backgroundColor(StyleConstants.Colors.Red),

    &.before (
      btnDefaultLine,
      btnDefaultLineHor,
      top(`0`),
      transformOrigin := "0 50%"
    ),

    &.after (
      btnDefaultLine,
      btnDefaultLineHor,
      bottom(`0`),
      transformOrigin := "100% 50%"
    ),

    MediaQueries.desktop(
      &.hover(
        &.before (
          transform := "scaleX(1)"
        ),

        &.after (
          transform := "scaleX(1)"
        ),

        unsafeChild(s".${btnDefaultInner.className}") (
          &.before (
            transform := "scaleY(1)"
          ),

          &.after (
            transform := "scaleY(1)"
          )
        )
      )
    ),

    MediaQueries.phone(
      width(100 %%),
      textAlign.center
    )
  )

  lazy val btnDefaultInner = style(
    padding(1 rem, 5 rem, 1.3 rem, 5 rem),
    transform := "translate3d(0,0,0)",
    &.before (
      btnDefaultLine,
      btnDefaultLineVert,
      left(`0`),
      transformOrigin := "50% 100%"
    ),

    &.after (
      btnDefaultLine,
      btnDefaultLineVert,
      right(`0`),
      transformOrigin := "50% 0"
    ),

    MediaQueries.phone(
      paddingLeft(`0`),
      paddingRight(`0`)
    )
  )

  val btnDefaultBlack = style(
    &.before (
      backgroundColor.black
    ),

    &.after (
      backgroundColor.black
    )
  )

  val btnDefaultInnerBlack = style(
    &.before (
      backgroundColor.black
    ),

    &.after (
      backgroundColor.black
    )
  )
}
