package io.udash.component

import com.avsystem.commons.misc.CaseMethods
import io.udash.macros.ComponentIdMacro
import org.scalajs.dom.Element
import scalatags.JsDom.GenericAttr
import scalatags.JsDom.all._


final case class ComponentId private(value: String) extends Modifier with CaseMethods {
  override def applyTo(t: Element): Unit = {
    t.id = value
  }

  /** Generate new [[ComponentId]] based on a current id value with added suffix. */
  def withSuffix(s: String): ComponentId = ComponentId(s"$value-$s")

  override def toString(): String = value
}

object ComponentId {
  private var count: Int = -1

  private def next(): Int = {
    count += 1
    count
  }

  /** Generates unique element ID based on the enclosing (calling) class */
  def generate(): ComponentId = macro ComponentIdMacro.impl

  def forName(name: String): ComponentId =
    ComponentId(name + "-" + ComponentId.next())

  implicit val idAttrValue: GenericAttr[ComponentId] = new GenericAttr[ComponentId]
}
