package io.udash

import io.udash.i18n.bindings.{AttrTranslationBinding, DynamicAttrTranslationBinding, DynamicTranslationBinding, TranslationBinding}
import org.scalajs.dom.Element

import scala.concurrent.Future
import scalatags.generic.Modifier

package object i18n {
  type LangProperty = Property[Lang]

  implicit def langFromProperty(implicit property: LangProperty): Lang =
    property.get

  /**
    * Binds translated string in DOM element.
    * @param translation Future containing translated string or error.
    * @param placeholder Placeholder, if `None` passed it will be empty text node.
    */
  def translated(translation: Future[Translated], placeholder: Option[Element] = None): Modifier[Element] =
    new TranslationBinding(translation, placeholder)

  /**
    * Binds translated string in DOM element and updates it when application language changes.
    * @param key TranslationKey which will be used in order to get text.
    * @param translator Should apply any needed arguments to TranslationKey and create `Future[Translated]`.
    * @param placeholder Placeholder, if `None` passed it will be empty text node.
    */
  def translatedDynamic[Key <: TranslationKey](key: Key, placeholder: Option[Element] = None)(translator: Key => Future[Translated])
                                       (implicit lang: LangProperty): Modifier[Element] =
    new DynamicTranslationBinding(key, translator, placeholder)

  /**
    * Binds translated string in DOM element attribute.
    * @param translation Future containing translated string or error.
    * @param attr Attribute name which gonna be updated when `translation` text become ready.
    */
  def translatedAttr(translation: Future[Translated], attr: String): Modifier[Element] =
    new AttrTranslationBinding(translation, attr)

  /**
    * Binds translated string in DOM element attribute and updates it when application language changes.
    * @param key TranslationKey which will be used in order to get text.
    * @param translator Should apply any needed arguments to TranslationKey and create `Future[Translated]`.
    * @param attr Attribute name which gonna be updated when `translation` text become ready.
    */
  def translatedAttrDynamic[Key <: TranslationKey](key: Key, attr: String)(translator: Key => Future[Translated])
                                                  (implicit lang: LangProperty): Modifier[Element] =
    new DynamicAttrTranslationBinding(key, translator, attr)
}
