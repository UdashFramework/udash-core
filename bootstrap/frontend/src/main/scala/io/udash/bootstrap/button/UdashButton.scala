package io.udash.bootstrap
package button

import io.udash._
import io.udash.bootstrap.button.UdashButton.ButtonClickEvent
import org.scalajs.dom
import org.scalajs.dom._

import scalacss.ScalatagsCss._
import scalatags.JsDom
import scalatags.JsDom.all._

class UdashButton private(style: ButtonStyle, size: ButtonSize, block: Boolean,
                          val active: Property[Boolean], val disabled: Property[Boolean])
                         (mds: Modifier*) extends UdashBootstrapComponent with Listenable[ButtonClickEvent] {
  import BootstrapStyles.Button._

  private lazy val classes: List[Modifier] = btn :: style :: size ::
    BootstrapStyles.Button.btnBlock.styleIf(block) :: BootstrapStyles.active.styleIf(active) ::
    BootstrapStyles.disabled.styleIf(disabled) :: JsDom.all.disabled.attrIf(disabled) :: Nil

  lazy val render: dom.html.Button =
    JsDom.all.button(classes: _*)(onclick :+= ((_: MouseEvent) => {
      fire(ButtonClickEvent(this))
      false
    }))(mds: _*).render
}

object UdashButton {
  case class ButtonClickEvent(button: UdashButton) extends ListenableEvent

  import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

  def apply(style: ButtonStyle = ButtonStyle.Default, size: ButtonSize = ButtonSize.Default, block: Boolean = false,
            active: Property[Boolean] = Property(false), disabled: Property[Boolean] = Property(false))(mds: Modifier*): UdashButton =
    new UdashButton(style, size, block, active, disabled)(mds: _*)

  def toggle(style: ButtonStyle = ButtonStyle.Default, size: ButtonSize = ButtonSize.Default, block: Boolean = false,
             active: Property[Boolean] = Property(false), disabled: Property[Boolean] = Property(false))(mds: Modifier*): UdashButton = {
    val button = new UdashButton(style, size, block, active, disabled)(mds: _*)
    button.listen { case _ => active.set(!active.get) }
    button
  }

}
