package io.udash.bootstrap
package pagination


import scalacss.StyleA

sealed abstract class PaginationSize(sizeStyle: Option[StyleA]) extends ClassModifier(sizeStyle.toSeq: _*) {
  def this(sizeStyle: StyleA) = this(Some(sizeStyle))
}

object PaginationSize {
  import io.udash.bootstrap.BootstrapStyles.Pagination._

  case object Default extends PaginationSize(None)
  case object Large extends PaginationSize(paginationLg)
  case object Small extends PaginationSize(paginationSm)
}
