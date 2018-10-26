package io.udash.selenium.views

import io.udash._
import io.udash.css.CssView
import io.udash.selenium.routing.I18nDemosState
import io.udash.selenium.views.demos.i18n._
import scalatags.JsDom.all._

object I18nDemosViewFactory extends StaticViewFactory[I18nDemosState.type](() => new I18nDemosView)

class I18nDemosView extends FinalView with CssView {
  private val content = div(
    h3("I18n demos"),
    new DynamicRemoteTranslationsDemo().getTemplate, hr,
    new RemoteTranslationsDemo().getTemplate, hr,
    new FrontendTranslationsDemo().getTemplate, hr
  )

  override def getTemplate: Modifier = content
}