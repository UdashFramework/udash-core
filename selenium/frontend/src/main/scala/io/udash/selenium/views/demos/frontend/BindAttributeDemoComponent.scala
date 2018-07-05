package io.udash.selenium.views.demos.frontend

import io.udash._
import io.udash.css.CssView
import org.scalajs.dom
import scalatags.JsDom

class BindAttributeDemoComponent extends CssView {
  import JsDom.all._

  val visible: Property[Boolean] = Property[Boolean](true)

  dom.window.setInterval(() => visible.set(!visible.get), 1000)

  def getTemplate: Modifier = div(id := "bind-attr-demo")(
    span("Visible: ", bind(visible), " -> "),
    span((style := "display: none;").attrIfNot(visible))("Show/hide")
  )
}
