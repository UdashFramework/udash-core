package io.udash.i18n

import io.udash.StrictLogging

trait FrontendTranslationProvider extends TranslationProvider with StrictLogging {
  protected def handleMixedPlaceholders(template: String): Unit =
    logger.warn(s"""Indexed and unindexed placeholders in "$template"!""")
}
