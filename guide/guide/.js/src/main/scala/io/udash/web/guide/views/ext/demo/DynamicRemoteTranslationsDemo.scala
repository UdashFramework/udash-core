package io.udash.web.guide.views.ext.demo

import io.udash.properties.single.Property
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
    import org.scalajs.dom
    import scalatags.JsDom.all._

    import scala.concurrent.duration.DurationInt

    implicit val translationProvider: RemoteTranslationProvider =
      new RemoteTranslationProvider(
        serverRpc.demos.translations,
        Some(dom.window.localStorage),
        6.hours
      )

    implicit val lang: Property[Lang] = Property(Lang("en"))

    div(
      button(
        Button.btn,
        Button.color(Color.Primary)
      )(id := "enButton", onclick := ((_: dom.Event) => lang.set(Lang("en"))))("EN"), " ",
      button(
        Button.btn,
        Button.color(Color.Primary)
      )(id := "plButton", onclick := ((_: dom.Event) => lang.set(Lang("pl"))))("PL"),
      div(Card.card, Card.body, Background.color(Color.Light), Spacing.margin(
        side = Side.Top,
        size = SpacingSize.Normal
      ))(ul(
        li(
          "auth.loginLabel: ",
          Translations.auth.loginLabel.translatedDynamic()
        ),
        li(
          "auth.passwordLabel: ",
          Translations.auth.passwordLabel.translatedDynamic()
        ),
        li(
          "auth.login.buttonLabel: ",
          Translations.auth.login.buttonLabel.translatedDynamic()
        ),
        li(
          "auth.login.retriesLeft: ",
          Translations.auth.login.retriesLeft(3).translatedDynamic()
        ),
        li(
          "auth.login.retriesLeftOne: ",
          Translations.auth.login.retriesLeftOne.translatedDynamic()
        ),
        li(
          "auth.register.buttonLabel: ",
          Translations.auth.register.buttonLabel.translatedDynamic()
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