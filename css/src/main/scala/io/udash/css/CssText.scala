package io.udash
package css

import scalatags.Text.all.Modifier
import scalatags.text.Builder

trait CssText {
  implicit def style2TextMod(s: CssStyle): Modifier = new CssText.TextStyleModifier(s)
  implicit def styles2TextMod(s: CssStyle*): Modifier = new CssText.TextStyleModifier(s: _*)
}

object CssText extends CssText {
  private class TextStyleModifier(styles: CssStyle*) extends Modifier {
    override def applyTo(t: Builder): Unit =
      styles.foreach { s => t.appendAttr("class", Builder.GenericAttrValueSource(s.classNames.mkString(" ", " ", ""))) }
  }
}
