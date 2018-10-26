package io.udash.i18n.bindings

import com.avsystem.commons._
import io.udash.bindings.Bindings
import io.udash.i18n.Translated
import io.udash.logging.CrossLogging
import org.scalajs.dom._
import scalatags.generic.Modifier

import scala.concurrent.Future
import scala.util.{Failure, Success}

private[i18n] class TranslationBinding(translation: Future[Translated], placeholder: Option[Element])
  extends Modifier[Element] with Bindings with CrossLogging {

  override def applyTo(t: Element): Unit =
    if (translation.isCompleted && translation.value.get.isSuccess) {
      t.appendChild(document.createTextNode(translation.value.get.get.string))
    } else {
      val holder: Node = placeholder.getOrElse(emptyStringNode())
      t.appendChild(holder)

      translation.onCompleteNow {
        case Success(text) =>
          t.replaceChild(
            document.createTextNode(text.string),
            holder
          )
        case Failure(ex) =>
          logger.error(ex.getMessage)
      }
    }
}