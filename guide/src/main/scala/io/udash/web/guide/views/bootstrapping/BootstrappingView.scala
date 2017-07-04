package io.udash.web.guide.views.bootstrapping

import io.udash._
import io.udash.web.guide.views.ViewContainer
import io.udash.web.guide.{Context, _}

import scalatags.JsDom

case object BootstrappingViewFactory extends StaticViewFactory[BootstrappingState.type](() => new BootstrappingView)

class BootstrappingView extends ViewContainer {
  import Context._

  import JsDom.all._

  protected val child = div().render

  override def getTemplate: Modifier = div(
    h1("Application bootstrapping"),
    p("In this part of the guide you will read about bootstrapping an Udash application from scratch."),
    p(
      i("This is an advanced topic, if you want to start development as soon as possible, start with ",
      a(href := BootstrappingGeneratorsState.url)("Udash generators"), "")
    ),
    child
  )
}