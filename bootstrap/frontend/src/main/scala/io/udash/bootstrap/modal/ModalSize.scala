package io.udash.bootstrap.modal

import io.udash.bootstrap.{BootstrapStyles, ClassModifier}
import org.scalajs.dom.Element

import scalacss.StyleA

sealed abstract class ModalSize(sizeStyle: StyleA) extends ClassModifier(sizeStyle)

object ModalSize {

  case object Default extends ModalSize(null) {
    override def applyTo(t: Element): Unit = {}
  }

  case object Large extends ModalSize(BootstrapStyles.Modal.modalLarge)

  case object Small extends ModalSize(BootstrapStyles.Modal.modalSmall)
}
