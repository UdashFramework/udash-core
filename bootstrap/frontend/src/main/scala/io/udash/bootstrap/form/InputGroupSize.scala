package io.udash.bootstrap
package form

import io.udash.css.CssStyle

sealed abstract class InputGroupSize(sizeStyle: Option[CssStyle]) extends ClassModifier(sizeStyle.toSeq: _*) {
  def this(sizeStyle: CssStyle) =
    this(Some(sizeStyle))
}

object InputGroupSize {
  import BootstrapStyles.Form._
  case object Default extends InputGroupSize(None)
  case object Large extends InputGroupSize(inputGroupLg)
  case object Small extends InputGroupSize(inputGroupSm)
}
