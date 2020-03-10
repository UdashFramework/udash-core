package io.udash.i18n

object Utils {
  def getTranslatedString(tr: TranslationKey0)(implicit lang: Lang, provider: TranslationProvider): String =
    tr().value.get.get.string
}
