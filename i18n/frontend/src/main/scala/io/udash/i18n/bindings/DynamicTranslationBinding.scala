package io.udash.i18n.bindings

import io.udash._
import io.udash.i18n._
import org.scalajs.dom._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import scalatags.JsDom
import scalatags.generic.Modifier

private[i18n] class DynamicTranslationBinding[Key <: TranslationKey](key: Key, translator: (Key) => Future[Translated], placeholder: Option[Element])
                                                                    (implicit ec: ExecutionContext, lang: ReadableProperty[Lang])
  extends Modifier[Element] with StrictLogging {
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

    lang.listen(_ => rebuild())
    rebuild()
  }
}