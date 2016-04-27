package io.udash.i18n

import io.udash._

import scala.concurrent.ExecutionContext

object LangProperty {
  /** Helper for `LangProperty` instance creation. */
  def apply(lang: Lang)(implicit ec: ExecutionContext): LangProperty =
    Property(lang)
}
