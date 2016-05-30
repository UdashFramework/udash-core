package io.udash.bootstrap

import org.scalajs.dom._

import scalacss.StyleA
import scalatags.JsDom.all._

abstract class ClassModifier(style: StyleA) extends Modifier {
  override def applyTo(t: Element): Unit = style.applyTo(t)
}
