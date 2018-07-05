package io.udash.selenium.views.demos.frontend

import io.udash._
import io.udash.css.CssView
import org.scalajs.dom
import scalatags.JsDom

class ShowIfDemoComponent extends CssView {
  import JsDom.all._

  val visible: Property[Boolean] = Property[Boolean](true)

  dom.window.setInterval(() => visible.set(!visible.get), 1000)

  def getTemplate: Modifier = div(id := "show-if-demo")(
    span("Visible: ", bind(visible), " -> "),
    showIf(visible)(span("Show/hide").render)
  )
}
