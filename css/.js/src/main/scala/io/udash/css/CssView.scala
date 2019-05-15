package io.udash
package css

import io.udash.bindings.modifiers.{Binding, EmptyModifier}
import org.scalajs.dom.Element
import scalatags.JsDom.all.Modifier

/** Contains integration of CSS structures with Scalatags. */
trait CssView extends CssText {

  import CssView._

  implicit def style2Mod(s: CssStyle): Modifier = new StyleModifier(s)
  implicit def styles2Mod(s: CssStyle*): Modifier = new StyleModifier(s: _*)
  implicit def elementOps(element: Element): ElementOps = new ElementOps(element)
  implicit def styleOps(style: CssStyle): StyleOps = new StyleOps(style)
  implicit def styleFactoryOps[T](factory: T => CssStyle): StyleFactoryOps[T] = new StyleFactoryOps[T](factory)
}

object CssView extends CssView {
  private class StyleModifier(styles: CssStyle*) extends Modifier {
    override def applyTo(t: Element): Unit =
      styles.foreach(_.addTo(t))
  }

  final class ElementOps(private val element: Element) extends AnyVal {
    def styles(styles: CssStyle*): Element = {
      styles.foreach(_.addTo(element))
      element
    }
  }

  final class StyleOps(private val style: CssStyle) extends AnyVal {
    def addTo(element: Element): Unit =
      style.classNames.foreach(element.classList.add)

    def removeFrom(element: Element): Unit = {
      val cl = element.classList
      cl.remove(style.className)
      style.commonPrefixClass.foreach { prefixClass =>
        def removePrefix(i: Int = 0): Boolean =
          if (i >= cl.length) true
          else !cl(i).startsWith(s"$prefixClass-") && removePrefix(i + 1)
        if (removePrefix()) {
          cl.remove(prefixClass)
        }
      }
    }

    def styleIf(property: ReadableProperty[Boolean]): Binding =
      property.reactiveApply(
        (elem, value) =>
          if (value) addTo(elem)
          else removeFrom(elem)
      )

    def styleIf(condition: Boolean): Modifier = {
      if (condition) new StyleModifier(style)
      else new EmptyModifier[Element]
    }
  }

  final class StyleFactoryOps[T](private val factory: T => CssStyle) extends AnyVal {
    def reactiveApply(p: ReadableProperty[T]): Binding =
      reactiveOptionApply(p.transform(Some.apply))

    def reactiveOptionApply(p: ReadableProperty[Option[T]]): Binding = new Binding {
      private var prevStyle: CssStyle = _
      override def applyTo(el: Element): Unit = {
        propertyListeners += p.listen(t => {
          if (prevStyle != null) {
            prevStyle.classNames.foreach(el.classList.remove)
          }
          t match {
            case Some(t) =>
              val newStyle = factory(t)
              newStyle.classNames.foreach(el.classList.add)
              prevStyle = newStyle
            case None =>
              prevStyle = null
          }
        }, initUpdate = true)
      }
    }
  }
}
