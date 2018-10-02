package io.udash.i18n.bindings

import io.udash._
import io.udash.bindings.modifiers.Binding
import io.udash.i18n._
import io.udash.logging.CrossLogging
import org.scalajs.dom._
import scalatags.JsDom

import scala.concurrent.Future
import scala.util.{Failure, Success}

private[i18n]
class DynamicTranslationBinding[Key <: TranslationKey](
  key: Key,
  translator: Key => Future[Translated],
  placeholder: Option[Element]
)(implicit lang: ReadableProperty[Lang]) extends Binding with CrossLogging {

  import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

  override def applyTo(t: Element): Unit = {
    var holder: Node = placeholder.getOrElse(emptyStringNode())
    t.appendChild(holder)

    def rebuild(): Unit = {
      translator(key) onComplete {
        case Success(text) =>
          val newHolder = JsDom.StringFrag(text.string).render
          t.replaceChild(
            newHolder,
            holder
          )
          holder = newHolder
        case Failure(ex) =>
          logger.error(ex.getMessage)
      }
    }

    propertyListeners += lang.listen(_ => rebuild())
    rebuild()
  }
}