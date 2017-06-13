package io.udash.web.homepage.styles

import io.udash.css.CssBase
import io.udash.web.commons.styles.DefaultStyles
import io.udash.web.commons.styles.utils.MediaQueries

import scala.language.postfixOps

object HomepageDefaultStyles extends CssBase with DefaultStyles {
  import dsl._

  style(
    unsafeRoot("body") (
      fontSize(1.7 rem)
    ),

    unsafeRoot("p")(
      fontSize(1.6 rem)
    ),

    unsafeRoot("h1") (
      marginBottom(5 rem),

      MediaQueries.phone(
        paddingTop(5 rem),
        marginBottom(3 rem)
      )
    )
  )
}
