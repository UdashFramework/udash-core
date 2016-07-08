package io.udash.i18n.bindings

import io.udash._
import io.udash.i18n._
import org.scalajs.dom
import org.scalajs.dom._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import scalatags.JsDom
import scalatags.generic.Modifier

private[i18n] class DynamicTranslationBinding[Key <: TranslationKey](key: Key, translator: (Key) => Future[Translated], placeholder: Option[dom.Element])
                                                                    (implicit ec: ExecutionContext, lang: ReadableProperty[Lang])
  extends Modifier[dom.Element] with StrictLogging {
  override def applyTo(t: dom.Element): Unit = {
    var holder: Element = placeholder.getOrElse(emptyStringNode())
    t.appendChild(holder)

    def rebuild(): Unit = {
      translator(key) onComplete {
        case Success(text) =>
          val newHolder = JsDom.StringFrag(text.string).render.asInstanceOf[dom.Element]
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