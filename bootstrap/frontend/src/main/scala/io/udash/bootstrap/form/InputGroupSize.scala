package io.udash.bootstrap
package form

import io.udash.css.CssStyle

final class InputGroupSize(sizeStyle: Option[CssStyle]) extends ClassModifier(sizeStyle.toSeq: _*) {
  def this(sizeStyle: CssStyle) =
    this(Some(sizeStyle))
}

object InputGroupSize {
  import BootstrapStyles.Form._
  val Default = new InputGroupSize(None)
  val Large = new InputGroupSize(inputGroupLg)
  val Small = new InputGroupSize(inputGroupSm)
}
