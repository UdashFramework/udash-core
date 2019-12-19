package io.udash.component

import com.avsystem.commons.misc.AbstractCase
import io.udash.macros.ComponentIdMacro
import org.scalajs.dom.Element
import scalatags.JsDom.GenericAttr
import scalatags.JsDom.all._


final case class ComponentId private(value: String) extends AbstractCase with Modifier {
  override def applyTo(t: Element): Unit = {
    t.id = value
  }

  /** Generate new [[ComponentId]] based on a current id value with added suffix. */
  def withSuffix(s: String): ComponentId = ComponentId.forName(ComponentId.join(value, s))

  override def toString(): String = value
}

object ComponentId {
  private val Separator = "-"
  private def join(s: String*): String = s.mkString(Separator)

  private var count: Int = -1

  private def next(): Int = {
    count += 1
    count
  }

  /** Generates unique element ID based on the enclosing (calling) class */
  def generate(): ComponentId = macro ComponentIdMacro.impl

  def forName(name: String): ComponentId = ComponentId(join(name, next().toString))

  implicit val IdAttrValue: GenericAttr[ComponentId] = new GenericAttr[ComponentId]
}
