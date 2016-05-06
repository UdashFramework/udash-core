package io.udash.web.guide.views.ext

import io.udash._
import io.udash.web.guide._
import io.udash.web.guide.components.CodeBlock
import io.udash.web.guide.views.References
import io.udash.web.guide.views.ext.demo.{DynamicRemoteTranslationsDemo, FrontendTranslationsDemo, RemoteTranslationsDemo}
import org.scalajs.dom

import scalatags.JsDom

case object I18NExtViewPresenter extends DefaultViewPresenterFactory[I18NExtState.type](() => new I18NExtView)


class I18NExtView extends View {
  import JsDom.all._

  override def getTemplate: dom.Element = div(
    h1("Udash i18n"),
    p(
      "The Udash framework supports internationalization of web applications. ",
      "The Udash i18n plugin provides translations loading from the server-side application via RPC ",
      "and allows locale changes in frontend application without refreshing. "
    ),
    h2("Translation keys"),
    p("If you want to use Udash translations support, you should define ", i("TranslationKeys"), " "),
    CodeBlock(
      s"""import io.udash.i18n._
         |
         |object Translations {
         |  import TranslationKey._
         |
         |  object auth {
         |    val loginLabel = key("auth.loginLabel")
         |    val passwordLabel = key("auth.passwordLabel")
         |
         |    object login {
         |      val buttonLabel = key("auth.login.buttonLabel")
         |      val retriesLeft = key1[Int]("auth.login.retriesLeft")
         |      val retriesLeftOne = key("auth.login.retriesLeftOne")
         |    }
         |
         |    object register {
         |      val buttonLabel = key("auth.register.buttonLabel")
         |    }
         |  }
         |}""".stripMargin
    )(),
    p(
      i("TranslationKey"), " knows the count and types of the arguments. In the above example, ",
      i("retriesLeft"), " key expects one integer as the argument."
    ),
    h2("TranslationProvider"),
    p("When translation keys are defined, we can create ", i("Translated"), " object as follows: "),
    CodeBlock(
      s"""val translated: Future[Translated] = Translations.auth.login.retriesLeft(3)
         |translated onSuccess {
         |  case Translated(text) => println(text)
         |}""".stripMargin
    )(),
    p(
      "This code requires a ", i("TranslationProvider"), " instance to compile. The Udash i18n plugin provides two ",
      i("TranslationProviders"), ": ", i("LocalTranslationProvider"), " and ", i("RemoteTranslationProvider"), ""
    ),
    h3("LocalTranslationProvider"),
    p(
      i("LocalTranslationProvider"), " was prepared for frontend-only applications. It takes a map from ",
      i("Lang"), " to ", i("Bundle"), ". Each bundle provides mapping from translation keys to translation templates."
    ),
    CodeBlock(
      s"""import io.udash.i18n._
         |
         |object FrontendTranslationProvider {
         |  import io.udash.guide.Context._
         |
         |  private val translations = Map(
         |    Lang("en") -> Bundle(BundleHash("enHash"), Map(
         |      "auth.loginLabel" -> "Username",
         |      "auth.passwordLabel" -> "Password",
         |      "auth.login.buttonLabel" -> "Sign in",
         |      "auth.login.retriesLeft" -> "{} retries left",
         |      "auth.login.retriesLeftOne" -> "1 retry left",
         |      "auth.register.buttonLabel" -> "Sign up"
         |    ))
         |  )
         |
         |  def apply(): LocalTranslationProvider =
         |    new LocalTranslationProvider(translations)
         |}
         |
         |object FrontendTranslationsDemo {
         |  import scalatags.JsDom.all._
         |  import scalacss.ScalatagsCss._
         |
         |  def apply(): dom.Element = {
         |    import io.udash.guide.Context._
         |    implicit val translationProvider = FrontendTranslationProvider()
         |    implicit val lang = Lang("en")
         |    div(
         |      ul(
         |        li(
         |          "auth.loginLabel: ",
         |          translated(Translations.auth.loginLabel())
         |        ),
         |        li(
         |          "auth.passwordLabel: ",
         |          translated(Translations.auth.passwordLabel())
         |        ),
         |        li(
         |          "auth.login.buttonLabel: ",
         |          translated(Translations.auth.login.buttonLabel())
         |        ),
         |        li(
         |          "auth.login.retriesLeft: ",
         |          translated(Translations.auth.login.retriesLeft(3))
         |        ),
         |        li(
         |          "auth.login.retriesLeftOne: ",
         |          translated(Translations.auth.login.retriesLeftOne())
         |        ),
         |        li(
         |          "auth.register.buttonLabel: ",
         |          translated(Translations.auth.register.buttonLabel())
         |        )
         |      )
         |    ).render
         |  }
         |}""".stripMargin
    )(),
    p(
      "Take a look at the example below. As you can see in the code sample, it uses ",
      i("translated"), " method to bind translation into DOM hierarchy. "
    ),
    FrontendTranslationsDemo(),
    h3("RemoteTranslationProvider"),
    p(
      "If your application is using the Udash RPC system, you can provide translations from the server side application. ",
      i("RemoteTranslationProvider"), " takes ", i("RemoteTranslationRPC"), " as constructor argument. It allows the frontend application ",
      "to ask the server application for the translation templates."
    ),
    h4("RemoteTranslationRPC implementation"),
    p("Let's start with ", i("RemoteTranslationRPC"), " implementation in the server application. Add the following method in your server RPC interface: "),
    CodeBlock(
      s"""import io.udash.i18n._
          |
          |@RPC
          |trait DemosServerRPC {
          |  def translations(): RemoteTranslationRPC
          |}""".stripMargin
    )(),
    p(
      "The Udash i18n plugin makes ", i("RemoteTranslationRPC"), " easier, because it provides ",
      i("TranslationRPCEndpoint"), " and ", i("ResourceBundlesTranslationTemplatesProvider"), " classes."
    ),
    CodeBlock(
      s"""import io.udash.i18n._
          |import java.{util => ju}
          |
          |class TranslationServer extends TranslationRPCEndpoint(
          |  new ResourceBundlesTranslationTemplatesProvider(
          |    TranslationServer.langs
          |      .map(lang =>
          |        Lang(lang) -> TranslationServer.bundlesNames.map(name =>
          |          ju.ResourceBundle.getBundle(name, new ju.Locale(lang))
          |        )
          |      ).toMap
          |  )
          |)
          |
          |object TranslationServer {
          |  val langs = Seq("en", "pl")
          |  val bundlesNames = Seq("demo_translations")
          |}""".stripMargin
    )(),
    p(
      i("ResourceBundlesTranslationTemplatesProvider"), " expects ", i("Map[Lang, Seq[ju.ResourceBundle]]"), " as a constructor argument, whereas ",
      i("TranslationRPCEndpoint"), " takes ", i("ResourceBundlesTranslationTemplatesProvider"), " instance. ",
      "The presented implementation will serve translation templates from bundles from server application resources."
    ),
    h4("Frontend usage"),
    p(
      "Now it is possible to load translations into the frontend application using ",
      i("RemoteTranslationProvider"), ". This provider loads required translation templates from server ",
      "and caches them in provided storage. In the example below it is a browser local storage which keeps cached values for 6 hours."
    ),
    CodeBlock(
      s"""import io.udash.i18n._
         |
         |object RemoteTranslationsDemo {
         |  import scalatags.JsDom.all._
         |  import scalacss.ScalatagsCss._
         |
         |  def apply(): dom.Element = {
         |    import io.udash.guide.Context._
         |    implicit val translationProvider =
         |      new RemoteTranslationProvider(serverRpc.translations(),
         |      Some(LocalStorage), 6 hours
         |    )
         |    implicit val lang = Lang("pl")
         |    div(
         |      ul(
         |        li(
         |          "auth.loginLabel: ",
         |          translated(Translations.auth.loginLabel())
         |        ),
         |        li(
         |          "auth.passwordLabel: ",
         |          translated(Translations.auth.passwordLabel())
         |        ),
         |        li(
         |          "auth.login.buttonLabel: ",
         |          translated(Translations.auth.login.buttonLabel())
         |        ),
         |        li(
         |          "auth.login.retriesLeft: ",
         |          translated(Translations.auth.login.retriesLeft(3))
         |        ),
         |        li(
         |          "auth.login.retriesLeftOne: ",
         |          translated(Translations.auth.login.retriesLeftOne())
         |        ),
         |        li(
         |          "auth.register.buttonLabel: ",
         |          translated(Translations.auth.register.buttonLabel())
         |        )
         |      )
         |    ).render
         |  }
         |}""".stripMargin
    )(),
    p("Take a look at the example below."),
    RemoteTranslationsDemo(),
    h2("Translations binding"),
    p(
      "All translations are resolved asynchronously, so they cannot be statically added into DOM hierarchy. The Udash i18n plugin ",
      "provides four methods for translations binding. These methods are divided into two groups: static and dynamic."
    ),
    h3("Static binding"),
    p(
      "Static binding takes ", i("Future[Translated]"),
      " as an argument and when it completes it puts translated string into DOM hierarchy.",
      ul(
        li(i("translated"), " - binds translated string in the DOM element."),
        li(i("translatedAttr"), " - binds translated string in the DOM element attribute.")
      )
    ),
    h3("Dynamic binding"),
    p(
      "Dynamic binding is able to update translation after a change of ", i("LangProperty"), ". These methods take ",
      "the following arguments: a translation key, a translator which applies arguments to translation and the lang property.",
      ul(
        li(i("translatedDynamic"), " - binds translated string in the DOM element and updates it when the application language changes."),
        li(i("translatedAttrDynamic"), " - binds translated string in the DOM element attribute and updates it when the application language changes.")
      ),
      "Take a look at the example below: "
    ),
    CodeBlock(
      s"""import io.udash.i18n._
         |
         |object RemoteTranslationsDemo {
         |  import scalatags.JsDom.all._
         |  import scalacss.ScalatagsCss._
         |
         |  def apply(): dom.Element = {
         |    import io.udash.guide.Context._
         |    implicit val translationProvider =
         |      new RemoteTranslationProvider(serverRpc.translations(),
         |      Some(LocalStorage), 6 hours
         |    )
         |    implicit val lang = LangProperty(Lang("en"))
         |    div(
         |      button(onclick := ((_: Event) => lang.set(Lang("en"))))("EN"),
         |      button(onclick := ((_: Event) => lang.set(Lang("pl"))))("PL"),
         |      ul(
         |        li(
         |          "auth.loginLabel: ",
         |          translatedDynamic(Translations.auth.loginLabel)(_.apply()))
         |        ),
         |        li(
         |          "auth.passwordLabel: ",
         |          translatedDynamic(Translations.auth.passwordLabel)(_.apply()))
         |        ),
         |        li(
         |          "auth.login.buttonLabel: ",
         |          translatedDynamic(Translations.auth.login.buttonLabel)(_.apply()))
         |        ),
         |        li(
         |          "auth.login.retriesLeft: ",
         |          translatedDynamic(Translations.auth.login.retriesLeft)(_.apply(3)))
         |        ),
         |        li(
         |          "auth.login.retriesLeftOne: ",
         |          translatedDynamic(Translations.auth.login.retriesLeftOne)(_.apply()))
         |        ),
         |        li(
         |          "auth.register.buttonLabel: ",
         |          translatedDynamic(Translations.auth.register.buttonLabel)(_.apply()))
         |        )
         |      )
         |    ).render
         |  }
         |}""".stripMargin
    )(),
    p("Now you can change the translation language without redrawing the whole component, as presented in the following live example."),
    DynamicRemoteTranslationsDemo()
  ).render

  override def renderChild(view: View): Unit = {}
}