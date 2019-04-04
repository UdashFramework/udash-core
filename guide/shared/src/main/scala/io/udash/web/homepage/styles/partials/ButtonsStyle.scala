package io.udash.web.homepage.styles.partials

import io.udash.css.{CssBase, CssStyle}
import io.udash.web.commons.styles.utils._

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

object ButtonsStyle extends CssBase {
  import dsl._

  val btn: CssStyle = style(
    CommonStyleUtils.transition(),
    position.relative,
    cursor.pointer,
    whiteSpace.nowrap,
    textAlign.center,
    userSelect := none,
    textDecoration := none,
    overflow.hidden,

    &.hover {
      textDecoration := none
    }
  )

  private val btnDefaultLine = mixin(
    content := "\" \"",
    position.absolute,
    backgroundColor.white
  )

  private val btnDefaultLineHor = mixin(
    CommonStyleUtils.transition(),
    left(`0`),
    width(100 %%),
    height(2 px),
    transform := "scaleX(0)"
  )

  private val btnDefaultLineVert = mixin(
    CommonStyleUtils.transition(),
    transitionDelay(250 milliseconds),
    width(2 px),
    height(100 %%),
    top(`0`),
    transform := "scaleY(0)"
  )

  val btnDefault: CssStyle = style(
    btn,
    UdashFonts.roboto(FontWeight.Bold),
    display.inlineBlock,
    color.white,
    fontSize(1.75 rem),
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

  lazy val btnDefaultInner: CssStyle = style(
    padding(.625 rem, 3.125 rem,.8125 rem, 3.125 rem),
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

  val btnDefaultBlack: CssStyle = style(
    &.before (
      backgroundColor.black
    ),

    &.after (
      backgroundColor.black
    )
  )

  val btnDefaultInnerBlack: CssStyle = style(
    &.before (
      backgroundColor.black
    ),

    &.after (
      backgroundColor.black
    )
  )
}
