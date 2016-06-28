package io.udash.bootstrap.form

import io.udash.bootstrap.{BootstrapStyles, ClassModifier}

sealed abstract class InputGroupSize(sizeStyle: Option[BootstrapStyles.BootstrapClass]) extends ClassModifier(sizeStyle.toSeq: _*) {
  def this(sizeStyle: BootstrapStyles.BootstrapClass) =
    this(Some(sizeStyle))
}

object InputGroupSize {
  case object Default extends InputGroupSize(None)
  case object Large extends InputGroupSize(BootstrapStyles.Form.inputGroupLg)
  case object Small extends InputGroupSize(BootstrapStyles.Form.inputGroupSm)
}
