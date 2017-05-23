package io.udash.css

import org.scalajs.dom.Element

import scalatags.JsDom.Modifier
import scalatags.text.Builder

trait CssView {
  implicit val pcCssStyle: PropertyCreator[CssStyle] = PropertyCreator.propertyCreator[CssStyle]
  implicit val pcOCssStyle: PropertyCreator[Option[CssStyle]] = PropertyCreator.propertyCreator[Option[CssStyle]]
  implicit val pcSCssStyle: PropertyCreator[Seq[CssStyle]] = PropertyCreator.propertyCreator[Seq[CssStyle]]

  private object NoopModifier extends Modifier {
    override def applyTo(t: Element): Unit = ()
  }

  private class StyleModifier(s: CssStyle) extends Modifier {
    override def applyTo(t: Element): Unit =
      s.addTo(t)
  }

  private class TextStyleModifier(s: CssStyle) extends scalatags.Text.all.Modifier {
    override def applyTo(t: Builder): Unit =
      t.appendAttr("class", Builder.GenericAttrValueSource(" " + s.className))
  }

  implicit class StyleOps(style: CssStyle) {
    def addTo(element: dom.Element): Unit =
      element.classList.add(style.className)

    def removeFrom(element: dom.Element): Unit =
      element.classList.remove(style.className)

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
    def styles(styles: CssStyle*): dom.Element = {
      styles.foreach(_.addTo(element))
      element
    }
  }

  implicit def style2Mod(s: CssStyle): Modifier = new Modifier {
    override def applyTo(t: Element): Unit =
      t.classList.add(s.className)
  }

  implicit def styles2Mod(s: CssStyle*): Modifier = new Modifier {
    override def applyTo(t: Element): Unit =
      s.foreach(_.addTo(t))
  }

  implicit def style2TextMod(s: CssStyle): scalatags.Text.all.Modifier =
    new TextStyleModifier(s)

  implicit def styles2TextMod(s: CssStyle*): scalatags.Text.all.Modifier =
    new scalatags.Text.all.Modifier {
      override def applyTo(t: Builder): Unit =
        s.foreach(s => new TextStyleModifier(s).applyTo(t))
    }

}
