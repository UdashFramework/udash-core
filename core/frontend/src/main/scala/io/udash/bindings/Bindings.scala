package io.udash.bindings

import io.udash._
import io.udash.bindings.Bindings.{AttrOps, AttrPairOps, PropertyOps}
import io.udash.bindings.modifiers.{Binding, _}
import io.udash.properties.ImmutableValue
import io.udash.properties.seq.{Patch, ReadableSeqProperty}
import io.udash.properties.single.ReadableProperty
import org.scalajs.dom._

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js
import scalatags.JsDom
import scalatags.JsDom.all.{Attr => _}
import scalatags.generic.{Attr, AttrPair, AttrValue, Modifier}

trait Bindings {
  implicit val allowFileTpe: ImmutableValue[File] = null

  val Checkbox      = io.udash.bindings.Checkbox
  val CheckButtons  = io.udash.bindings.CheckButtons
  val FileInput     = io.udash.bindings.FileInput
  val NumberInput   = io.udash.bindings.NumberInput
  val PasswordInput = io.udash.bindings.PasswordInput
  val RadioButtons  = io.udash.bindings.RadioButtons
  val Select        = io.udash.bindings.Select
  val TextArea      = io.udash.bindings.TextArea
  val TextInput     = io.udash.bindings.TextInput

  implicit def seqFromNode(el: Node): Seq[Node] = Seq(el)
  implicit def seqFromElement(el: Element): Seq[Element] = Seq(el)

  /** Creates empty text node, which is useful as placeholder. */
  def emptyStringNode(): Node =
    JsDom.StringFrag("").render

  /**
    * Use it to bind value of property into DOM structure. Value of the property will be rendered as text node. (Using .toString method.)
    * If property value is null, empty text node will be added.
    *
    * @param property Property to bind.
    * @return Modifier for bound property.
    */
  def bind[T](property: ReadableProperty[T]): Binding =
    new SimplePropertyModifier[T](property, true)

  /**
    * Shows provided DOM elements only if property value is `true`.
    *
    * @param property Property to check.
    * @param elements  Elements to show if property value is `true`.
    * @return Modifier for bounded property.
    */
  def showIf(property: ReadableProperty[Boolean])(elements: Seq[Node]): Binding =
    showIfElse(property)(elements, Seq.empty)

  /**
    * Shows provided DOM elements only if property value is `true`.
    *
    * @param property Property to check.
    * @param elements  Elements to show if property value is `true`.
    * @param customElementsReplace Takes root element, old children and new children. It should return `true`, if it did not replace elements in DOM.
    * @return Modifier for bounded property.
    */
  def showIf(property: ReadableProperty[Boolean], customElementsReplace: DOMManipulator#ReplaceMethod)
            (elements: Seq[Node]): Binding =
    showIfElse(property, customElementsReplace)(elements, Seq.empty)

  /**
    * Switches provided DOM elements depending on property value.
    *
    * @param property Property to check.
    * @param elements  Elements to show if property value is `true`.
    * @param elseElements Elements to show if property value is `false`.
    * @return Modifier for bounded property.
    */
  def showIfElse(property: ReadableProperty[Boolean])(elements: Seq[Node], elseElements: Seq[Node]): Binding =
    showIfElse(property, DOMManipulator.defaultElementReplace)(elements, elseElements)

  /**
    * Switches provided DOM elements depending on property value.
    *
    * @param property Property to check.
    * @param elements  Elements to show if property value is `true`.
    * @param elseElements Elements to show if property value is `false`.
    * @param customElementsReplace Takes root element, old children and new children. It should return `true`, if it did not replace elements in DOM.
    * @return Modifier for bounded property.
    */
  def showIfElse(property: ReadableProperty[Boolean], customElementsReplace: DOMManipulator#ReplaceMethod)
                (elements: Seq[Node], elseElements: Seq[Node]): Binding =
    new PropertyModifier[Boolean](
      property,
      (show: Boolean, _) => if (show) elements else elseElements,
      true, customElementsReplace
    )

  /**
    * Use it to bind property into DOM structure, given `builder` will be used to generate DOM element on every value change.
    * If property value is null, empty text node will be added as placeholder.
    *
    * @param property  Property to bind.
    * @param builder   Element builder which will be used to create HTML element.
    * @param checkNull If it is true, then null value of property will result in rendering empty text node.
    *                  If it is false, then null value has to be handled by builder.
    * @return Modifier for bounded property.
    */
  def produce[T](property: ReadableProperty[T], checkNull: Boolean = true)(builder: T => Seq[Node]): Binding =
    new PropertyModifier[T](property, builder, checkNull)

  /**
    * Use it to bind property into DOM structure, given `builder` will be used to generate DOM element on every value change.
    * If property value is null, empty text node will be added as placeholder.<br/><br/>
    *
    * The builder takes nested bindings interceptor - it should be used if you want to create another binding inside
    * this builder. This prevents memory leaks by killing nested bindings on property change. <br/><br/>
    *
    * For example:
    * <pre>
    *   produceWithNested(property) { case (data, nested) =>
    *     div(data,
    *       nested(produce(anotherProperty) { innerData => span(innerData).render })
    *     ).render
    *   }
    * </pre>
    *
    * @param property              Property to bind.
    * @param builder               Element builder which will be used to create HTML element.
    * @param checkNull             If it is true, then null value of property will result in rendering empty text node.
    *                              If it is false, then null value has to be handled by builder.
    * @return Modifier for bounded property.
    */
  def produceWithNested[T](property: ReadableProperty[T], checkNull: Boolean = true)
                          (builder: (T, Binding => Binding) => Seq[Node]): Binding =
    new PropertyModifier[T](property, builder, checkNull, DOMManipulator.defaultElementReplace)

  /**
    * Use it to bind property into DOM structure, given `builder` will be used to generate DOM element on every value change.
    * If property value is null, empty text node will be added as placeholder.<br/><br/>
    *
    * The builder takes nested bindings interceptor - it should be used if you want to create another binding inside
    * this builder. This prevents memory leaks by killing nested bindings on property change. <br/><br/>
    *
    * For example:
    * <pre>
    *   produceWithNested(property) { case (data, nested) =>
    *     div(data,
    *       nested(produce(anotherProperty) { innerData => span(innerData).render })
    *     ).render
    *   }
    * </pre>
    *
    * @param property              Property to bind.
    * @param builder               Element builder which will be used to create HTML element.
    * @param checkNull             If it is true, then null value of property will result in rendering empty text node.
    *                              If it is false, then null value has to be handled by builder.
    * @param customElementsReplace Takes root element, old children and new children. It should return `true`, if it did not replace elements in DOM.
    * @return Modifier for bounded property.
    */
  def produceWithNested[T](property: ReadableProperty[T], customElementsReplace: DOMManipulator#ReplaceMethod, checkNull: Boolean)
                          (builder: (T, Binding => Binding) => Seq[Node]): Binding =
    new PropertyModifier[T](property, builder, checkNull, customElementsReplace)

  /**
    * Use it to bind sequence property into DOM structure, given `builder` will be used to generate DOM element on every value change.
    * Notice that on every property change, whole element representing property will be rendered again.
    *
    * @param property              Property to bind.
    * @param builder               Element builder which will be used to create HTML element. Seq passed to the builder can not be null.
    * @return Modifier for bounded property.
    */
  def produce[T](property: ReadableSeqProperty[T, _ <: ReadableProperty[T]])
                (builder: Seq[T] => Seq[Node]): Binding =
    new SeqAsValueModifier[T](property, builder, DOMManipulator.defaultElementReplace)

  /**
    * Use it to bind sequence property into DOM structure, given `builder` will be used to generate DOM element on every value change.
    * Notice that on every property change, whole element representing property will be rendered again.
    *
    * @param property              Property to bind.
    * @param builder               Element builder which will be used to create HTML element. Seq passed to the builder can not be null.
    * @param customElementsReplace Takes root element, old children and new children. It should return `true`, if it did not replace elements in DOM.
    * @return Modifier for bounded property.
    */
  def produce[T](property: ReadableSeqProperty[T, _ <: ReadableProperty[T]],
                 customElementsReplace: DOMManipulator#ReplaceMethod)
                (builder: Seq[T] => Seq[Node]): Binding =
    new SeqAsValueModifier[T](property, builder, customElementsReplace)

  /**
    * Use it to bind sequence property into DOM structure, given `builder` will be used to generate DOM element on every value change.
    * Notice that on every property change, whole element representing property will be rendered again.<br/><br/>
    *
    * The builder takes nested bindings interceptor - it should be used if you want to create another binding inside
    * this builder. This prevents memory leaks by killing nested bindings on property change. <br/><br/>
    *
    * @param property              Property to bind.
    * @param builder               Element builder which will be used to create HTML element. Seq passed to the builder can not be null.
    * @return Modifier for bounded property.
    */
  def produceWithNested[T](property: ReadableSeqProperty[T, _ <: ReadableProperty[T]])
                          (builder: (Seq[T], Binding => Binding) => Seq[Node]): Binding =
    new SeqAsValueModifier[T](property, builder, DOMManipulator.defaultElementReplace)

  /**
    * Use it to bind sequence property into DOM structure, given `builder` will be used to generate DOM element on every value change.
    * Notice that on every property change, whole element representing property will be rendered again.<br/><br/>
    *
    * The builder takes nested bindings interceptor - it should be used if you want to create another binding inside
    * this builder. This prevents memory leaks by killing nested bindings on property change. <br/><br/>
    *
    * @param property              Property to bind.
    * @param builder               Element builder which will be used to create HTML element. Seq passed to the builder can not be null.
    * @param customElementsReplace Takes root element, old children and new children. It should return `true`, if it did not replace elements in DOM.
    * @return Modifier for bounded property.
    */
  def produceWithNested[T](property: ReadableSeqProperty[T, _ <: ReadableProperty[T]],
                           customElementsReplace: DOMManipulator#ReplaceMethod)
                          (builder: (Seq[T], Binding => Binding) => Seq[Node]): Binding =
    new SeqAsValueModifier[T](property, builder, customElementsReplace)

  /**
    * Use it to bind sequence property into DOM structure, given `initBuilder` will be used to generate DOM element at start.
    * Then it listens to structure change and calls `elementsUpdater` to handle each structure change. <br/>
    * <b>Note:</b> This will handle only structure changes, if you want to handle concrete subproperties value changes, you should use
    * another binding method inside `initBuilder` and `elementsUpdater`.
    *
    * @param property        Property to bind.
    * @param initBuilder     Element builder which will be used to create initial HTML element.
    * @param elementsUpdater Function used to update element basing on patch.
    * @return Modifier for bounded property.
    */
  def produce[T, E <: ReadableProperty[T]](property: ReadableSeqProperty[T, E],
                                           initBuilder: Seq[E] => Seq[Node],
                                           elementsUpdater: (Patch[E], Seq[Node]) => Any): Binding =
    new SeqAsValuePatchingModifier[T, E](property, initBuilder, elementsUpdater)

  /**
    * Use it to bind sequence property into DOM structure, given `initBuilder` will be used to generate DOM element at start.
    * Then it listens to structure change and calls `elementsUpdater` to handle each structure change. <br/>
    * <b>Note:</b> This will handle only structure changes, if you want to handle concrete subproperties value changes,
    * you should use another binding method inside `initBuilder` and `elementsUpdater`.<br/><br/>
    *
    * The builder and updater take nested bindings interceptor - it should be used if you want to create another binding inside
    * this builder. This prevents memory leaks by killing nested bindings on property change. <br/><br/>
    *
    * @param property        Property to bind.
    * @param initBuilder     Element builder which will be used to create initial HTML element.
    * @param elementsUpdater Function used to update element basing on patch.
    * @return Modifier for bounded property.
    */
  def produceWithNested[T, E <: ReadableProperty[T]](property: ReadableSeqProperty[T, E],
                                                     initBuilder: (Seq[E], Binding => Binding) => Seq[Node],
                                                     elementsUpdater: (Patch[E], Seq[Node], Binding => Binding) => Any): Binding =
    new SeqAsValuePatchingModifier[T, E](property, initBuilder, elementsUpdater)

  /**
    * Use it to bind sequence property into DOM structure. This method cares about adding new elements which appears in
    * sequence and also removes those which were removed. You only need to provide builder which is used to
    * create HTML element for each sequence member. <br/>
    * <b>Note:</b> This will handle only structure changes, if you want to handle concrete subproperties changes, you should use
    * another binding method inside `builder`.
    *
    * @param property Property to bind.
    * @param builder  Builder which is used for every element.
    * @param customElementsReplace Takes root element, old children and new children. It should return `true`, if it did not replace elements in DOM.
    * @return Modifier for repeat logic.
    */
  def repeat[T, E <: ReadableProperty[T]](property: ReadableSeqProperty[T, E],
                                          customElementsReplace: DOMManipulator#ReplaceMethod = DOMManipulator.defaultElementReplace,
                                          customElementsInsert: DOMManipulator#InsertMethod = DOMManipulator.defaultElementInsert)
                                         (builder: (E) => Seq[Node]): Binding =
    new SeqPropertyModifier[T, E](property, builder, customElementsReplace, customElementsInsert)

  /**
    * Use it to bind sequence property into DOM structure. This method cares about adding new elements which appears in
    * sequence and also removes those which were removed. You only need to provide builder which is used to
    * create HTML element for each sequence member. <br/>
    * <b>Note:</b> This will handle only structure changes, if you want to handle concrete subproperties changes, you should use
    * another binding method inside `builder`.<br/><br/>
    *
    * The builder takes nested bindings interceptor - it should be used if you want to create another binding inside
    * this builder. This prevents memory leaks by killing nested bindings on property change. <br/><br/>
    *
    * @param property Property to bind.
    * @param builder  Builder which is used for every element.
    * @param customElementsReplace Takes root element, old children and new children. It should return `true`, if it did not replace elements in DOM.
    * @return Modifier for repeat logic.
    */
  def repeatWithNested[T, E <: ReadableProperty[T]](property: ReadableSeqProperty[T, E],
                                                    customElementsReplace: DOMManipulator#ReplaceMethod = DOMManipulator.defaultElementReplace,
                                                    customElementsInsert: DOMManipulator#InsertMethod = DOMManipulator.defaultElementInsert)
                                                   (builder: (E, Binding => Binding) => Seq[Node]): Binding =
    new SeqPropertyModifier[T, E](property, builder, customElementsReplace, customElementsInsert)

  /**
    * Use it to bind sequence property into DOM structure. This method cares about adding new elements which appears in
    * sequence and also removes those which were removed. You only need to provide builder which is used to
    * create HTML element for each sequence member. This modifier provides also property with element index in sequence. <br/>
    * <b>Note:</b> This will handle only structure changes, if you want to handle concrete subproperties changes, you should use
    * another binding method inside `builder`.<br/><br/>
    *
    * The builder takes nested bindings interceptor - it should be used if you want to create another binding inside
    * this builder. This prevents memory leaks by killing nested bindings on property change. <br/><br/>
    *
    * @param property Property to bind.
    * @param builder  Builder which is used for every element.
    * @param customElementsReplace Takes root element, old children and new children. It should return `true`, if it did not replace elements in DOM.
    * @return Modifier for repeat logic.
    */
  def repeatWithIndex[T, E <: ReadableProperty[T]](property: ReadableSeqProperty[T, E],
                                                   customElementsReplace: DOMManipulator#ReplaceMethod = DOMManipulator.defaultElementReplace,
                                                   customElementsInsert: DOMManipulator#InsertMethod = DOMManipulator.defaultElementInsert)
                                                  (builder: (E, ReadableProperty[Int], Binding => Binding) => Seq[Node]): Binding =
    new SeqPropertyWithIndexModifier[T, E](property, builder, customElementsReplace, customElementsInsert)

  /**
    * Use in order to add validation logic over property. As this modifier listens on property validation results, user is able
    * to customize what HTML elements should be shown.
    *
    * @param property        Property to bind.
    * @param initBuilder     Builder which is called when validation process is started. It will also give you an access to future of
    *                        validation results.
    * @param completeBuilder Builder which is called when validation process is completed. It will give an access to validation results.
    * @param errorBuilder    Builder which is called, when validation process fails.
    * @return Modifier for validation logic.
    */
  @deprecated("Use `valid` instead.", "0.4.0")
  def bindValidation[A](property: ReadableProperty[A],
                        initBuilder: Future[ValidationResult] => Seq[Element],
                        completeBuilder: ValidationResult => Seq[Element],
                        errorBuilder: Throwable => Seq[Element])(implicit ec: ExecutionContext): Binding =
    new ValidationValueModifier(property, Some(initBuilder), completeBuilder, Some(errorBuilder))

  /**
    * Use in order to add validation logic over property. As this modifier listens on property validation results, user is able
    * to customize what HTML elements should be shown.
    *
    * @param property        Property to bind.
    * @param progressBuilder     Builder which is called when validation process is started. It will also give you an access to future of
    *                        validation results.
    * @param completeBuilder Builder which is called when validation process is completed. It will give an access to validation results.
    * @param errorBuilder    Builder which is called, when validation process fails.
    * @return Modifier for validation logic.
    */
  def valid[A](property: ReadableProperty[A])
              (completeBuilder: ValidationResult => Seq[Node],
               progressBuilder: Future[ValidationResult] => Seq[Node] = null,
               errorBuilder: Throwable => Seq[Node] = null)
              (implicit ec: ExecutionContext): Binding =
    new ValidationValueModifier(property, Option(progressBuilder), completeBuilder, Option(errorBuilder))

  /**
    * Use in order to add validation logic over property. As this modifier listens on property validation results, user is able
    * to customize what HTML elements should be shown.<br/><br/>
    *
    * The builders take nested bindings interceptor - it should be used if you want to create another binding inside
    * this builder. This prevents memory leaks by killing nested bindings on property change. <br/><br/>
    *
    * @param property              Property to bind.
    * @param progressBuilder       Builder which is called when validation process is started. It will also give you an access to future of
    *                              validation results.
    * @param completeBuilder       Builder which is called when validation process is completed. It will give an access to validation results.
    * @param errorBuilder          Builder which is called, when validation process fails.
    * @param customElementsReplace Takes root element, old children and new children. It should return `true`, if it did not replace elements in DOM.
    * @return Modifier for validation logic.
    */
  def validWithNested[A](property: ReadableProperty[A])
                        (completeBuilder: (ValidationResult, Binding => Binding) => Seq[Node],
                         progressBuilder: (Future[ValidationResult], Binding => Binding) => Seq[Node] = null,
                         errorBuilder: (Throwable, Binding => Binding) => Seq[Node] = null,
                         customElementsReplace: DOMManipulator#ReplaceMethod = DOMManipulator.defaultElementReplace)
                        (implicit ec: ExecutionContext): Binding =
    new ValidationValueModifier(property, Option(progressBuilder), completeBuilder, Option(errorBuilder), customElementsReplace)

  /**
    * Use it to update DOM elements, on every `property` change.
    *
    * @param property Property to listen.
    * @param updater  Element attribute updater.
    * @return Modifier for bounded property.
    */
  @deprecated("Use `Attr.bind`, `AttrPair.attrIf` or `Property.reactiveApply` instead.", "0.4.0")
  def bindAttribute[T](property: ReadableProperty[T])(updater: (T, Element) => Any): Binding =
    new AttrModifier[T](property, updater)

  implicit def toAttrOps(attr: Attr): AttrOps =
    new AttrOps(attr)

  implicit def toAttrPairOps(attr: scalatags.generic.AttrPair[Element, _]): AttrPairOps =
    new AttrPairOps(attr)

  implicit def toPropertyOps[T](property: ReadableProperty[T]): PropertyOps[T] =
    new PropertyOps(property)

}

object Bindings extends Bindings {

  class AttrOps(private val attr: Attr) extends AnyVal {
    /** Use this to bind value which is nullable. If the value is null, attribute will be removed. */
    def :?=[B, T](value: T)(implicit ev: AttrValue[B, T]): Modifier[B] =
      if (value == null) new EmptyModifier[B]
      else AttrPair(attr, value, ev)

    /**
      * Use this to add more events listeners to an attribute (:= always overrides previous binding).
      * If callback returns true, other listeners which are queued will not be invoked.
      * If callback returns false, next callback in the queue will be invoked.
      */
    def :+=[T <: Event](callback: (T) => Boolean): Modifier[Element] = {
      AttrPair(attr, callback, new AttrValue[Element, (T) => Boolean] {
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

    /**
      * Use this to add more events listeners to an attribute (:= always overrides previous binding).
      * If callback returns true, other listeners which are queued will not be invoked.
      * If callback returns false, next callback in the queue will be invoked.
      */
    def :+=[T <: Event](callback: (T) => Any, stopPropagation: Boolean = false): Modifier[Element] =
     :+=((v: T) => { callback(v); stopPropagation })

    /** Sets attribute on element. */
    def applyTo(element: Element, value: String = ""): Unit =
      element.setAttribute(attr.name, value)

    /** Removes attribute on element. */
    def removeFrom(element: Element): Unit =
      element.removeAttribute(attr.name)

    /** Synchronises attribute value with property content. */
    def bind(property: ReadableProperty[String]): Modifier[Element] =
      property.reactiveApply {
        case (elem, null) => removeFrom(elem)
        case (elem, v) => applyTo(elem, v)
      }
  }

  class AttrPairOps(private val attr: scalatags.generic.AttrPair[Element, _]) extends AnyVal { outer =>
    /** Sets attribute on element. */
    def applyTo(element: Element): Unit =
      attr.applyTo(element)

    /** Removes attribute on element. */
    def removeFrom(element: Element): Unit =
      element.removeAttribute(attr.a.name)

    /** Synchronises attribute with property content by adding it if value is `false` and removing otherwise. */
    def attrIfNot(property: ReadableProperty[Boolean]): Binding =
      property.reactiveApply(
        (elem, apply) =>
          if (apply) removeFrom(elem)
          else applyTo(elem)
      )

    /** Synchronises attribute with property content by adding it if value is `true` and removing otherwise. */
    def attrIf(property: ReadableProperty[Boolean]): Binding =
      property.reactiveApply(
        (elem, apply) =>
          if (apply) applyTo(elem)
          else removeFrom(elem)
      )

    /** Adds attribute to element if `condition` is `true`. */
    def attrIf(condition: Boolean): Modifier[Element] = new Modifier[Element] {
      override def applyTo(t: Element): Unit =
        if (condition) outer.applyTo(t)
    }
  }

  class PropertyOps[T](private val property: ReadableProperty[T]) extends AnyVal {
    /** Calls provided callback on every property value change. */
    def reactiveApply(callback: (Element, T) => Unit): Binding = new Binding {
      override def applyTo(t: Element): Unit = {
        propertyListeners += property.listen(value =>
          if (available(t)) callback(t, value)
          else kill()
        )
        if (available(t)) callback(t, property.get)
      }
    }
  }

  @inline
  def available(e: Node): Boolean =
    !js.isUndefined(e) && e.ne(null)
}
