package io.udash.i18n.bindings

import io.udash._
import io.udash.i18n.{Lang, Translated, TranslationKey}
import org.scalajs.dom

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import scalatags.generic.Modifier

private[i18n] class DynamicAttrTranslationBinding[Key <: TranslationKey](key: Key, translator: (Key) => Future[Translated], attr: String)
                                                                        (implicit ec: ExecutionContext, lang: ReadableProperty[Lang])
  extends Modifier[dom.Element] with StrictLogging {
  override def applyTo(t: dom.Element): Unit = {
    def rebuild(): Unit = {
      translator(key) onComplete {
        case Success(text) =>
          t.setAttribute(attr, text.string)
        case Failure(ex) =>
          logger.error(ex.getMessage)
      }
    }

    lang.listen(_ => rebuild())
    rebuild()
  }
}