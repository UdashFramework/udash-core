package io.udash.i18n.bindings

import com.avsystem.commons._
import io.udash.bindings.Bindings
import io.udash.i18n.Translated
import io.udash.logging.CrossLogging
import org.scalajs.dom
import scalatags.generic.Modifier

import scala.concurrent.Future
import scala.util.{Failure, Success}

private[i18n] class AttrTranslationBinding(translation: Future[Translated], attr: String)
  extends Modifier[dom.Element] with Bindings with CrossLogging {

  override def applyTo(t: dom.Element): Unit =
    if (translation.isCompleted && translation.value.get.isSuccess) {
      t.setAttribute(attr, translation.value.get.get.string)
    } else {
      translation.onCompleteNow {
        case Success(text) =>
          t.setAttribute(attr, text.string)
        case Failure(ex) =>
          logger.error(ex.getMessage)
      }
    }
}