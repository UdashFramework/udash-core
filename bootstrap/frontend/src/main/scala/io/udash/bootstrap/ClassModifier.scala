package io.udash.bootstrap

import org.scalajs.dom._

import scalacss.StyleA
import scalatags.JsDom.all._

abstract class ClassModifier(styles: StyleA*) extends Modifier {
  override def applyTo(t: Element): Unit = styles.foreach(_.applyTo(t))
}
