package io.udash.web.guide.views.ext.demo

import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

object DynamicRemoteTranslationsDemo extends AutoDemo {

  private val (rendered, source) = {
    import io.udash.bootstrap.utils.BootstrapStyles._
    import io.udash.css.CssView._
    import io.udash.i18n._
    import io.udash.web.guide.Context.serverRpc
    import io.udash.web.guide.demos.i18n.Translations
    import org.scalajs.dom.Event
    import org.scalajs.dom.ext.LocalStorage
    import scalatags.JsDom.all._

    import scala.concurrent.duration.DurationInt

    implicit val translationProvider: RemoteTranslationProvider =
      new RemoteTranslationProvider(
        serverRpc.demos.translations,
        Some(LocalStorage),
        6.hours
      )

    implicit val lang: LangProperty = LangProperty(Lang("en"))

    div(
      button(
        Button.btn,
        Button.color(Color.Primary)
      )(id := "enButton", onclick := ((_: Event) => lang.set(Lang("en"))))("EN"), " ",
      button(
        Button.btn,
        Button.color(Color.Primary)
      )(id := "plButton", onclick := ((_: Event) => lang.set(Lang("pl"))))("PL"),
      div(Card.card, Card.body, Background.color(Color.Light), Spacing.margin(
        side = Side.Top,
        size = SpacingSize.Normal
      ))(ul(
        li(
          "auth.loginLabel: ",
          translatedDynamic(Translations.auth.loginLabel)(_.apply())
        ),
        li(
          "auth.passwordLabel: ",
          translatedDynamic(Translations.auth.passwordLabel)(_.apply())
        ),
        li(
          "auth.login.buttonLabel: ",
          translatedDynamic(Translations.auth.login.buttonLabel)(_.apply())
        ),
        li(
          "auth.login.retriesLeft: ",
          translatedDynamic(Translations.auth.login.retriesLeft)(_.apply(3))
        ),
        li(
          "auth.login.retriesLeftOne: ",
          translatedDynamic(Translations.auth.login.retriesLeftOne)(_.apply())
        ),
        li(
          "auth.register.buttonLabel: ",
          translatedDynamic(Translations.auth.register.buttonLabel)(_.apply())
        )
      ))
    )
  }.withSourceCode

  override protected def demoWithSource(): (Modifier, Iterator[String]) = {
    import io.udash.css.CssView._
    (
      div(
        id := "dynamic-rpc-translations-demo",
        GuideStyles.frame,
        GuideStyles.useBootstrap
      )(rendered),
      source.linesIterator
    )
  }
}