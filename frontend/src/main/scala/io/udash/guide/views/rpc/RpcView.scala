package io.udash.guide.views.rpc

import io.udash._
import io.udash.guide._
import io.udash.guide.views.ViewContainer
import org.scalajs.dom

import scalatags.JsDom

case object RpcViewPresenter extends DefaultViewPresenterFactory[RpcState.type](() => new RpcView)

class RpcView extends ViewContainer {
  import JsDom.all._

  protected val child = div().render

  override def getTemplate: dom.Element = div(
    h1("RPC in Udash"),
    p(
      "In this part of the guide you can read about client-server communication in a Udash application."
    ),
    child
  ).render
}