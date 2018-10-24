package io.udash.i18n

import scala.concurrent.Future

object Utils {
  def getTranslatedString(tr: Future[Translated]): String =
    tr.value.get.get.string
}
