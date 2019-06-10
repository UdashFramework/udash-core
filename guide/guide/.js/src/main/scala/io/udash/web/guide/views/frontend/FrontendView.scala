package io.udash.web.guide.views.frontend

import io.udash._
import io.udash.web.guide.FrontendState
import io.udash.web.guide.views.ViewContainer

import scalatags.JsDom

case object FrontendViewFactory extends StaticViewFactory[FrontendState.type](() => new FrontendView)


class FrontendView extends ViewContainer {
  import JsDom.all._

  protected val child = div().render

  override def getTemplate: Modifier = div(
    h1("Frontend"),
    p(
      "In this part of the guide you will read about creating a frontend application with Udash. Let's make your ",
      "frontend type-safe, elegant and maintainable. "
    ),
    child
  )
}