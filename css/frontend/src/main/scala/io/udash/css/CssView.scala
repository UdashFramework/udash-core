package io.udash.css

import io.udash.ReadableProperty
import io.udash.properties.PropertyCreator
import org.scalajs.dom
import org.scalajs.dom.Element

import scalatags.JsDom.all.Modifier
import scalatags.text.Builder

/** Contains integration of CSS structures with Scalatags. */
trait CssView {
  implicit val pcCssStyle: PropertyCreator[CssStyle] = PropertyCreator.propertyCreator[CssStyle]
  implicit val pcOCssStyle: PropertyCreator[Option[CssStyle]] = PropertyCreator.propertyCreator[Option[CssStyle]]
  implicit val pcSCssStyle: PropertyCreator[Seq[CssStyle]] = PropertyCreator.propertyCreator[Seq[CssStyle]]

  private class TextStyleModifier(s: CssStyle) extends scalatags.Text.all.Modifier {
    override def applyTo(t: Builder): Unit =
      t.appendAttr("class", Builder.GenericAttrValueSource(s.classNames.mkString(" ", " ", "")))
  }

  implicit def style2Mod(s: CssStyle): Modifier = new Modifier {
    override def applyTo(t: Element): Unit =
      s.addTo(t)
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

  implicit def elementOps(element: dom.Element): CssView.ElementOps =
    new CssView.ElementOps(element)

  implicit def styleOps(style: CssStyle): CssView.StyleOps =
    new CssView.StyleOps(style)
}

object CssView extends CssView {
  private object NoopModifier extends Modifier {
    override def applyTo(t: Element): Unit = ()
  }

  private class StyleModifier(s: CssStyle) extends Modifier {
    override def applyTo(t: Element): Unit =
      s.addTo(t)
  }

  implicit class ElementOps(private val element: dom.Element) extends AnyVal {
    def styles(styles: CssStyle*): dom.Element = {
      styles.foreach(_.addTo(element))
      element
    }
  }

  implicit class StyleOps(private val style: CssStyle) extends AnyVal {
    def addTo(element: dom.Element): Unit =
      style.classNames.foreach(element.classList.add)

    def removeFrom(element: dom.Element): Unit = {
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

    def styleIf(property: ReadableProperty[Boolean]): Modifier =
      property.reactiveApply(
        (elem, value) =>
          if (value) addTo(elem)
          else removeFrom(elem)
      )

    def styleIf(condition: Boolean): Modifier =
      if (condition) new StyleModifier(style)
      else NoopModifier
  }
}
