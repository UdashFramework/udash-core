package io.udash.web.guide.styles

import io.udash.css.CssBase
import io.udash.web.commons.styles.DefaultStyles
import io.udash.web.commons.styles.utils.{FontWeight, MediaQueries, UdashFonts}

import scala.language.postfixOps

object GuideDefaultStyles extends CssBase with DefaultStyles {
  import dsl._

  style(
    unsafeRoot("body") (
      fontSize(1.6 rem)
    ),

    unsafeRoot("pre") (
      backgroundColor(c"#f5f5f5"),
      overflow.auto
    ),

    unsafeRoot("p")(
      marginTop(2.5 rem),
      fontSize(1.6 rem),
      lineHeight(1.3),

      &.firstChild (
        marginTop(`0`)
      )
    ),

    unsafeRoot("a") (
      &.visited(
        color.inherit
      )
    ),

    unsafeRoot("h3") (
      UdashFonts.acumin(FontWeight.ExtraLight),
      marginTop(4.5 rem),
      marginBottom(1.5 rem),
      lineHeight(1.2),
      fontSize(3.2 rem),

      MediaQueries.phone(
        fontSize(2.6 rem)
      )
    ),

    unsafeRoot("h4") (
      UdashFonts.acumin(FontWeight.ExtraLight),
      marginTop(3.5 rem),
      marginBottom(1.5 rem),
      lineHeight(1.2),
      fontSize(2.4 rem),

      MediaQueries.phone(
        fontSize(2 rem)
      )
    )
  )
}
