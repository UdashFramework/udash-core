package io.udash.bootstrap
package form

sealed abstract class InputGroupSize(sizeStyle: Option[BootstrapStyles.BootstrapClass]) extends ClassModifier(sizeStyle.toSeq: _*) {
  def this(sizeStyle: BootstrapStyles.BootstrapClass) =
    this(Some(sizeStyle))
}

object InputGroupSize {
  import BootstrapStyles.Form._
  case object Default extends InputGroupSize(None)
  case object Large extends InputGroupSize(inputGroupLg)
  case object Small extends InputGroupSize(inputGroupSm)
}
