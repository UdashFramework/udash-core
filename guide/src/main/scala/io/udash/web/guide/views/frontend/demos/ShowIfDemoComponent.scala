package io.udash.web.guide.views.frontend.demos

import io.udash._
import io.udash.web.commons.views.Component
import io.udash.web.guide.styles.partials.GuideStyles
import org.scalajs.dom

import scalatags.JsDom

class ShowIfDemoComponent extends Component {
  import JsDom.all._

  val visible: Property[Boolean] = Property[Boolean](true)

  dom.window.setInterval(() => visible.set(!visible.get), 1000)

  override def getTemplate: Modifier = div(id := "show-if-demo", GuideStyles.frame)(
    span("Visible: ", bind(visible), " -> "),
    showIf(visible)(span("Show/hide").render)
  )
}
