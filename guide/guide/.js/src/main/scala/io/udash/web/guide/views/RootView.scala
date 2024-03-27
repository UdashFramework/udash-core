package io.udash.web.guide.views

import io.udash.*
import io.udash.web.commons.components.Footer
import io.udash.web.guide.RootState
import io.udash.web.guide.components.Header
import org.scalajs.dom.Element

object RootViewFactory extends StaticViewFactory[RootState.type](() => new RootView)

class RootView extends ViewContainer {
  import scalatags.JsDom.all._

  override protected val child: Element = div().render

  private val content = div(
    Header.getTemplate,
    child,
    Footer.getTemplate()
  )

  override def getTemplate: Modifier = content
}