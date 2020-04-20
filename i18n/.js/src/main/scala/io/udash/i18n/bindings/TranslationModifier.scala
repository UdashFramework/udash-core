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

private[i18n] class TranslationModifier(
  translation: => Future[Translated],
  placeholder: Option[Element],
  rawHtml: Boolean
) extends Modifier with CrossLogging {

  protected final def update(t: Element, holder: Seq[Node]): Future[Seq[Node]] = {
    translation.transformNow {
      case Success(Translated(text)) =>
        val newHolder: Seq[Node] = parseTranslation(rawHtml, text)
        t.replaceChildren(holder, newHolder)
        Success(newHolder)
      case Failure(ex) =>
        logger.error(ex.getMessage)
        Success(holder)
    }
  }

  override def applyTo(t: Element): Unit = {
    val holder: Seq[Node] = Seq(t.appendChild(placeholder.getOrElse(Bindings.emptyStringNode())))
    update(t, holder).discard
  }
}