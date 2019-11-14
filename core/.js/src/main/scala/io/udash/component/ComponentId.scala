package io.udash.component

/** General usage class for carrying id of component. */
case class ComponentId(id: String) extends AnyVal {
  override def toString: String = id

  def withSuffix(subId: String): ComponentId =
    ComponentId(s"$id-$subId")
}

object ComponentId {
  private var cid = -1

  /** Generates unique element ID */
  def generate(): ComponentId = {
    cid += 1
    ComponentId(s"bs$cid")
  }

}
