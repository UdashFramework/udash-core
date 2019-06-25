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
        id := "frontend-translations-demo",
        GuideStyles.frame,
        GuideStyles.useBootstrap
      )(rendered),
      source.linesIterator
    )
  }
}