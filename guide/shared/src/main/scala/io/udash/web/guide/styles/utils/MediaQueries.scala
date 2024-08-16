package io.udash.web.guide.styles.utils

import io.udash.css.{CssBase, CssStyle}
import io.udash.web.commons.styles.utils.StyleConstants
import scalacss.internal.DslBase.ToStyle

import scala.language.postfixOps

object MediaQueries extends CssBase {
  import dsl._

  def desktop(properties: ToStyle*): CssStyle = mixin(
    media.screen.minWidth(StyleConstants.MediaQueriesBounds.TabletLandscapeMax + 1 px)(
      properties: _*
    )
  )

  def tabletLandscape(properties: ToStyle*): CssStyle = mixin(
    media.screen.minWidth(1 px).maxWidth(StyleConstants.MediaQueriesBounds.TabletLandscapeMax px) (
      properties:_*
    )
  )

  def tabletPortrait(properties: ToStyle*): CssStyle = mixin(
    media.screen.minWidth(1 px).maxWidth(StyleConstants.MediaQueriesBounds.TabletMax px) (
      properties:_*
    )
  )

  def phone(properties: ToStyle*): CssStyle = mixin(
    media.screen.minWidth(1 px).maxWidth(StyleConstants.MediaQueriesBounds.PhoneMax px) (
      properties:_*
    )
  )
}
