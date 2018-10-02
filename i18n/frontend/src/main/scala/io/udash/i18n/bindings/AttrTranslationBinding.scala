package io.udash.i18n.bindings

import io.udash.bindings.Bindings
import io.udash.bindings.modifiers.Binding
import io.udash.i18n.Translated
import io.udash.logging.CrossLogging
import org.scalajs.dom.Element

import scala.concurrent.Future
import scala.util.{Failure, Success}

private[i18n]
class AttrTranslationBinding(translation: Future[Translated], attr: String)
  extends Binding with Bindings with CrossLogging {

  import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

  override def applyTo(t: Element): Unit =
    if (translation.isCompleted && translation.value.get.isSuccess) {
      t.setAttribute(attr, translation.value.get.get.string)
    } else {
      translation onComplete {
        case Success(text) =>
          t.setAttribute(attr, text.string)
        case Failure(ex) =>
          logger.error(ex.getMessage)
      }
    }
}