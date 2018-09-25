package io.udash.selenium.views.demos.i18n

import io.udash.i18n._
import io.udash.selenium.Launcher
import io.udash.selenium.rpc.demos.i18n.Translations
import org.scalajs.dom
import org.scalajs.dom.ext.LocalStorage

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

class RemoteTranslationsDemo {
  import scalatags.JsDom.all._

  def getTemplate: dom.Element = {
    implicit val translationProvider = new RemoteTranslationProvider(Launcher.serverRpc.demos().translations(), Some(LocalStorage), 6 hours)
    implicit val lang = Lang("pl")
    div(id := "rpc-translations-demo")(
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