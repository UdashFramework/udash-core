package io.udash.selenium.demos.i18n

import java.{util => ju}

import io.udash.i18n._

import scala.concurrent.ExecutionContext

class TranslationServer()(implicit ec: ExecutionContext) extends TranslationRPCEndpoint(
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