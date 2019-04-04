package io.udash.web.guide.styles

import io.udash.css.CssBase
import io.udash.web.commons.styles.DefaultStyles
import io.udash.web.commons.styles.utils.{FontWeight, MediaQueries, UdashFonts}

import scala.language.postfixOps

object GuideDefaultStyles extends CssBase with DefaultStyles {
  import dsl._

  style(
    unsafeRoot("body") (
      fontSize(1 rem)
    ),

    unsafeRoot("pre") (
      backgroundColor(c"#f5f5f5"),
      overflow.auto
    ),

    unsafeRoot("p")(
      marginTop(1.5625 rem),
      fontSize(1 rem),
      lineHeight(1.3),

      &.firstChild (
        marginTop(`0`)
      )
    ),

    unsafeRoot("h3") (
      UdashFonts.roboto(FontWeight.Thin),
      marginTop(2.8125 rem),
      marginBottom(.9375 rem),
      lineHeight(1.2),
      fontSize(2 rem),

      MediaQueries.phone(
        fontSize(1.625 rem)
      )
    ),

    unsafeRoot("h4") (
      UdashFonts.roboto(FontWeight.Thin),
      marginTop(2.1875 rem),
      marginBottom(.9375 rem),
      lineHeight(1.2),
      fontSize(1.5 rem),

      MediaQueries.phone(
        fontSize(1.25 rem)
      )
    )
  )
}
