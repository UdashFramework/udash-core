package io.udash.web.guide.demos.i18n

import io.udash.i18n.{Lang, ResourceBundlesTranslationTemplatesProvider, TranslationRPCEndpoint}
import io.udash.web.Implicits.*

import java.util as ju

class TranslationServer extends TranslationRPCEndpoint(
  new ResourceBundlesTranslationTemplatesProvider(
    TranslationServer.langs
      .map(lang =>
        Lang(lang) -> TranslationServer.bundlesNames.map(name => ju.ResourceBundle.getBundle(name, new ju.Locale.Builder().setLanguage(lang).build()))
      ).toMap
  )
)

object TranslationServer {
  val langs = Seq("en", "pl")
  val bundlesNames = Seq("demo_translations")
}