package io.udash.guide.views.frontend

import io.udash._
import io.udash.guide.FrontendState
import io.udash.guide.styles.partials.GuideStyles
import io.udash.guide.views.ViewContainer
import org.scalajs.dom

import scalatags.JsDom
import scalacss.ScalatagsCss._

case object FrontendViewPresenter extends DefaultViewPresenterFactory[FrontendState.type](() => new FrontendView)


class FrontendView extends ViewContainer {
  import JsDom.all._

  protected val child = div().render

  override def getTemplate: dom.Element = div(
    h1("Frontend"),
    p(
      "In this part of the guide you will read about creating a frontend application with Udash. Let's make your ",
      "frontend type safe, elegant and maintainable. "
    ),
    child
  ).render
}