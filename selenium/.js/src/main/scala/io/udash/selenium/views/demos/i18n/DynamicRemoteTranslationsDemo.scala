package io.udash.selenium.views.demos.i18n

import io.udash._
import io.udash.bootstrap.button.UdashButton
import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.i18n._
import io.udash.selenium.Launcher
import io.udash.selenium.rpc.demos.i18n.Translations
import org.scalajs.dom.Element
import org.scalajs.dom.ext.LocalStorage

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

class DynamicRemoteTranslationsDemo {
  import scalatags.JsDom.all._

  def getTemplate: Element = {
    implicit val translationProvider = new RemoteTranslationProvider(Launcher.serverRpc.call().demos().translations(), Some(LocalStorage), 6 hours)
    implicit val lang = LangProperty(Lang("en"))

    val enBtn = UdashButton(buttonStyle = BootstrapStyles.Color.Primary.toProperty, componentId = ComponentId("enButton"))(_ => "EN")
    val plBtn = UdashButton(buttonStyle = BootstrapStyles.Color.Primary.toProperty, componentId = ComponentId("plButton"))(_ => "PL")

    enBtn.listen { case UdashButton.ButtonClickEvent(_, _) => lang.set(Lang("en")) }
    plBtn.listen { case UdashButton.ButtonClickEvent(_, _) => lang.set(Lang("pl")) }

    div(id := "dynamic-rpc-translations-demo")(
      enBtn.render, " ", plBtn.render,
      ul(
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