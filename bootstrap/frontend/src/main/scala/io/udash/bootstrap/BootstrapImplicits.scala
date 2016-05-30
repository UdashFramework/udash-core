package io.udash.bootstrap

import org.scalajs.dom
import io.udash._
import org.scalajs.dom.Element

import scala.scalajs.js
import scalacss.Defaults._
import scalatags.JsDom.all._


trait BootstrapImplicits {

  implicit class StyleOps(style: StyleA) {
    def applyTo(element: dom.Element): Unit = {
      element.classList.add(style.htmlClass)
    }

    def styleIf(property: Property[Boolean]): Modifier = property.reactiveApply(
      (elem, value) =>
        if (value) applyTo(elem)
        else elem.classList.remove(style.htmlClass)
    )
  }

  implicit class ElementOps(element: dom.Element) {
    def styles(styles: StyleA*): dom.Element = {
      styles.foreach(_.applyTo(element))
      element
    }
  }

  implicit class PropertyOps[T](property: Property[T]) {
    def reactiveApply(f: (Element, T) => Unit): Modifier = new Modifier {
      override def applyTo(t: Element): Unit = {
        var registration: Registration = null
        registration = property.listen(value => if (available(t)) f(t, value) else registration.cancel())
        property.set(property.get)
      }
    }
  }

  @inline
  private def available(e: dom.Node): Boolean = {
    !js.isUndefined(e) && e.ne(null)
  }
}

object BootstrapImplicits extends BootstrapImplicits