package io.udash.bindings

import io.udash.bindings.modifiers._
import io.udash.properties._
import io.udash.properties.seq.{Patch, ReadableSeqProperty}
import io.udash.properties.single.ReadableProperty
import org.scalajs.dom
import org.scalajs.dom._

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js
import scalatags.JsDom
import scalatags.generic.{Attr, AttrPair, AttrValue, Modifier}

trait Bindings {
  val Checkbox      = io.udash.bindings.Checkbox
  val CheckButtons  = io.udash.bindings.CheckButtons
  val NumberInput   = io.udash.bindings.NumberInput
  val PasswordInput = io.udash.bindings.PasswordInput
  val RadioButtons  = io.udash.bindings.RadioButtons
  val Select        = io.udash.bindings.Select
  val TextArea      = io.udash.bindings.TextArea
  val TextInput     = io.udash.bindings.TextInput

  /** Creates empty text node, which is useful as placeholder. */
  def emptyStringNode(): dom.Element =
    JsDom.StringFrag("").render.asInstanceOf[dom.Element]

  /**
    * Use it to bind value of property into DOM structure. Value of the property will be rendered as text node. (Using .toString method.)
    * If property value is null, empty text node will be added.
    * @param property Property to bind.
    * @return Modifier for bound property.
    */
  def bind[T](property: ReadableProperty[T]) =
    new SimplePropertyModifier[T](property, true)

  /**
    * Use it to bind property into DOM structure, given `builder` will be used to generate DOM element on every value change.
    * If property value is null, empty text node will be added as placeholder.
    * @param property Property to bind.
    * @param builder Element builder which will be used to create HTML element.
    * @return Modifier for bounded property.
    */
  def produce[T](property: ReadableProperty[T])(builder: T => Element) =
    new PropertyModifier[T](property, builder, true)

  /**
    * Use it to bind property into DOM structure, given `builder` will be used to generate DOM element on every value change.
    * @param property Property to bind.
    * @param builder Element builder which will be used to create HTML element.
    * @param checkNull If it is true, then null value of property will result in rendering empty text node.
    *                  If it is false, then null value has to be handled by builder.
    * @return Modifier for bounded property.
    */
  def produce[T](property: ReadableProperty[T], checkNull: Boolean)(builder: T => Element) =
    new PropertyModifier[T](property, builder, checkNull)

  /**
    * Use it to bind sequence property into DOM structure, given `builder` will be used to generate DOM element on every value change.
    * Notice that on every property change, whole element representing property will be rendered again.
    * @param property Property to bind.
    * @param builder Element builder which will be used to create HTML element. Seq passed to the builder can not be null.
    * @return Modifier for bounded property.
    */
  def produce[T](property: ReadableSeqProperty[T, _ <: ReadableProperty[T]])(builder: Seq[T] => Element) =
    new SeqAsValueModifier[T](property, builder)

  /**
    * Use it to bind sequence property into DOM structure, given `initBuilder` will be used to generate DOM element at start.
    * Then it listens to structure change and calls `elementsUpdater` to handle each structure change. <br/>
    * <b>Note:</b> This will handle only structure changes, if you want to handle concrete subproperties value changes, you should use
    * another binding method inside `initBuilder` and `elementsUpdater`.
    * @param property Property to bind.
    * @param initBuilder Element builder which will be used to create initial HTML element.
    * @param elementsUpdater Function used to update element basing on patch.
    * @return Modifier for bounded property.
    */
  def produce[T, E <: ReadableProperty[T]](property: ReadableSeqProperty[T, E],
                                           initBuilder: Seq[E] => Element,
                                           elementsUpdater: (Patch[E], Element) => Any) =
    new SeqAsValuePatchingModifier[T, E](property, initBuilder, elementsUpdater)

  /**
    * Use it to bind sequence property into DOM structure. This method cares about adding new elements which appears in
    * sequence and also removes those which were removed. You only need to provide builder which is used to
    * create HTML element for each sequence member. <br/>
    * <b>Note:</b> This will handle only structure changes, if you want to handle concrete subproperties changes, you should use
    * another binding method inside `builder`.
    * @param property Property to bind.
    * @param builder Builder which is used for every element.
    * @return Modifier for repeat logic.
    */
  def repeat[T, E <: ReadableProperty[T]](property: ReadableSeqProperty[T, E])(builder: (E) => Element) =
    new SeqPropertyModifier[T, E](property, builder)

  /**
    * Use in order to add validation logic over property. As this modifier listens on property validation results, user is able
    * to customize what HTML elements should be shown.
    * @param property Property to bind.
    * @param initBuilder Builder which is called when validation process is started. It will also give you an access to future of
    *                    validation results.
    * @param completeBuilder Builder which is called when validation process is completed. It will give an access to validation results.
    * @param errorBuilder Builder which is called, when validation process fails.
    * @return Modifier for validation logic.
    */
  def bindValidation[A](property: ReadableProperty[A],
                        initBuilder: Future[ValidationResult] => Element,
                        completeBuilder: ValidationResult => Element,
                        errorBuilder: Throwable => Element)(implicit ec: ExecutionContext) =
    new ValidationValueModifier(property, initBuilder, completeBuilder, errorBuilder)

  /**
    * Use it to update DOM elements, on every `property` change.
    * @param property Property to listen.
    * @param updater Element attribute updater.
    * @return Modifier for bounded property.
    */
  def bindAttribute[T](property: ReadableProperty[T])(updater: (T, Element) => Any) =
    new AttrModifier[T](property, updater)

  implicit class ModifierExt(attr: Attr) {
    /** Use this to bind value which is nullable. If the value is null, attribute will be removed. */
    def :?=[B, T](value: T)(implicit ev: AttrValue[B, T]): Modifier[B] =
      if (value == null) new EmptyModifier[B] else AttrPair(attr, value, ev)

    /**
      * Use this to add more events listeners to an attribute (:= always overrides previous binding).
      * If callback returns true, other listeners which are queued will not be invoked.
      * If callback returns false, next callback in the queue will be invoked.
      */
    def :+=[T <: Event](callback: (T) => Boolean): Modifier[dom.Element] = {
      AttrPair(attr, callback, new AttrValue[dom.Element, (T) => Boolean] {
        override def apply(el: Element, attr: Attr, callback: (T) => Boolean): Unit = {
          val dyn: js.Dynamic = el.asInstanceOf[js.Dynamic]
          val existingCallbacks: js.Function1[T, Boolean] = dyn.selectDynamic(attr.name).asInstanceOf[js.Function1[T, Boolean]]
          if (existingCallbacks == null) {
            dyn.updateDynamic(attr.name)((e: T) => {
              if (callback(e)) e.preventDefault()
            })
          } else {
            dyn.updateDynamic(attr.name)((e: T) => {
              val preventDefault = callback(e)
              if (preventDefault) e.preventDefault()
              else existingCallbacks(e)
            })
          }
        }
      })
    }
  }
}
