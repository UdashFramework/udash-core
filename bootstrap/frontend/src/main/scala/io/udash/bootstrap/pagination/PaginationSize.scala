package io.udash.bootstrap
package pagination

import io.udash.css.CssStyle

final class PaginationSize(sizeStyle: Option[CssStyle]) extends ClassModifier(sizeStyle.toSeq: _*) {
  def this(sizeStyle: CssStyle) =
    this(Some(sizeStyle))
}

object PaginationSize {
  import io.udash.bootstrap.BootstrapStyles.Pagination._

  final val Default = new PaginationSize(None)
  final val Large = new PaginationSize(paginationLg)
  final val Small = new PaginationSize(paginationSm)
}
