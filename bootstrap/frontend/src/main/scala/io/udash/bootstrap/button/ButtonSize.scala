package io.udash.bootstrap
package button

import org.scalajs.dom.Element

import scalacss.StyleA

sealed abstract class ButtonSize(sizeStyle: StyleA) extends ClassModifier(sizeStyle)

object ButtonSize {

  import BootstrapStyles._

  case object Default extends ButtonSize(null) {
    override def applyTo(t: Element): Unit = {}
  }

  case object Large extends ButtonSize(lg)

  case object Small extends ButtonSize(sm)

  case object ExtraSmall extends ButtonSize(xs)

}
