package io.udash.bootstrap

import io.udash._
import io.udash.bootstrap.UdashBootstrap.ComponentId
import org.scalajs.dom
import org.scalajs.dom.Element

import scalatags.JsDom.GenericAttr
import scalatags.JsDom.all._


trait BootstrapImplicits {

  private object NoopModifier extends Modifier {
    override def applyTo(t: Element): Unit = ()
  }

  implicit val idAttrValue: GenericAttr[ComponentId] = new GenericAttr[ComponentId]

  class StyleModifier(s: BootstrapStyles.BootstrapClass) extends ClassModifier(s)

  implicit final def styleToJsDomTag(s: BootstrapStyles.BootstrapClass): Modifier =
    new StyleModifier(s)

  implicit class StyleOps(style: BootstrapStyles.BootstrapClass) {
    def addTo(element: dom.Element): Unit =
      element.classList.add(style.cls)

    def removeFrom(element: dom.Element): Unit =
      element.classList.remove(style.cls)

    def styleIf(property: ReadableProperty[Boolean]): Modifier =
      property.reactiveApply(
        (elem, value) =>
          if (value) addTo(elem)
          else removeFrom(elem)
      )

    def styleIf(condition: Boolean): Modifier =
      condition match {
        case true => new StyleModifier(style)
        case false => NoopModifier
      }
  }

  implicit class ElementOps(element: dom.Element) {
    def styles(styles: BootstrapStyles.BootstrapClass*): dom.Element = {
      styles.foreach(_.addTo(element))
      element
    }
  }
}

object BootstrapImplicits extends BootstrapImplicits