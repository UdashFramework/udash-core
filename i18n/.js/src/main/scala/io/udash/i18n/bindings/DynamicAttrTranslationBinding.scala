package io.udash.i18n.bindings

import io.udash._
import io.udash.bindings.modifiers.Binding
import io.udash.i18n.{Lang, Translated, TranslationKey}
import org.scalajs.dom.Element

import scala.concurrent.Future

private[i18n] final class DynamicAttrTranslationBinding[Key <: TranslationKey](
  key: Key, translator: Key => Future[Translated], attr: String
)(implicit lang: ReadableProperty[Lang]) extends AttrTranslationModifier(translator(key), attr) with Binding {

  override def applyTo(t: Element): Unit = {
    propertyListeners += lang.listen(_ => super.applyTo(t), initUpdate = true)
  }
}