package io.udash.guide.demos.i18n

import java.{util => ju}

import io.udash.guide.Implicits._
import io.udash.i18n.{Lang, ResourceBundlesTranslationTemplatesProvider, TranslationRPCEndpoint}

class TranslationServer extends TranslationRPCEndpoint(
  new ResourceBundlesTranslationTemplatesProvider(
    TranslationServer.langs
      .map(lang =>
        Lang(lang) -> TranslationServer.bundlesNames.map(name => ju.ResourceBundle.getBundle(name, new ju.Locale(lang)))
      ).toMap
  )
)

object TranslationServer {
  val langs = Seq("en", "pl")
  val bundlesNames = Seq("demo_translations")
}