package io.udash
package web.guide.views.frontend

import io.udash.css.CssView
import io.udash.web.guide.FrontendBindingsTestState

case object FrontendBindingsTestViewFactory extends StaticViewFactory[FrontendBindingsTestState.type](() => new FrontendBindingsTestView)

final class FrontendBindingsTestView extends View with CssView {

  println("view init")

  import scalatags.JsDom.all._

  override def getTemplate: Modifier = {
    println("view get template")
    div(
      h2("Bindings test"),
      p(
        "Test test"
      )
    )
  }

  override def onClose(): Unit = {
    println("view closed")
  }
}