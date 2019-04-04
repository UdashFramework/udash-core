package io.udash.web.homepage.styles

import io.udash.css.CssBase
import io.udash.web.commons.styles.DefaultStyles
import io.udash.web.commons.styles.utils.MediaQueries

import scala.language.postfixOps

object HomepageDefaultStyles extends CssBase with DefaultStyles {
  import dsl._

  style(
    unsafeRoot("body") (
      fontSize(1.0625 rem)
    ),

    unsafeRoot("p")(
      fontSize(1 rem)
    ),

    unsafeRoot("h1") (
      marginBottom(3.125 rem),

      MediaQueries.phone(
        paddingTop(3.125 rem),
        marginBottom(1.875 rem)
      )
    )
  )
}
