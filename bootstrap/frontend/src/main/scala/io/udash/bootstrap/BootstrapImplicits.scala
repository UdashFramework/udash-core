package io.udash.bootstrap

import io.udash._
import org.scalajs.dom
import org.scalajs.dom.Element

import scala.scalajs.js
import scalacss.Defaults._
import scalatags.JsDom.all._


trait BootstrapImplicits {

  implicit class StyleOps(style: StyleA) { outer =>
    def applyTo(element: dom.Element): Unit =
      for (cl <- style.classNameIterator) element.classList.add(cl.value)

    def removeFrom(element: dom.Element): Unit =
      for (cl <- style.classNameIterator) element.classList.remove(cl.value)

    def styleIf(property: ReadableProperty[Boolean]): Modifier = property.reactiveApply(
      (elem, value) =>
        if (value) applyTo(elem)
        else removeFrom(elem)
    )

    def styleIf(condition: Boolean): Modifier = new Modifier {
      override def applyTo(t: Element): Unit =
        if (condition) outer.applyTo(t)
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
    def styles(styles: StyleA*): dom.Element = {
      styles.foreach(_.applyTo(element))
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