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
    import org.scalajs.dom.ext.LocalStorage
    import scalatags.JsDom.all._

    import scala.concurrent.duration.DurationInt
    import scala.language.postfixOps

    implicit val translationProvider: RemoteTranslationProvider = {
      new RemoteTranslationProvider(
        serverRpc.demos.translations,
        Some(LocalStorage),
        6 hours
      )
    }

    implicit val lang: Lang = Lang("pl")

    div(
      ul(
        li(
          "auth.loginLabel: ",
          translated(Translations.auth.loginLabel())
        ),
        li(
          "auth.passwordLabel: ",
          translated(Translations.auth.passwordLabel())
        ),
        li(
          "auth.login.buttonLabel: ",
          translated(Translations.auth.login.buttonLabel())
        ),
        li(
          "auth.login.retriesLeft: ",
          translated(Translations.auth.login.retriesLeft(3))
        ),
        li(
          "auth.login.retriesLeftOne: ",
          translated(Translations.auth.login.retriesLeftOne())
        ),
        li(
          "auth.register.buttonLabel: ",
          translated(Translations.auth.register.buttonLabel())
        )
      )
    )
  }.withSourceCode

  override protected def demoWithSource(): (Modifier, Iterator[String]) = {
    (
      div(
        BootstrapUtils.wellStyles,
        id := "rpc-translations-demo",
        GuideStyles.frame,
        GuideStyles.useBootstrap
      )(rendered),
      source.lines
    )
  }
}