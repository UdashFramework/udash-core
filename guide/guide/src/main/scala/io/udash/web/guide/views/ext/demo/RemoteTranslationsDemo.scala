package io.udash.web.guide.views.ext.demo

import io.udash.css.CssView
import io.udash.i18n._
import io.udash.web.guide.components.BootstrapUtils
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.demos.i18n.Translations
import io.udash.web.guide.styles.partials.GuideStyles
import org.scalajs.dom.ext.LocalStorage
import scalatags.JsDom

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

object RemoteTranslationsDemo extends AutoDemo with CssView {

  import JsDom.all._

  private val (rendered, source) = {
    import io.udash.web.guide.Context._

    implicit val translationProvider = new RemoteTranslationProvider(
      serverRpc.demos.translations,
      Some(LocalStorage),
      6 hours
    )
    implicit val lang = Lang("pl")

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

  override protected def demoWithSource(): (JsDom.all.Modifier, Iterator[String]) = {
    (div(BootstrapUtils.wellStyles)(id := "rpc-translations-demo", GuideStyles.frame, GuideStyles.useBootstrap)(
      rendered), source.lines.drop(1))
  }
}