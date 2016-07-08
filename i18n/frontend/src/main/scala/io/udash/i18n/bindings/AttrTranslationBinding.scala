package io.udash.i18n.bindings

import io.udash.StrictLogging
import io.udash.bindings.Bindings
import io.udash.i18n.Translated
import org.scalajs.dom

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import scalatags.generic.Modifier

private[i18n] class AttrTranslationBinding(translation: Future[Translated], attr: String)(implicit ec: ExecutionContext)
  extends Modifier[dom.Element] with Bindings with StrictLogging {
  override def applyTo(t: dom.Element): Unit =
    translation onComplete {
      case Success(text) =>
        t.setAttribute(attr, text.string)
      case Failure(ex) =>
        logger.error(ex.getMessage)
    }
}