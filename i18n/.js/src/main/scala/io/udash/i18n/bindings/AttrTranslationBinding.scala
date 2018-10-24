package io.udash.i18n.bindings

import com.avsystem.commons._
import io.udash.bindings.Bindings
import io.udash.i18n.Translated
import io.udash.logging.CrossLogging
import org.scalajs.dom.Element
import scalatags.JsDom.Modifier

import scala.concurrent.Future
import scala.util.{Failure, Success}

private[i18n] class AttrTranslationBinding(translation: Future[Translated], attr: String)
  extends Modifier with Bindings with CrossLogging {

  override def applyTo(t: Element): Unit =
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