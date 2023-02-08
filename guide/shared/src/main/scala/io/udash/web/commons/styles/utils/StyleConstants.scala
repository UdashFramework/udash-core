package io.udash.web.commons.styles.utils

import io.udash.css.CssBase

object StyleConstants extends CssBase {
  import dsl._

  /**
    * SIZES
    */
  object Sizes {
    val BodyWidth = 1075
    val BodyPaddingPx = 30
    val MinSiteHeight = 550
    val LandingPageHeaderHeight = 150
    val HeaderHeight = 80
    val GuideHeaderHeightMobile = HeaderHeight * .7
    val HeaderHeightPin = 80
    val FooterHeight = 120
    val MenuWidth = 320
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
  }

  /**
    * MEDIA QUERIES
    */
  object MediaQueriesBounds {
    val TabletLandscapeMax = 1074
    val TabletMax = 767
    val PhoneMax = 480
  }
}
