package io.udash.i18n.bindings

import com.avsystem.commons._
import io.udash._
import io.udash.bindings.modifiers._
import io.udash.i18n._
import org.scalajs.dom._

import scala.concurrent.Future

private[i18n] final class DynamicTranslationBinding(
  translation: => Future[Translated],
  placeholder: Option[Element],
  rawHtml: Boolean
)(implicit lang: ReadableProperty[Lang]) extends TranslationModifier(translation, placeholder, rawHtml) with Binding {
  override def applyTo(t: Element): Unit = {
    var holder: Seq[Node] = t.appendChild(placeholder.getOrElse(emptyStringNode()))
    (propertyListeners += lang.listen(_ => update(t, holder).foreachNow(holder = _), initUpdate = true)).discard
  }
}