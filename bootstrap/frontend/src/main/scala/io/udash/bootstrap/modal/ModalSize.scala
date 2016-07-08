package io.udash.bootstrap
package modal

sealed abstract class ModalSize(sizeStyle: Option[BootstrapStyles.BootstrapClass]) extends ClassModifier(sizeStyle.toSeq:_*) {
  def this(sizeStyle: BootstrapStyles.BootstrapClass) = this(Some(sizeStyle))
}

object ModalSize {
  import BootstrapStyles.Modal._

  case object Default extends ModalSize(None)
  case object Large extends ModalSize(modalLarge)
  case object Small extends ModalSize(modalSmall)
}
