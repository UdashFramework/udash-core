package io.udash.web.guide.views.rpc

import io.udash.*
import io.udash.web.guide.*
import io.udash.web.guide.views.ViewContainer
import org.scalajs.dom.Element
import scalatags.JsDom

case object RpcViewFactory extends StaticViewFactory[RpcState.type](() => new RpcView)

class RpcView extends ViewContainer {
  import JsDom.all._

  override protected val child: Element = div().render

  override def getTemplate: Modifier = div(
    h1("RPC in Udash"),
    p("In this part of the guide you can read about client-server communication in a Udash application."),
    child
  )
}