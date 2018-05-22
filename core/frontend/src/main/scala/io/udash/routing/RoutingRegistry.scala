package io.udash.routing

import io.udash.core.{State, Url}

/**
  * The implementation of this trait should be injected to [[io.udash.routing.RoutingEngine]].
  * It should implement a bidirectional mapping between [[io.udash.core.Url]] and [[io.udash.core.State]].
  */
trait RoutingRegistry[HierarchyRoot <: State] {
  def matchUrl(url: Url): HierarchyRoot
  def matchState(state: HierarchyRoot): Url

  protected def bidirectional(pf: PartialFunction[String, HierarchyRoot]): (PartialFunction[String, HierarchyRoot], PartialFunction[HierarchyRoot, String]) =
  macro com.avsystem.commons.macros.misc.BidirectionalMacro.impl[String, HierarchyRoot]

  import RoutingRegistry._

  protected final val / = RoutingRegistry./

  protected implicit def stringRoutingOps(str: String): StringRoutingOps =
    new StringRoutingOps(str)
}

object RoutingRegistry {
  implicit class StringRoutingOps(val left: String) extends AnyVal {
    def /(right: Any): String = RoutingRegistry./(left, right.toString)
  }

  object / {
    def unapply(path: String): Option[(String, String)] = {
      val strippedPath = path.stripSuffix("/")
      Some(strippedPath.lastIndexOf("/")).filter(_ >= 0).map { splitIndex =>
        val left = strippedPath.substring(0, splitIndex)
        val right = strippedPath.substring(splitIndex + 1, strippedPath.length)
        (left, right)
      }
    }

    def apply(left: String, right: String): String =
      left + "/" + right
  }
}
