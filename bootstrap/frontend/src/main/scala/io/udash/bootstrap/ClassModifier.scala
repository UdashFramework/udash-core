package io.udash.bootstrap

import io.udash.css.CssStyle
import org.scalajs.dom._
import scalatags.JsDom.all._

/** Adds selected classes to provided DOM element. */
abstract class ClassModifier(styles: CssStyle*) extends Modifier {
  import io.udash.css.CssView._
  override def applyTo(t: Element): Unit =
    styles.foreach(_.addTo(t))
}
