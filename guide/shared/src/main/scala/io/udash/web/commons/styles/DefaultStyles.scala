package io.udash.web.commons.styles

import io.udash.css.CssBase
import io.udash.web.commons.styles.utils._
import scalacss.internal.{Attr, Literal}

import scala.language.postfixOps

trait DefaultStyles extends CssBase {
  import dsl._

  style(
    unsafeRoot( """
                  |html, body, div, span, applet, object, iframe,
                  |h1, h2, h3, h4, h5, h6, p, blockquote, pre,
                  |a, abbr, acronym, address, big, cite, code,
                  |del, dfn, em, img, ins, kbd, q, s, samp,
                  |small, strike, strong, sub, sup, tt, var,
                  |b, u, i, center,
                  |dl, dt, dd, ol, ul, li,
                  |fieldset, form, label, legend,
                  |table, caption, tbody, tfoot, thead, tr, th, td,
                  |article, aside, canvas, details, embed,
                  |figure, figcaption, footer, header, hgroup,
                  |menu, nav, output, ruby, section, summary,
                  |time, mark, audio, video
                """.stripMargin.replaceAll("\\s+", ""))(
      margin.`0`,
      padding.`0`,
      border.`0`,
      fontSize(100 %%),
      font := Literal.inherit,
      verticalAlign.baseline
    ),

    unsafeRoot("article, aside, details, figcaption, figure, footer, header, hgroup, menu, nav, section")(
      display.block
    ),

    unsafeRoot("body")(
      lineHeight(1)
    ),

    unsafeRoot("ol,ul")(
      listStyle := none
    ),

    unsafeRoot("blockquote, q")(
      quotes.none
    ),

    unsafeRoot("blockquote:before, blockquote:after, q:before, q:after")(
      content.string(""),
      content.none
    ),

    unsafeRoot("table")(
      borderCollapse.collapse,
      borderSpacing.`0`
    ),

    unsafeRoot("#application") (
      height(100 %%)
    ),

    unsafeRoot("html") (
      UdashFonts.roboto(),
      position.relative,
      height(100 %%),
    ),

    unsafeRoot("body") (
      position.relative,
      height(100 %%)
    ),

    unsafeRoot("li")(
      fontSize.inherit,
      lineHeight(1.3)
    ),

    unsafeRoot("h1") (
      position.relative,
      UdashFonts.roboto(FontWeight.Bold),
      paddingTop(4.375 rem),
      paddingBottom(2.8125 rem),
      lineHeight(1.2),
      fontSize(3 rem),
      textAlign.left,

      &.after(
        content.string("—"),
        position.absolute,
        left(`0`),
        bottom(`0`),
        fontSize(2.25 rem)
      ),

      MediaQueries.phone(
        fontSize(2 rem)
      )
    ),

    unsafeRoot("h2") (
      UdashFonts.roboto(FontWeight.Bold),
      marginTop(3.4375 rem),
      marginBottom(1.25 rem),
      lineHeight(1.2),
      fontSize(2 rem),

      MediaQueries.phone(
        fontSize(1.75 rem)
      )
    ),

    unsafeRoot("blockquote") (
      UdashFonts.roboto(FontWeight.Thin, FontStyle.Italic),
      position.relative,
      margin(2.5 rem, `0`, 3.125 rem, 2.8125 rem),
      padding(.9375 rem, 1.875 rem),
      fontSize(2 rem),
      color(StyleConstants.Colors.Grey),

      &.before(
        CommonStyleUtils.border(StyleConstants.Colors.Red,.1875 rem),
        content.string(" "),
        position.absolute,
        top(`0`),
        left(`0`),
        height(100 %%)
      ),

      MediaQueries.phone(
        fontSize(1.5 rem)
      )
    ),

    unsafeRoot("a") (
      textDecoration := none,
      outline(`0`).important,

      &.link(
        textDecoration := none
      ),

      &.hover (
        textDecoration := none
      ),

      &.hover (
        textDecoration := Literal.underline
      )
    ),

    unsafeRoot("img")(
      maxWidth(100 %%),
      height.auto
    ),

    unsafeRoot("svg") (
      display.block
    ),

    unsafeRoot("object[type='image/svg+xml']") (
      display.block,
      pointerEvents.none
    ),

    unsafeRoot("input") (
      &.focus (
        outline.none
      )
    ),

    unsafeRoot("input[type=number]") (
      Attr.real("-moz-appearance") := "textfield"
    ),

    unsafeRoot("input::-webkit-outer-spin-button")(
      Attr.real("-webkit-appearance") := none,
      margin(`0`)
    ),

    unsafeRoot("input::-webkit-inner-spin-button")(
      Attr.real("-webkit-appearance") := none,
      margin(`0`)
    ),

    unsafeRoot("textarea") (
      resize.none
    ),

    unsafeRoot("strong")(
      fontWeight.bolder
    ),

    unsafeRoot("b")(
      fontWeight.bold
    ),

    unsafeRoot("i")(
      fontStyle.italic,
      fontWeight._600
    ),

    unsafeRoot("em")(
      fontStyle.italic,
      fontWeight._600
    ),

    unsafeRoot("*") (
      boxSizing.borderBox,

      &.before (
        boxSizing.borderBox
      ),

      &.after (
        boxSizing.borderBox
      )
    )
  )
}

