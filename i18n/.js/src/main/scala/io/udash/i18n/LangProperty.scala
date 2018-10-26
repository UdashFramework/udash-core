package io.udash.i18n

import io.udash._

object LangProperty {
  /** Helper for `LangProperty` instance creation. */
  def apply(lang: Lang): LangProperty =
    Property(lang)
}
