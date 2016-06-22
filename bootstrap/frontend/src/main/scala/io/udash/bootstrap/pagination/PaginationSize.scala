package io.udash.bootstrap
package pagination

import org.scalajs.dom.Element

import scalacss.StyleA

sealed abstract class PaginationSize(sizeStyle: StyleA) extends ClassModifier(sizeStyle)

object PaginationSize {
  import io.udash.bootstrap.BootstrapStyles.Pagination._
  case object Default extends PaginationSize(null) {
    override def applyTo(t: Element): Unit = {}
  }

  case object Large extends PaginationSize(paginationLg)
  case object Small extends PaginationSize(paginationSm)
}
