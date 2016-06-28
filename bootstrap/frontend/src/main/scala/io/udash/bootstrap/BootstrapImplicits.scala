package io.udash.bootstrap

import io.udash._
import org.scalajs.dom
import org.scalajs.dom.Element

import scala.scalajs.js
import scalatags.JsDom.all._


trait BootstrapImplicits {
  class StyleModifier(s: BootstrapStyles.BootstrapClass) extends Modifier {
    def applyTo(t: dom.Element) =
      t.classList.add(s.cls)
  }

  implicit final def styleToJsDomTag(s: BootstrapStyles.BootstrapClass): Modifier =
    new StyleModifier(s)

  implicit class StyleOps(style: BootstrapStyles.BootstrapClass) { outer =>
    def addTo(element: dom.Element): Unit =
      element.classList.add(style.cls)

    def removeFrom(element: dom.Element): Unit =
      element.classList.remove(style.cls)

    def styleIf(property: ReadableProperty[Boolean]): Modifier = property.reactiveApply(
      (elem, value) =>
        if (value) addTo(elem)
        else removeFrom(elem)
    )

    def styleIf(condition: Boolean): Modifier = new Modifier {
      override def applyTo(t: Element): Unit =
        if (condition) outer.addTo(t)
    }
  }

  implicit class AttrOps(attr: Attr) { outer =>
    def applyTo(element: dom.Element, value: String = ""): Unit =
      element.setAttribute(attr.name, value)

    def bind(property: ReadableProperty[String]): Modifier = property.reactiveApply(
      (elem, value) => applyTo(elem)
    )
  }

  implicit class AttrPairOps(attr: scalatags.generic.AttrPair[_, _]) { outer =>
    def applyTo(element: dom.Element, value: String = ""): Unit =
      element.setAttribute(attr.a.name, value)

    def removeFrom(element: dom.Element): Unit =
      element.removeAttribute(attr.a.name)

    def attrIf(property: ReadableProperty[Boolean]): Modifier = property.reactiveApply(
      (elem, value) =>
        if (value) applyTo(elem)
        else removeFrom(elem)
    )

    def attrIf(condition: Boolean): Modifier = new Modifier {
      override def applyTo(t: Element): Unit =
        if (condition) outer.applyTo(t)
    }
  }

  implicit class ElementOps(element: dom.Element) {
    def styles(styles: BootstrapStyles.BootstrapClass*): dom.Element = {
      styles.foreach(_.addTo(element))
      element
    }
  }

  implicit class PropertyOps[T](property: ReadableProperty[T]) {
    def reactiveApply(f: (Element, T) => Unit): Modifier = new Modifier {
      override def applyTo(t: Element): Unit = {
        var registration: Registration = null
        registration = property.listen(value => if (available(t)) f(t, value) else registration.cancel())
        if (available(t)) f(t, property.get)
      }
    }
  }

  @inline
  private def available(e: dom.Node): Boolean = {
    !js.isUndefined(e) && e.ne(null)
  }
}

object BootstrapImplicits extends BootstrapImplicits