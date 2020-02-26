package io.udash.i18n.bindings

import com.avsystem.commons._
import io.udash.bindings.Bindings
import io.udash.bindings.modifiers._
import io.udash.i18n._
import io.udash.logging.CrossLogging
import org.scalajs.dom._
import scalatags.JsDom.Modifier

import scala.concurrent.Future
import scala.util.{Failure, Success}

private[i18n] final class TranslationBinding(
  translation: Future[Translated],
  placeholder: Option[Element],
  rawHtml: Boolean
) extends Modifier with Bindings with CrossLogging {

  override def applyTo(t: Element): Unit = {
    val holder: Seq[Node] = t.appendChild(placeholder.getOrElse(emptyStringNode()))
    translation.onCompleteNow {
      case Success(Translated(text)) =>
        t.replaceChildren(holder, parseTranslation(rawHtml, text))
      case Failure(ex) =>
        logger.error(ex.getMessage)
    }
  }
}