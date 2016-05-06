package io.udash.web.commons.styles.utils

/**
  * Created by malchik on 2016-03-22.
  */

import scalacss.Defaults._

object StyleConstants extends StyleSheet.Inline{
  import dsl._

  /**
    * SIZES
    */
  object Sizes {
    val BodyWidth = 1075

    val MinSiteHeight = 550

    val LandingPageHeaderHeight = 150

    val HeaderHeight = 80

    val HeaderHeightMobile = 80

    val HeaderHeightPin = 80

    val FooterHeight = 120

    val MenuWidth = 320

    val MobileMenuButton = 50
  }

  /**
    * COLORS
    */
  object Colors {
    val Red = c"#e30613"

    val RedLight = c"#ff2727"

    val RedDark = c"#a6031b"

    val Grey = c"#898989"

    val GreyExtra = c"#ebebeb"

    val GreySemi = c"#cfcfd6"

    val GreySuperDark = c"#1c1c1e"

    val Yellow = c"#ffd600"

    val Cyan = c"#eef4f7"
  }

  /**
    * MEDIA QUERIES
    */
  object MediaQueriesBounds {
    val TabletLandscapeMax = Sizes.BodyWidth - 1

    val TabletLandscapeMin = 768

    val TabletMax = TabletLandscapeMin - 1

    val TableMin = 481

    val PhoneMax = TableMin - 1
  }
}
