package io.udash.bootstrap
package form

import io.udash.css.CssStyle

final class InputGroupSize(sizeStyle: Option[CssStyle]) extends ClassModifier(sizeStyle.toSeq: _*) {
  def this(sizeStyle: CssStyle) =
    this(Some(sizeStyle))
}

object InputGroupSize {
  import BootstrapStyles.InputGroup

  final val Default = new InputGroupSize(None)
  final val Large = new InputGroupSize(InputGroup.size(BootstrapStyles.Size.Large))
  final val Small = new InputGroupSize(InputGroup.size(BootstrapStyles.Size.Small))
}
