package io.udash.guide.views.bootstrapping

import io.udash.core.{DefaultViewPresenterFactory, View}
import io.udash.guide.views.ViewContainer
import io.udash.guide.{Context, _}
import org.scalajs.dom

import scalatags.JsDom

case object BootstrappingViewPresenter extends DefaultViewPresenterFactory[BootstrappingState.type](() => new BootstrappingView)

class BootstrappingView extends ViewContainer {
  import Context._

  import JsDom.all._

  protected val child = div().render

  override def getTemplate: dom.Element = div(
    h1("Application bootstrapping"),
    p("In this part of the guide you will read about bootstrapping an Udash application from scratch."),
    p(
      i("This is an advanced topic, if you want to start development as soon as possible, start with ",
      a(href := BootstrappingGeneratorsState.url)("Udash generators"), ".")
    ),
    child
  ).render
}