package io.udash.i18n


import scala.concurrent.{ExecutionContext, Future}

/**
  * TranslationProvider dedicated to frontend-only applications.
  *
  * @param bundles `Bundle`s of translations for each language.
  * @param missingTranslationError This text will be used in place of missing translations.
  */
class LocalTranslationProvider(bundles: Map[Lang, Bundle], missingTranslationError: String = "Missing translation")
                              (implicit ec: ExecutionContext) extends FrontendTranslationProvider {
  def translate(key: String, argv: Any*)(implicit lang: Lang): Future[Translated] = Future {
    putArgs(bundles(lang).translations.getOrElse(key, missingTranslationError), argv: _*)
  }
}
