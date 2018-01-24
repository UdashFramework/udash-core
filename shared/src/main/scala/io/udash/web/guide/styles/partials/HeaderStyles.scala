package io.udash.web.guide.styles.partials

import io.udash.css.CssBase
import io.udash.web.commons.styles.components.{HeaderButtonsStyles, HeaderNavStyles}
import io.udash.web.commons.styles.utils.StyleConstants
import io.udash.web.guide.styles.utils.{MediaQueries, GuideStyleUtils}

import scala.language.postfixOps

/**
  * Created by malchik on 2016-04-04.
  */
object HeaderStyles extends CssBase with HeaderButtonsStyles with HeaderNavStyles {
  import dsl._

  val header = style(
    position.relative,
    backgroundColor.black,
    height(StyleConstants.Sizes.HeaderHeight px),
    fontSize(1.6 rem),
    zIndex(99),

    MediaQueries.tabletPortrait(
      height(StyleConstants.Sizes.GuideHeaderHeightMobile px)
    )
  )

  val headerLeft = style(
    position.relative,
    float.left,
    height(100 %%)
  )

  val headerLogo = style(
    GuideStyleUtils.relativeMiddle,
    display.inlineBlock,
    verticalAlign.top,
    width(130 px),
    marginRight(25 px),

    MediaQueries.tabletLandscape(
      marginLeft(StyleConstants.Sizes.GuideHeaderHeightMobile px)
    ),

    MediaQueries.tabletPortrait(
      width(130 * .8 px)
    )
  )
}
