package io.udash.bootstrap
package button

sealed abstract class ButtonSize(sizeStyle: Option[BootstrapStyles.BootstrapClass]) extends ClassModifier(sizeStyle.toSeq: _*) {
  def this(sizeStyle: BootstrapStyles.BootstrapClass) =
    this(Some(sizeStyle))
}

object ButtonSize {

  import BootstrapStyles.Button._

  case object Default extends ButtonSize(None)

  case object Large extends ButtonSize(btnLg)

  case object Small extends ButtonSize(btnSm)

  case object ExtraSmall extends ButtonSize(btnXs)

}
