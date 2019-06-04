package io.udash.web.guide.views.ext.demo

import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.bootstrap.utils.BootstrapStyles.Color
import io.udash.css.CssView
import io.udash.i18n._
import io.udash.web.guide.components.BootstrapUtils
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.demos.i18n.Translations
import io.udash.web.guide.styles.partials.GuideStyles
import org.scalajs.dom.Event
import org.scalajs.dom.ext.LocalStorage
import scalatags.JsDom

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

object DynamicRemoteTranslationsDemo extends AutoDemo with CssView {
  import scalatags.JsDom.all._

  private val (rendered, source) = {
    import io.udash.web.guide.Context._

    implicit val translationProvider = new RemoteTranslationProvider(
      serverRpc.demos.translations,
      Some(LocalStorage),
      6 hours
    )
    implicit val lang = LangProperty(Lang("en"))

    div(
      button(
        BootstrapStyles.Button.btn,
        BootstrapStyles.Button.color(Color.Primary)
      )(id := "enButton", onclick := ((_: Event) => lang.set(Lang("en"))))("EN"), " ",
      button(
        BootstrapStyles.Button.btn,
        BootstrapStyles.Button.color(Color.Primary)
      )(id := "plButton", onclick := ((_: Event) => lang.set(Lang("pl"))))("PL"),
      div(BootstrapUtils.wellStyles, BootstrapStyles.Spacing.margin(
        side = BootstrapStyles.Side.Top,
        size = BootstrapStyles.SpacingSize.Normal
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

  override protected def demoWithSource(): (JsDom.all.Modifier, Iterator[String]) = {
    (div(id := "dynamic-rpc-translations-demo", GuideStyles.frame, GuideStyles.useBootstrap)(
      rendered), source.lines.drop(1))
  }
}