package io.udash.bindings

import com.avsystem.commons.misc.Opt
import io.udash.bindings.Bindings.{AttrOps, AttrPairOps, HasCssName, PropertyOps}
import io.udash.bindings.modifiers._
import io.udash.properties.seq.ReadableSeqProperty
import io.udash.properties.single.ReadableProperty
import org.scalajs.dom._
import org.scalajs.dom.raw.HTMLElement
import scalatags.JsDom
import scalatags.generic.{Attr, AttrPair, AttrValue, Modifier}

import scala.scalajs.js

trait Bindings {
  final val Checkbox = inputs.Checkbox
  final val CheckButtons = inputs.CheckButtons
  final val FileInput = inputs.FileInput
  final val NumberInput = inputs.NumberInput
  final val PasswordInput = inputs.PasswordInput
  final val RadioButtons = inputs.RadioButtons
  final val Select = inputs.Select
  final val TextArea = inputs.TextArea
  final val TextInput = inputs.TextInput
  final val RangeInput = inputs.RangeInput

  implicit def seqFromNode(el: Node): Seq[Node] = js.Array(el)
  implicit def seqFromElement(el: Element): Seq[Element] = js.Array(el)
  implicit def seqNodeFromOpt[T](el: Opt[T])(implicit ev: T => Modifier[Element]): Modifier[Element] = {
    new JsDom.all.SeqNode(el.toSeq)
  }

  /** Creates empty text node, which is useful as placeholder. */
  def emptyStringNode(): Node =
    document.createTextNode("")

  /**
    * Renders component with provided timeout.
    * It's useful to render heavy components after displaying the main view.
    */
  def queuedNode(component: => Seq[Node], timeout: Int = 0): Modifier[Element] = new Modifier[Element] {
    override def applyTo(t: Element): Unit = {
      val el = document.createElement("div")
      t.appendChild(el)
      window.setTimeout(() => t.replaceChildren(el, component), timeout)
    }
  }

  /**
    * Use it to bind value of property into DOM structure. Value of the property will be rendered as text node. (Using .toString method.)
    * If property value is null, empty text node will be added.
    *
    * @param property Property to bind.
    * @return Modifier for bound property.
    */
  def bind[T](property: ReadableProperty[T]): Binding =
    new SimplePropertyModifier[T](property)

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
    * @param customElementsReplace Takes root element, old children and new children. It should return `true`,
    *                              if it did not replace elements in DOM. Is such a case the default implementation
    *                              will replace the elements. Otherwise you have to replace elements in DOM manually.
    * @return Modifier for bounded property.
    */
  def showIf(property: ReadableProperty[Boolean], customElementsReplace: DOMManipulator.ReplaceMethod)
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
    showIfElse(property, DOMManipulator.DefaultElementReplace)(elements, elseElements)

  /**
    * Switches provided DOM elements depending on property value.
    *
    * @param property Property to check.
    * @param elements  Elements to show if property value is `true`.
    * @param elseElements Elements to show if property value is `false`.
    * @param customElementsReplace Takes root element, old children and new children. It should return `true`,
    *                              if it did not replace elements in DOM. Is such a case the default implementation
    *                              will replace the elements. Otherwise you have to replace elements in DOM manually.
    * @return Modifier for bounded property.
    */
  def showIfElse(property: ReadableProperty[Boolean], customElementsReplace: DOMManipulator.ReplaceMethod)
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
                          (builder: (T, Binding.NestedInterceptor) => Seq[Node]): Binding =
    new PropertyModifier[T](property, builder, checkNull, DOMManipulator.DefaultElementReplace)

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
    * @param customElementsReplace Takes root element, old children and new children. It should return `true`,
    *                              if it did not replace elements in DOM. Is such a case the default implementation
    *                              will replace the elements. Otherwise you have to replace elements in DOM manually.
    * @return Modifier for bounded property.
    */
  def produceWithNested[T](property: ReadableProperty[T], customElementsReplace: DOMManipulator.ReplaceMethod, checkNull: Boolean)
                          (builder: (T, Binding.NestedInterceptor) => Seq[Node]): Binding =
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
    new SeqAsValueModifier[T](property, builder, DOMManipulator.DefaultElementReplace)

  /**
    * Use it to bind sequence property into DOM structure, given `builder` will be used to generate DOM element on every value change.
    * Notice that on every property change, whole element representing property will be rendered again.
    *
    * @param property              Property to bind.
    * @param builder               Element builder which will be used to create HTML element. Seq passed to the builder can not be null.
    * @param customElementsReplace Takes root element, old children and new children. It should return `true`,
    *                              if it did not replace elements in DOM. Is such a case the default implementation
    *                              will replace the elements. Otherwise you have to replace elements in DOM manually.
    * @return Modifier for bounded property.
    */
  def produce[T](property: ReadableSeqProperty[T, _ <: ReadableProperty[T]],
                 customElementsReplace: DOMManipulator.ReplaceMethod)
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
                          (builder: (Seq[T], Binding.NestedInterceptor) => Seq[Node]): Binding =
    new SeqAsValueModifier[T](property, builder, DOMManipulator.DefaultElementReplace)

  /**
    * Use it to bind sequence property into DOM structure, given `builder` will be used to generate DOM element on every value change.
    * Notice that on every property change, whole element representing property will be rendered again.<br/><br/>
    *
    * The builder takes nested bindings interceptor - it should be used if you want to create another binding inside
    * this builder. This prevents memory leaks by killing nested bindings on property change. <br/><br/>
    *
    * @param property              Property to bind.
    * @param builder               Element builder which will be used to create HTML element. Seq passed to the builder can not be null.
    * @param customElementsReplace Takes root element, old children and new children. It should return `true`,
    *                              if it did not replace elements in DOM. Is such a case the default implementation
    *                              will replace the elements. Otherwise you have to replace elements in DOM manually.
    * @return Modifier for bounded property.
    */
  def produceWithNested[T](property: ReadableSeqProperty[T, _ <: ReadableProperty[T]],
                           customElementsReplace: DOMManipulator.ReplaceMethod)
                          (builder: (Seq[T], Binding.NestedInterceptor) => Seq[Node]): Binding =
    new SeqAsValueModifier[T](property, builder, customElementsReplace)

  /**
    * Use it to bind sequence property into DOM structure. This method cares about adding new elements which appears in
    * sequence and also removes those which were removed. You only need to provide builder which is used to
    * create HTML element for each sequence member. <br/>
    * <b>Note:</b> This will handle only structure changes, if you want to handle concrete subproperties changes, you should use
    * another binding method inside `builder`.
    *
    * @param property Property to bind.
    * @param builder  Builder which is used for every element.
    * @param customElementsInsert Takes root element, ref node and new children. It should return `true`,
    *                             if it does not insert elements in DOM. Is such a case the default implementation
    *                             will insert the elements. Otherwise you have to insert elements in DOM manually.
    * @param customElementsReplace Takes root element, old children and new children. It should return `true`,
    *                              if it did not replace elements in DOM. Is such a case the default implementation
    *                              will replace the elements. Otherwise you have to replace elements in DOM manually.
    * @return Modifier for repeat logic.
    */
  def repeat[T, E <: ReadableProperty[T]](property: ReadableSeqProperty[T, E],
                                          customElementsReplace: DOMManipulator.ReplaceMethod = DOMManipulator.DefaultElementReplace,
                                          customElementsInsert: DOMManipulator.InsertMethod = DOMManipulator.DefaultElementInsert)
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
    * @param customElementsInsert Takes root element, ref node and new children. It should return `true`,
    *                             if it does not insert elements in DOM. Is such a case the default implementation
    *                             will insert the elements. Otherwise you have to insert elements in DOM manually.
    * @param customElementsReplace Takes root element, old children and new children. It should return `true`,
    *                              if it did not replace elements in DOM. Is such a case the default implementation
    *                              will replace the elements. Otherwise you have to replace elements in DOM manually.
    * @return Modifier for repeat logic.
    */
  def repeatWithNested[T, E <: ReadableProperty[T]](property: ReadableSeqProperty[T, E],
                                                    customElementsReplace: DOMManipulator.ReplaceMethod = DOMManipulator.DefaultElementReplace,
                                                    customElementsInsert: DOMManipulator.InsertMethod = DOMManipulator.DefaultElementInsert)
                                                   (builder: (E, Binding.NestedInterceptor) => Seq[Node]): Binding =
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
    * @param customElementsInsert Takes root element, ref node and new children. It should return `true`,
    *                             if it does not insert elements in DOM. Is such a case the default implementation
    *                             will insert the elements. Otherwise you have to insert elements in DOM manually.
    * @param customElementsReplace Takes root element, old children and new children. It should return `true`,
    *                              if it did not replace elements in DOM. Is such a case the default implementation
    *                              will replace the elements. Otherwise you have to replace elements in DOM manually.
    * @return Modifier for repeat logic.
    */
  def repeatWithIndex[T, E <: ReadableProperty[T]](property: ReadableSeqProperty[T, E],
                                                   customElementsReplace: DOMManipulator.ReplaceMethod = DOMManipulator.DefaultElementReplace,
                                                   customElementsInsert: DOMManipulator.InsertMethod = DOMManipulator.DefaultElementInsert)
                                                  (builder: (E, ReadableProperty[Int], Binding.NestedInterceptor) => Seq[Node]): Binding =
    new SeqPropertyWithIndexModifier[T, E](property, builder, customElementsReplace, customElementsInsert)

  implicit def toAttrOps(attr: Attr): AttrOps =
    new AttrOps(attr)

  implicit def toAttrPairOps(attr: scalatags.generic.AttrPair[Element, _]): AttrPairOps =
    new AttrPairOps(attr)

  implicit def toPropertyOps[T](property: ReadableProperty[T]): PropertyOps[T] =
    new PropertyOps(property)

  implicit class InlineStyleOps[T: HasCssName](style: T)(implicit hasCssName: HasCssName[T]) {
    /** Sets style on element. */
    def applyTo(element: HTMLElement, value: String = ""): Unit =
      element.style.setProperty(hasCssName.cssName(style), value)

    /** Removes style from element. */
    def removeFrom(element: HTMLElement): Unit =
      element.style.removeProperty(hasCssName.cssName(style))

    /** Synchronises style value with property content. */
    def bind(property: ReadableProperty[String]): Binding =
      property.reactiveApply {
        case (elem: HTMLElement, null) => removeFrom(elem)
        case (elem: HTMLElement, v) => applyTo(elem, v)
        case _ =>
      }

    /**
      * Synchronises style value with property content by adding it when property is not null and
      * condition property is 'true' and removing otherwise.
      */
    def bindIf(property: ReadableProperty[String], conditionProperty: ReadableProperty[Boolean]): Binding =
      property.combine(conditionProperty)((_, _)).reactiveApply {
        case (elem: HTMLElement, (null, _) | (_, false)) => removeFrom(elem)
        case (elem: HTMLElement, (v, true)) => applyTo(elem, v)
        case _ =>
      }
  }

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
    def bind(property: ReadableProperty[String]): Binding =
      property.reactiveApply {
        case (elem, null) => removeFrom(elem)
        case (elem, v) => applyTo(elem, v)
      }

    /**
      * Synchronises attribute value with property content by adding it when property is not null and
      * condition property is 'true' and removing otherwise.
      */
    def bindIf(property: ReadableProperty[String], conditionProperty: ReadableProperty[Boolean]): Binding =
      property.combine(conditionProperty)((_, _)).reactiveApply {
        case (elem, (null, _) | (_, false)) => removeFrom(elem)
        case (elem, (v, true)) => applyTo(elem, v)
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

  /** Abstraction over scalatags styles representation. */
  trait HasCssName[-T] {
    def cssName(v: T): String
  }
  object HasCssName {

    import scalatags.generic.{PixelStyle, Style}

    implicit val StyleHasCssName: HasCssName[Style] = new HasCssName[Style] {
      override def cssName(v: scalatags.generic.Style): String = v.cssName
    }
    implicit val PixelStyleHasCssName: HasCssName[PixelStyle] = new HasCssName[PixelStyle] {
      override def cssName(v: scalatags.generic.PixelStyle): String = v.cssName
    }
  }

  @inline
  def available(e: Node): Boolean =
    !js.isUndefined(e) && e.ne(null)
}
