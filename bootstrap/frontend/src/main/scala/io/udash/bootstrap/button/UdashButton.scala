package io.udash.bootstrap
package button

import org.scalajs.dom

import scalacss.ScalatagsCss._
import scalatags.JsDom
import scalatags.JsDom.all._

private class UdashButtonGenerator(style: ButtonStyle = ButtonStyle.Default, size: ButtonSize = ButtonSize.Default,
                                   block: Boolean = false, active: Boolean = false, disabled: Boolean = false) {

  import BootstrapStyles.Button._

  private val classlist: List[Modifier] = btn :: style :: size ::
    (optionalModifier(block, BootstrapStyles.Button.btnBlock) :: optionalModifier(active, BootstrapStyles.active) ::
      optionalModifier(disabled, BootstrapStyles.disabled) :: Nil).flatten

  def button(mds: Modifier*): dom.Element = JsDom.all.button(classlist: _*)(mds: _*).render

}

object UdashButton {

  def apply(text: String): dom.Element = new UdashButtonGenerator().button(text)

}
