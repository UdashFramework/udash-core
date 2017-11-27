package io.udash.i18n.bindings

import io.udash.StrictLogging
import io.udash.bindings.Bindings
import io.udash.i18n.Translated
import org.scalajs.dom._

import scala.concurrent.Future
import scala.util.{Failure, Success}
import scalatags.JsDom
import scalatags.generic.Modifier

private[i18n]
class TranslationBinding(translation: Future[Translated], placeholder: Option[Element])
  extends Modifier[Element] with Bindings with StrictLogging {

  import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

  override def applyTo(t: Element): Unit =
    if (translation.isCompleted && translation.value.get.isSuccess) {
      t.appendChild(JsDom.StringFrag(translation.value.get.get.string).render)
    } else {
      val holder: Node = placeholder.getOrElse(emptyStringNode())
      t.appendChild(holder)

      translation onComplete {
        case Success(text) =>
          t.replaceChild(
            JsDom.StringFrag(text.string).render,
            holder
          )
        case Failure(ex) =>
          logger.error(ex.getMessage)
      }
    }
}