package io.udash.web.commons.styles

import io.udash.css.CssBase
import io.udash.web.commons.styles.utils._

import scala.language.postfixOps
import scalacss.internal.{Attr, Literal}

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
      listStyle := none // TODO
    ),

    unsafeRoot("blockquote, q")(
      quotes.none
    ),

    unsafeRoot("blockquote:before, blockquote:after, q:before, q:after")(
      content := "''",
      content := none
    ),

    unsafeRoot("table")(
      borderCollapse.collapse,
      borderSpacing.`0`
    ),

    unsafeRoot("#application") (
      height(100 %%)
    ),

    unsafeRoot("html") (
      UdashFonts.acumin(),
      position.relative,
      height(100 %%),
      fontSize(62.5 %%)
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
      UdashFonts.acumin(FontWeight.SemiBold),
      paddingTop(7 rem),
      paddingBottom(4.5 rem),
      lineHeight(1.2),
      fontSize(4.8 rem),
      textAlign.left,

      &.after (
        content := "\"—\"",
        position.absolute,
        left(`0`),
        bottom(`0`),
        fontSize(3.6 rem)
      ),

      MediaQueries.phone(
        fontSize(3.2 rem)
      )
    ),

    unsafeRoot("h2") (
      UdashFonts.acumin(FontWeight.SemiBold),
      marginTop(5.5 rem),
      marginBottom(2 rem),
      lineHeight(1.2),
      fontSize(3.2 rem),

      MediaQueries.phone(
        fontSize(2.8 rem)
      )
    ),

    unsafeRoot("blockquote") (
      UdashFonts.acumin(FontWeight.ExtraLight, FontStyle.Italic),
      position.relative,
      margin(4 rem, `0`, 5 rem, 4.5 rem),
      padding(1.5 rem, 3 rem),
      fontSize(3.2 rem),
      color(StyleConstants.Colors.Grey),

      &.before(
        CommonStyleUtils.border(StyleConstants.Colors.Red, .3 rem),
        content := "\" \"",
        position.absolute,
        top(`0`),
        left(`0`),
        height(100 %%)
      ),

      MediaQueries.phone(
        fontSize(2.4 rem)
      )
    ),

    unsafeRoot("a") (
      textDecoration := "none",
      outline(`0`).important,

      &.link(
        textDecoration := "none"
      ),

      &.hover (
        textDecoration := "none"
      ),

      &.hover (
        textDecoration := "underline"
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
      pointerEvents := "none"
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
      Attr.real("-webkit-appearance") := "none",
      margin(`0`)
    ),

    unsafeRoot("input::-webkit-inner-spin-button")(
      Attr.real("-webkit-appearance") := "none",
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

