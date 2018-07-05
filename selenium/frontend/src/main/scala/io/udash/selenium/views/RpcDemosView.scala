package io.udash.selenium.views

import io.udash._
import io.udash.css.CssView
import io.udash.selenium.routing.RpcDemosState
import io.udash.selenium.views.demos.rpc._
import scalatags.JsDom.all._

object RpcDemosViewFactory extends StaticViewFactory[RpcDemosState.type](() => new RpcDemosView)

class RpcDemosView extends FinalView with CssView {
  private val content = div(
    h3("RPC demos"),
    new ClientIdDemoComponent().getTemplate, hr,
    new ExceptionsDemoComponent().getTemplate, hr,
    new GenCodecsDemoComponent().getTemplate, hr,
    new NotificationsDemoComponent().getTemplate, hr,
    new PingPongCallDemoComponent().getTemplate, hr,
    new PingPongPushDemoComponent().getTemplate, hr
  )

  override def getTemplate: Modifier = content
}