package io.udash.i18n

import io.udash.logging.CrossLogging

trait FrontendTranslationProvider extends TranslationProvider with CrossLogging {
  protected def handleMixedPlaceholders(template: String): Unit =
    logger.warn(s"""Indexed and unindexed placeholders in "$template"!""")
}
