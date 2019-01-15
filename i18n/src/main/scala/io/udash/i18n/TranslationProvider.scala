package io.udash.i18n


import scala.concurrent.Future
import scala.util.Try
import scala.util.matching.Regex

trait TranslationProvider {
  import TranslationProvider._

  /** Basing on provided translation key, arguments and language it should created `Future` containing translated text. */
  def translate(key: String, argv: Any*)(implicit lang: Lang): Future[Translated]

  protected def handleMixedPlaceholders(template: String): Unit

  protected def putArgs(template: String, argv: Any*): Translated = {
    val args = argv.map(_.toString).map(Regex.quoteReplacement).lift

    var prevN = -1
    var indexed = false
    var unindexed = false
    val result = argRegex.replaceSomeIn(template, m => {
      val n = Try {
        val i = m.group(1).toInt
        indexed = true
        i
      } getOrElse {
        unindexed = true
        prevN + 1
      }

      if (indexed && unindexed) handleMixedPlaceholders(template)

      prevN = n
      args(n)
    })
    Translated(result)
  }
}

object TranslationProvider {
  val argRegex = """\{(\d*)\}""".r
  val indexedArgRegex = """\{(\d+)\}""".r
  val unindexedArgRegex = """\{\}""".r
}
