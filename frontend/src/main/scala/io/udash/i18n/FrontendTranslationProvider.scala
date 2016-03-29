package io.udash.i18n

import io.udash.utils.Logger

trait FrontendTranslationProvider extends TranslationProvider {
  protected def handleMixedPlaceholders(template: String): Unit =
    Logger.warn(s"""Indexed and unindexed placeholders in "$template"!""")
}
