package io.udash
package web.commons.components

import org.scalajs.dom.html.Div
import scalatags.JsDom

object ForceBootstrap {
  import scalatags.JsDom.all._

  def apply(modifiers: Modifier*): JsDom.TypedTag[Div] =
    div(cls := "bootstrap")( //force Bootstrap styles
      modifiers:_*
    )
}
