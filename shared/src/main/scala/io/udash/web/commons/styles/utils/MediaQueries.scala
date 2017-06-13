package io.udash.web.commons.styles.utils

import io.udash.css.{CssBase, CssStyle}

import scala.language.postfixOps
import scalacss.internal.DslBase.ToStyle

/**
  * Created by malchik on 2016-03-30.
  */
object MediaQueries extends CssBase {
  import dsl._

  def desktop(properties: ToStyle*) = style(
    media.screen.minWidth(StyleConstants.MediaQueriesBounds.TabletLandscapeMax + 1 px) (
      properties:_*
    )
  )

  def tabletLandscape(properties: ToStyle*) = style(
    media.screen.minWidth(1 px).maxWidth(StyleConstants.MediaQueriesBounds.TabletLandscapeMax px) (
      properties:_*
    )
  )

  def tabletPortrait(properties: ToStyle*) = style(
    media.screen.minWidth(1 px).maxWidth(StyleConstants.MediaQueriesBounds.TabletMax px) (
      properties:_*
    )
  )

  def phone(properties: ToStyle*) = style(
    media.screen.minWidth(1 px).maxWidth(StyleConstants.MediaQueriesBounds.PhoneMax px) (
      properties:_*
    )
  )
}
