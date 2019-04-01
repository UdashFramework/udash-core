package io.udash.web.commons.styles.utils

import io.udash.css.CssBase

object StyleConstants extends CssBase {
  import dsl._

  /**
    * SIZES
    */
  object Sizes {
    val BodyPaddingPx = 30
    val MinSiteHeight = 550
    val LandingPageHeaderHeight = 150
    val HeaderHeight = 80
    val HeaderHeightMobile = 80
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
    val Cyan = c"#eef4f7"
  }

  /**
    * MEDIA QUERIES
    */
  object MediaQueriesBounds {
    val TabletLandscapeMax = 1074
    val TabletLandscapeMin = 768
    val TabletMax = TabletLandscapeMin - 1
    val TabletMin = 481
    val PhoneMax = TabletMin - 1
  }
}
