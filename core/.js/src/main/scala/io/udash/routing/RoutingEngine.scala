package io.udash.routing

import io.udash._
import io.udash.utils.CallbacksHandler
import io.udash.utils.FilteringUtils._

import scala.annotation.tailrec
import scala.collection.mutable
import scala.reflect.ClassTag
import scala.util.Try

case class StateChangeEvent[S <: State : ClassTag](currentState: S, oldState: S)

/**
  * RoutingEngine handles URL changes by resolving application [[io.udash.core.State]] with
  * matching [[io.udash.core.ViewFactory]]s and rendering views via passed [[io.udash.ViewRenderer]].
  */
class RoutingEngine[HierarchyRoot >: Null <: GState[HierarchyRoot] : ClassTag](
  routingRegistry: RoutingRegistry[HierarchyRoot],
  viewFactoryRegistry: ViewFactoryRegistry[HierarchyRoot],
  viewRenderer: ViewRenderer
) {

  private val currentStateProp = Property(null: HierarchyRoot)
  private val callbacks = new CallbacksHandler[StateChangeEvent[HierarchyRoot]]
  private val statesMap = mutable.LinkedHashMap.empty[HierarchyRoot, (View, Presenter[_ <: HierarchyRoot])]

  /**
    * Handles the URL change. Gets a routing states hierarchy for the provided URL and redraws <b>only</b> changed ViewFactories.
    *
    * @param url URL to be resolved
    */
  def handleUrl(url: Url, fullReload: Boolean = false): Try[Unit] = Try {
    if (fullReload) clearAllPresenters()

    val newState = routingRegistry.matchUrl(url)
    val oldState = currentStateProp.get
    currentStateProp.set(newState)

    val currentStatePath = statesMap.keys.toList
    val newStatePath = getStatePath(Some(newState))

    val samePath = findEqPrefix(newStatePath, currentStatePath)
    val diffPath = findDiffSuffix(newStatePath, currentStatePath)

    val (viewsToLeave, viewsToAdd) = {
      val toUpdateStatesSize = getUpdatablePathSize(diffPath, statesMap.keys.slice(samePath.size, statesMap.size).toList)
      val toRemoveStates = statesMap.slice(samePath.size + toUpdateStatesSize, statesMap.size)
      toRemoveStates.values.foreach { case (_, presenter) => presenter.onClose() }

      val oldViewFactories =
        newStatePath
          .slice(samePath.size, samePath.size + toUpdateStatesSize)
          .zip(statesMap.slice(samePath.size, samePath.size + toUpdateStatesSize).values)(scala.collection.breakOut)
      var i = samePath.size
      statesMap.retain { (_, _) =>
        i -= 1
        i >= 0
      }
      statesMap ++= oldViewFactories

      val viewsToLeave = statesMap.values.map(_._1).toList
      val views = resolvePath(diffPath.slice(toUpdateStatesSize, diffPath.size))
      (viewsToLeave, views)
    }

    diffPath.foldRight(Option(newState)) { (currentState, previousState) =>
      previousState.flatMap(statesMap.get).foreach { case (_, presenter) =>
        presenter.asInstanceOf[Presenter[HierarchyRoot]].handleState(currentState)
      }
      currentState.parentState
    }

    viewRenderer.renderView(viewsToLeave, viewsToAdd)

    if (fullReload || newState != oldState) callbacks.fire(StateChangeEvent(newState, oldState))
  }.recover { case ex: Throwable => statesMap.clear(); throw ex }

  /**
    * Register a callback for the routing state change.
    *
    * @param callback Callback getting StateChangeEvent as arguments
    */
  def onStateChange(callback: StateChangeEvent[HierarchyRoot] => Any): Registration =
    onStateChange({ case x => callback(x) }: callbacks.CallbackType)

  /**
    * Register a callback for the routing state change.
    *
    * The callbacks are executed in order of registration. Registration operations don't preserve callbacks order.
    * Each callback is executed once, exceptions thrown in callbacks are swallowed.
    *
    * @param callback Callback (PartialFunction) getting StateChangeEvent as arguments
    */
  def onStateChange(callback: callbacks.CallbackType): Registration =
    callbacks.register(callback)

  /** @return Current routing state */
  def currentState: HierarchyRoot = currentStateProp.get

  /** @return Property reflecting current routing state */
  def currentStateProperty: ReadableProperty[HierarchyRoot] = currentStateProp.readable

  @tailrec
  private def getStatePath(forState: Option[HierarchyRoot], acc: List[HierarchyRoot] = Nil): List[HierarchyRoot] = forState match {
    case Some(state) => getStatePath(state.parentState, state :: acc)
    case None => acc
  }

  @tailrec
  private def getUpdatablePathSize(path: List[HierarchyRoot], oldPath: List[HierarchyRoot], acc: Int = 0): Int = {
    (path, oldPath) match {
      case (head1 :: tail1, head2 :: tail2)
        if viewFactoryRegistry.matchStateToResolver(head1) == viewFactoryRegistry.matchStateToResolver(head2) =>
          getUpdatablePathSize(tail1, tail2, acc + 1)
      case _ => acc
    }
  }

  private def clearAllPresenters(): Unit = {
    statesMap.values.foreach { case (_, presenter) => presenter.onClose() }
    statesMap.clear()
  }

  private def resolvePath(path: List[HierarchyRoot]): List[View] = {
    path.map { state =>
      val (view, presenter) = viewFactoryRegistry.matchStateToResolver(state).create()
      statesMap(state) = (view, presenter)
      view
    }
  }
}
