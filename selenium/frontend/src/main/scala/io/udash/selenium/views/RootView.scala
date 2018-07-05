package io.udash.selenium.views

import io.udash._
import io.udash.bootstrap.{BootstrapStyles, UdashBootstrap}
import io.udash.css.CssView
import io.udash.selenium.routing.RootState

object RootViewFactory extends StaticViewFactory[RootState.type](() => new RootView)

class RootView extends ContainerView with CssView {
  import scalatags.JsDom.all._

  override def getTemplate: Modifier = div(
    UdashBootstrap.loadBootstrapStyles(),
    UdashBootstrap.loadFontAwesome(),
    div(BootstrapStyles.container)(
      childViewContainer
    )
  )
}