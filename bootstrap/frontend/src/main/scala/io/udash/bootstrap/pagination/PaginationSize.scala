package io.udash.bootstrap
package pagination

import io.udash.css.CssStyle

sealed abstract class PaginationSize(sizeStyle: Option[CssStyle]) extends ClassModifier(sizeStyle.toSeq: _*) {
  def this(sizeStyle: CssStyle) =
    this(Some(sizeStyle))
}

object PaginationSize {
  import io.udash.bootstrap.BootstrapStyles.Pagination._

  case object Default extends PaginationSize(None)
  case object Large extends PaginationSize(paginationLg)
  case object Small extends PaginationSize(paginationSm)
}
