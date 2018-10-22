package io.udash.i18n.bindings

import com.avsystem.commons._
import io.udash._
import io.udash.i18n._
import io.udash.logging.CrossLogging
import org.scalajs.dom._
import scalatags.generic.Modifier

import scala.concurrent.Future
import scala.util.{Failure, Success}

private[i18n] class DynamicTranslationBinding[Key <: TranslationKey](key: Key, translator: Key => Future[Translated],
                                                       placeholder: Option[Element])
                                                      (implicit lang: ReadableProperty[Lang])
  extends Modifier[Element] with CrossLogging {

  override def applyTo(t: Element): Unit = {
    var holder: Node = placeholder.getOrElse(emptyStringNode())
    t.appendChild(holder)

    def rebuild(): Unit = {
      translator(key).onCompleteNow {
        case Success(text) =>
          val newHolder = document.createTextNode(text.string)
          t.replaceChild(
            newHolder,
            holder
          )
          holder = newHolder
        case Failure(ex) =>
          logger.error(ex.getMessage)
      }
    }

    lang.listen(_ => rebuild(), initUpdate = true)
  }
}