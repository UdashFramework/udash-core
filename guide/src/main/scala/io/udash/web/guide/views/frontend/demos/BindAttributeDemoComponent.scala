package io.udash.web.guide.views.frontend.demos

import io.udash._
import io.udash.web.commons.views.Component
import io.udash.web.guide.styles.partials.GuideStyles
import org.scalajs.dom

import scalatags.JsDom

class BindAttributeDemoComponent extends Component {
  import io.udash.web.guide.Context._
  import JsDom.all._

  val visible: Property[Boolean] = Property[Boolean](true)

  dom.window.setInterval(() => visible.set(!visible.get), 1000)

  override def getTemplate: Modifier = div(id := "bind-attr-demo", GuideStyles.frame)(
    span("Visible: ", bind(visible), " -> "),
    span((style := "display: none;").attrIfNot(visible))("Show/hide")
  )
}
