package io.udash
package css

import scalatags.Text.all._
import scalatags.text.Builder

trait CssText {
  implicit def style2TextMod(s: CssStyle): Modifier = new CssText.TextStyleModifier(s)
  implicit def styles2TextMod(s: CssStyle*): Modifier = new CssText.TextStyleModifier(s: _*)
}

object CssText extends CssText {
  private final class TextStyleModifier(styles: CssStyle*) extends Modifier {
    override def applyTo(t: Builder): Unit =
      styles.foreach { s => t.appendAttr(cls.name, Builder.GenericAttrValueSource(s.classNames.mkString(" ", " ", ""))) }
  }
}
