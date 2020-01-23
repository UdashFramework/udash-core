package io.udash.i18n.bindings

import com.avsystem.commons._
import io.udash._
import io.udash.bindings.modifiers._
import io.udash.i18n._
import io.udash.logging.CrossLogging
import org.scalajs.dom._

import scala.concurrent.Future
import scala.util.{Failure, Success}

private[i18n] final class DynamicTranslationBinding[Key <: TranslationKey](
  key: Key,
  translator: Key => Future[Translated],
  placeholder: Option[Element],
  rawHtml: Boolean
)(implicit lang: ReadableProperty[Lang]) extends Binding with CrossLogging {
  override def applyTo(t: Element): Unit = {
    var holder: Seq[Node] = t.appendChild(placeholder.getOrElse(emptyStringNode()))

    propertyListeners += lang.listen(_ =>
      translator(key) onCompleteNow {
        case Success(Translated(text)) =>
          val newHolder: Seq[Node] = parseTranslation(rawHtml, text)
          t.replaceChildren(holder, newHolder)
          holder = newHolder
        case Failure(ex) =>
          logger.error(ex.getMessage)
      }
    , initUpdate = true)
  }
}