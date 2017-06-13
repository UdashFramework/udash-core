package io.udash.web.guide.views.rest

import io.udash._
import io.udash.web.guide._
import io.udash.web.guide.views.ViewContainer

import scalatags.JsDom

case object RestViewPresenter extends DefaultViewPresenterFactory[RestState.type](() => new RestView)

class RestView extends ViewContainer {
  import JsDom.all._

  protected val child = div().render

  override def getTemplate: Modifier = div(
    h1("REST in Udash"),
    p("In this part of the guide you can read about REST API usage in a Udash application."),
    child
  )
}