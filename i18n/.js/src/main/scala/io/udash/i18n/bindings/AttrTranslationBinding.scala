package io.udash.i18n.bindings

import io.udash.bindings.Bindings
import io.udash.i18n.Translated
import io.udash.logging.CrossLogging
import org.scalajs.dom

import scala.concurrent.Future
import scala.util.{Failure, Success}
import scalatags.generic.Modifier

private[i18n]
class AttrTranslationBinding(translation: Future[Translated], attr: String)
  extends Modifier[dom.Element] with Bindings with CrossLogging {

  import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

  override def applyTo(t: dom.Element): Unit =
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