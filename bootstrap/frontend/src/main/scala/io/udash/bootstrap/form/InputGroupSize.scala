package io.udash.bootstrap
package form

import io.udash.css.CssStyle

final class InputGroupSize(sizeStyle: Option[CssStyle]) extends ClassModifier(sizeStyle.toSeq: _*) {
  def this(sizeStyle: CssStyle) =
    this(Some(sizeStyle))
}

object InputGroupSize {
  import BootstrapStyles.Form._
  final val Default = new InputGroupSize(None)
  final val Large = new InputGroupSize(inputGroupLg)
  final val Small = new InputGroupSize(inputGroupSm)
}
