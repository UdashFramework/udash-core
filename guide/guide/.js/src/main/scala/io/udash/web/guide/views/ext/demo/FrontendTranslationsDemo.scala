package io.udash.web.guide.views.ext.demo

import io.udash.css.CssView
import io.udash.web.guide.components.BootstrapUtils
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

object FrontendTranslationsDemo extends AutoDemo with CssView {

  private val (rendered, source) = {
    import io.udash.i18n._
    import io.udash.web.guide.demos.i18n.Translations
    import scalatags.JsDom.all._

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

    implicit val translationProvider: LocalTranslationProvider =
      FrontendTranslationsProvider()
    implicit val lang: Lang = Lang("en")

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
        id := "frontend-translations-demo",
        GuideStyles.frame,
        GuideStyles.useBootstrap
      )(rendered),
      source
    )
}