package io.udash.routing

import io.udash.{State, Url}

/**
 * The implementation of this trait should be injected to [[io.udash.Application]].
 * It should implement a bidirectional mapping between [[io.udash.Url]] and [[io.udash.core.State]].
 */
trait RoutingRegistry[HierarchyRoot <: State] {
  def matchUrl(url: Url): HierarchyRoot
  def matchState(state: HierarchyRoot): Url

  protected def bidirectional(pf: PartialFunction[Url, HierarchyRoot]): (PartialFunction[Url, HierarchyRoot], PartialFunction[HierarchyRoot, Url]) =
  macro com.avsystem.commons.macros.misc.BidirectionalMacro.impl[Url, HierarchyRoot]

  import RoutingRegistry._

  protected final val / = RoutingRegistry./

  protected implicit def stringRoutingOps(str: Url): StringRoutingOps =
    new StringRoutingOps(str)
}

object RoutingRegistry {
  implicit final class StringRoutingOps(private val left: String) extends AnyVal {
    def /(right: Any): Url = RoutingRegistry./(left, right.toString)
  }

  object / {
    def unapply(path: Url): Option[(Url, Url)] = {
      val strippedPath = path.stripSuffix("/")
      Some(strippedPath.lastIndexOf("/")).collect {
        case splitIndex if splitIndex >= 0 =>
          val left = strippedPath.substring(0, splitIndex)
          val right = strippedPath.substring(splitIndex + 1, strippedPath.length)
          (left, right)
      }
    }

    def apply(left: Url, right: Url): Url =
      left + "/" + right
  }
}
