package io.udash.selenium.views.demos.i18n

import io.udash.i18n._
import io.udash.selenium.rpc.demos.i18n.Translations
import org.scalajs.dom.Element

object FrontendTranslationsProvider {

  private val translations = Map(
    Lang("en") -> Bundle(BundleHash("enHash"), Map(
      "auth.loginLabel" -> "Username",
      "auth.passwordLabel" -> "Password",
      "auth.login.buttonLabel" -> "Sign in",
      "auth.login.retriesLeft" -> "{} retries left",
      "auth.login.retriesLeftOne" -> "1 retry left",
      "auth.register.buttonLabel" -> "Sign up"
    ))
  )

  def apply(): LocalTranslationProvider =
    new LocalTranslationProvider(translations)
}

class FrontendTranslationsDemo {
  import scalatags.JsDom.all._

  def getTemplate: Element = {
    implicit val translationProvider = FrontendTranslationsProvider()
    implicit val lang = Lang("en")
    div(id := "frontend-translations-demo")(
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