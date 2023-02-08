package io.udash.web.guide.views.ext.demo

import io.udash.css.CssView
import io.udash.web.guide.components.BootstrapUtils
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

object RemoteTranslationsDemo extends AutoDemo with CssView {

  private val (rendered, source) = {
    import io.udash.i18n._
    import io.udash.web.guide.Context.serverRpc
    import io.udash.web.guide.demos.i18n.Translations
    import org.scalajs.dom
    import scalatags.JsDom.all._

    import scala.concurrent.duration.DurationInt

    implicit val translationProvider: RemoteTranslationProvider =
      new RemoteTranslationProvider(
        serverRpc.demos.translations,
        Some(dom.window.localStorage),
        6.hours
      )

    implicit val lang: Lang = Lang("pl")

    div(
      ul(
        li(
          "auth.loginLabel: ",
          Translations.auth.loginLabel.translated()
        ),
        li(
          "auth.passwordLabel: ",
          Translations.auth.passwordLabel.translated()
        ),
        li(
          "auth.login.buttonLabel: ",
          Translations.auth.login.buttonLabel.translated()
        ),
        li(
          "auth.login.retriesLeft: ",
          Translations.auth.login.retriesLeft(3).translated()
        ),
        li(
          "auth.login.retriesLeftOne: ",
          Translations.auth.login.retriesLeftOne.translated()
        ),
        li(
          "auth.register.buttonLabel: ",
          Translations.auth.register.buttonLabel.translated()
        )
      )
    )
  }.withSourceCode

  override protected def demoWithSource(): (Modifier, String) =
    (
      div(
        BootstrapUtils.wellStyles,
        id := "rpc-translations-demo",
        GuideStyles.frame,
        GuideStyles.useBootstrap
      )(rendered),
      source
    )
}