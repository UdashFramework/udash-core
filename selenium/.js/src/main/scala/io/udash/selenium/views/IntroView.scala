package io.udash.selenium.views

import io.udash._
import io.udash.css.CssView
import io.udash.selenium.routing._
import scalatags.JsDom.all._

object IntroViewFactory extends StaticViewFactory[RootState.type](() => new IntroView)

class IntroView extends FinalView with CssView {
  import io.udash.selenium.Launcher.applicationInstance

  private val content = div(
    h3("Demo pages"),
    ul(
      li(a(href := FrontendDemosState.url)("Frontend")),
      li(a(href := FrontendRoutingDemosState(None).url)("Frontend Routing")),
      li(a(href := RpcDemosState.url)("RPC")),
      li(a(href := RestDemosState.url)("REST")),
      li(a(href := I18nDemosState.url)("i18n")),
      li(a(href := JQueryDemosState.url)("jQuery")),
      li(a(href := BootstrapDemosState.url)("Bootstrap"))
    )
  )

  override def getTemplate: Modifier = content
}