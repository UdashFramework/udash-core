package io.udash.bootstrap

import com.avsystem.commons.misc.AbstractCase

case class ComponentId(id: String) extends AbstractCase {
  override def toString: String = id

  def subcomponent(subId: String): ComponentId =
    ComponentId(s"$id.$subId")
}

object ComponentId {
  private var cid = -1

  /** Generates unique element ID */
  def newId(): ComponentId = {
    cid += 1
    ComponentId(s"bs-$cid")
  }

}
