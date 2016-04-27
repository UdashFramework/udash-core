package io.udash.i18n.bindings

import io.udash.bindings.Bindings
import io.udash.i18n.Translated
import io.udash.utils.Logger
import org.scalajs.dom
import org.scalajs.dom._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import scalatags.JsDom
import scalatags.generic.Modifier

private[i18n] class AttrTranslationBinding(translation: Future[Translated], attr: String)
                                          (implicit ec: ExecutionContext) extends Modifier[dom.Element] with Bindings {
  override def applyTo(t: dom.Element): Unit =
    translation onComplete {
      case Success(text) =>
        t.setAttribute(attr, text.string)
      case Failure(ex) =>
        Logger.error(ex.getMessage)
    }
}