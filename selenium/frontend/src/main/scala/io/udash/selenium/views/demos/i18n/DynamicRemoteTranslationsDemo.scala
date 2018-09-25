package io.udash.selenium.views.demos.i18n

import io.udash.bootstrap.BootstrapStyles
import io.udash.i18n._
import io.udash.selenium.Launcher
import io.udash.selenium.rpc.demos.i18n.Translations
import org.scalajs.dom
import org.scalajs.dom.Event
import org.scalajs.dom.ext.LocalStorage

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

class DynamicRemoteTranslationsDemo {
  import io.udash.css.CssView._
  import scalatags.JsDom.all._

  def getTemplate: dom.Element = {
    implicit val translationProvider = new RemoteTranslationProvider(Launcher.serverRpc.demos().translations(), Some(LocalStorage), 6 hours)
    implicit val lang = LangProperty(Lang("en"))
    div(id := "dynamic-rpc-translations-demo")(
      button(BootstrapStyles.Button.btn, BootstrapStyles.Button.btnPrimary)(id := "enButton", onclick := ((_: Event) => lang.set(Lang("en"))))("EN"), " ",
      button(BootstrapStyles.Button.btn, BootstrapStyles.Button.btnPrimary)(id := "plButton", onclick := ((_: Event) => lang.set(Lang("pl"))))("PL"),
      ul(BootstrapStyles.Well.well)(
        li("auth.loginLabel: ", translatedDynamic(Translations.auth.loginLabel)(_.apply())),
        li("auth.passwordLabel: ", translatedDynamic(Translations.auth.passwordLabel)(_.apply())),
        li("auth.login.buttonLabel: ", translatedDynamic(Translations.auth.login.buttonLabel)(_.apply())),
        li("auth.login.retriesLeft: ", translatedDynamic(Translations.auth.login.retriesLeft)(_.apply(3))),
        li("auth.login.retriesLeftOne: ", translatedDynamic(Translations.auth.login.retriesLeftOne)(_.apply())),
        li("auth.register.buttonLabel: ", translatedDynamic(Translations.auth.register.buttonLabel)(_.apply()))
      )
    ).render
  }
}