package io.udash.i18n.bindings

import com.avsystem.commons._
import io.udash._
import io.udash.i18n.{Lang, Translated, TranslationKey}
import io.udash.logging.CrossLogging
import org.scalajs.dom
import scalatags.generic.Modifier

import scala.concurrent.Future
import scala.util.{Failure, Success}

private[i18n] class DynamicAttrTranslationBinding[Key <: TranslationKey](
  key: Key, translator: Key => Future[Translated], attr: String
)(implicit lang: ReadableProperty[Lang]) extends Modifier[dom.Element] with CrossLogging {

  override def applyTo(t: dom.Element): Unit = {
    def rebuild(): Unit = {
      translator(key).onCompleteNow {
        case Success(text) =>
          t.setAttribute(attr, text.string)
        case Failure(ex) =>
          logger.error(ex.getMessage)
      }
    }

    lang.listen(_ => rebuild(), initUpdate = true)
  }
}