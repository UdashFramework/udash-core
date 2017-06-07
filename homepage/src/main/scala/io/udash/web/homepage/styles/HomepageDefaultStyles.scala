package io.udash.web.homepage.styles

import io.udash.web.commons.styles.{DefaultStyles, UdashStylesheet}
import io.udash.web.commons.styles.utils._

import scala.language.postfixOps
import scalacss.ProdDefaults._

object HomepageDefaultStyles extends UdashStylesheet with DefaultStyles {
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
        style(
          paddingTop(5 rem),
          marginBottom(3 rem)
        )
      )
    )
  )
}
