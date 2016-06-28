package io.udash.bootstrap.modal

import io.udash.bootstrap.{BootstrapStyles, ClassModifier}
import org.scalajs.dom.Element

sealed abstract class ModalSize(sizeStyle: BootstrapStyles.BootstrapClass) extends ClassModifier(sizeStyle)

object ModalSize {

  case object Default extends ModalSize(BootstrapStyles.BootstrapClass("")) {
    override def applyTo(t: Element): Unit = {}
  }

  case object Large extends ModalSize(BootstrapStyles.Modal.modalLarge)

  case object Small extends ModalSize(BootstrapStyles.Modal.modalSmall)
}
