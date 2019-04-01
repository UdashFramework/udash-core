package io.udash.web.guide.views.ext.demo

import io.udash.i18n._
import io.udash.web.guide.demos.i18n.Translations
import io.udash.web.guide.styles.partials.GuideStyles
import org.scalajs.dom
import org.scalajs.dom.ext.LocalStorage

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

object RemoteTranslationsDemo {
  import io.udash.css.CssView._

  import scalatags.JsDom.all._

  def apply(): dom.Element = {
    import io.udash.web.guide.Context._
    implicit val translationProvider = new RemoteTranslationProvider(serverRpc.demos().translations(), Some(LocalStorage), 6 hours)
    implicit val lang = Lang("pl")
    div(id := "rpc-translations-demo", GuideStyles.frame, GuideStyles.useBootstrap)(
      ul(
        li("auth.loginLabel: ", translated(Translations.auth.loginLabel())),
        li("auth.passwordLabel: ", translated(Translations.auth.passwordLabel())),
        li("auth.login.buttonLabel: ", translated(Translations.auth.login.buttonLabel())),
        li("auth.login.retriesLeft: ", translated(Translations.auth.login.retriesLeft(3))),
        li("auth.login.retriesLeftOne: ", translated(Translations.auth.login.retriesLeftOne())),
        li("auth.register.buttonLabel: ", translated(Translations.auth.register.buttonLabel()))
      )
    ).render
  }
}