package io.udash.bootstrap.form

import io.udash.bootstrap.{BootstrapStyles, ClassModifier}

import scalacss.StyleA

sealed abstract class InputGroupSize(sizeStyle: Option[StyleA]) extends ClassModifier(sizeStyle.toSeq: _*) {
  def this(sizeStyle: StyleA) = this(Some(sizeStyle))
}

object InputGroupSize {
  case object Default extends InputGroupSize(None)
  case object Large extends InputGroupSize(BootstrapStyles.Form.inputGroupLg)
  case object Small extends InputGroupSize(BootstrapStyles.Form.inputGroupSm)
}
