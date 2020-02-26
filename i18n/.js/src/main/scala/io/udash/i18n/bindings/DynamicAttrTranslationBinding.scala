package io.udash.i18n.bindings

import com.avsystem.commons._
import io.udash._
import io.udash.bindings.modifiers.Binding
import io.udash.i18n.{Lang, Translated, TranslationKey}
import io.udash.logging.CrossLogging
import org.scalajs.dom.Element

import scala.concurrent.Future
import scala.util.{Failure, Success}

private[i18n] final class DynamicAttrTranslationBinding[Key <: TranslationKey](
  key: Key, translator: Key => Future[Translated], attr: String
)(implicit lang: ReadableProperty[Lang]) extends Binding with CrossLogging {

  override def applyTo(t: Element): Unit = {
    def rebuild(): Unit = {
      translator(key).onCompleteNow {
        case Success(text) =>
          t.setAttribute(attr, text.string)
        case Failure(ex) =>
          logger.error(ex.getMessage)
      }
    }

    propertyListeners += lang.listen(_ => rebuild(), initUpdate = true)
  }
}