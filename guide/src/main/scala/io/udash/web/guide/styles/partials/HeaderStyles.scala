package io.udash.web.guide.styles.partials

import io.udash.web.commons.styles.components.{HeaderButtonsStyles, HeaderNavStyles}
import io.udash.web.commons.styles.utils.StyleConstants
import io.udash.web.guide.styles.utils.{MediaQueries, StyleUtils}

import scala.language.postfixOps
import scalacss.Defaults._

/**
  * Created by malchik on 2016-04-04.
  */
object HeaderStyles extends StyleSheet.Inline with HeaderButtonsStyles with HeaderNavStyles {
  import dsl._

  val header = style(
    position.relative,
    backgroundColor.black,
    height(StyleConstants.Sizes.HeaderHeight px),
    fontSize(1.6 rem),
    zIndex(99),

    MediaQueries.tabletPortrait(
      style(
        height(StyleConstants.Sizes.HeaderHeight * .7 px)
      )
    )
  )

  val headerLeft = style(
    position.relative,
    float.left,
    height(100 %%)
  )

  val headerLogo = style(
    StyleUtils.relativeMiddle,
    display.inlineBlock,
    verticalAlign.top,
    width(130 px),
    marginRight(25 px),

    MediaQueries.tabletLandscape(
      style(
        marginLeft(StyleConstants.Sizes.MobileMenuButton px)
      )
    ),

    MediaQueries.tabletPortrait(
      style(
        width(130 * .8 px)
      )
    )
  )
}
