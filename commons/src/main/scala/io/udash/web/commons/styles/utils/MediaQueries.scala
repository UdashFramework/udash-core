package io.udash.web.commons.styles.utils

import scala.language.postfixOps
import scalacss.ProdDefaults._

/**
  * Created by malchik on 2016-03-30.
  */
object MediaQueries extends StyleSheet.Inline {
  import dsl._

  def desktop(properties: StyleA) = style(
    media.screen.minWidth(StyleConstants.MediaQueriesBounds.TabletLandscapeMax + 1 px) (
      properties
    )
  )

  def tabletLandscape(properties: StyleA) = style(
    media.screen.minWidth(1 px).maxWidth(StyleConstants.MediaQueriesBounds.TabletLandscapeMax px) (
      properties
    )
  )

  def tabletPortrait(properties: StyleA) = style(
    media.screen.minWidth(1 px).maxWidth(StyleConstants.MediaQueriesBounds.TabletMax px) (
      properties
    )
  )

  def phone(properties: StyleA) = style(
    media.screen.minWidth(1 px).maxWidth(StyleConstants.MediaQueriesBounds.PhoneMax px) (
      properties
    )
  )
}
