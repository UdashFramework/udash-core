package io.udash.guide.views.frontend.demos

import io.udash._
import io.udash.guide.styles.partials.GuideStyles
import org.scalajs.dom
import org.scalajs.dom.Element

import scalatags.JsDom

import scalacss.ScalatagsCss._

class BindAttributeDemoComponent extends Component {
  import io.udash.guide.Context._

  import JsDom.all._

  val visible: Property[Boolean] = Property[Boolean](true)

  dom.window.setInterval(() => visible.set(!visible.get), 1000)

  override def getTemplate: Element = div(id := "bind-attr-demo", GuideStyles.frame)(
    span("Visible: ", bind(visible), " -> "),
    span(bindAttribute(visible)((show, el) => {
      if (show) el.setAttribute("style", "display: inline;")
      else el.setAttribute("style", "display: none;")
    }))("Show/hide")
  ).render
}
