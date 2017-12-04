package io.udash.css

import io.udash.ReadableProperty
import io.udash.bindings.modifiers.EmptyModifier
import io.udash.properties.PropertyCreator
import org.scalajs.dom
import org.scalajs.dom.Element

import scala.scalajs.js
import scalatags.JsDom.all.Modifier
import scalatags.text.Builder
import js.JSConverters._

/** Contains integration of CSS structures with Scalatags. */
trait CssView {
  implicit def pcCssStyle: PropertyCreator[CssStyle] = CssView.cssStylePC
  implicit def pcOCssStyle: PropertyCreator[Option[CssStyle]] = CssView.cssStylePCO
  implicit def pcSCssStyle: PropertyCreator[Seq[CssStyle]] = CssView.cssStylePCS

  implicit def style2Mod(s: CssStyle): Modifier = new CssView.StyleModifier(js.Array(s))
  implicit def styles2Mod(s: CssStyle*): Modifier = new CssView.StyleModifier(s.toJSArray)
  implicit def style2TextMod(s: CssStyle): scalatags.Text.all.Modifier = new CssView.TextStyleModifier(js.Array(s))
  implicit def styles2TextMod(s: CssStyle*): scalatags.Text.all.Modifier = new CssView.TextStyleModifier(s.toJSArray)

  implicit def elementOps(element: dom.Element): CssView.ElementOps =
    new CssView.ElementOps(element)

  implicit def styleOps(style: CssStyle): CssView.StyleOps =
    new CssView.StyleOps(style)
}

object CssView extends CssView {
  private val cssStylePC: PropertyCreator[CssStyle] = PropertyCreator.propertyCreator[CssStyle]
  private val cssStylePCO: PropertyCreator[Option[CssStyle]] = PropertyCreator.propertyCreator[Option[CssStyle]]
  private val cssStylePCS: PropertyCreator[Seq[CssStyle]] = PropertyCreator.propertyCreator[Seq[CssStyle]]

  private class StyleModifier(styles: js.Array[CssStyle]) extends Modifier {
    override def applyTo(t: Element): Unit =
      styles.foreach(_.addTo(t))
  }

  private class TextStyleModifier(styles: js.Array[CssStyle]) extends scalatags.Text.all.Modifier {
    override def applyTo(t: Builder): Unit =
      styles.foreach { s =>
        t.appendAttr("class", Builder.GenericAttrValueSource(s.classNames.mkString(" ", " ", "")))
      }
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

    def styleIf(condition: Boolean): Modifier = {
      import scalatags.JsDom.all.UnitFrag
      if (condition) new StyleModifier(js.Array(style))
      else new EmptyModifier[Element]
    }
  }
}
