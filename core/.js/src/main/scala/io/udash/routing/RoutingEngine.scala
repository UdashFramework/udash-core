package io.udash.routing

import com.avsystem.commons._
import com.avsystem.commons.misc.AbstractCase
import com.github.ghik.silencer.silent
import io.udash._
import io.udash.logging.CrossLogging
import io.udash.properties.PropertyCreator
import io.udash.utils.CallbacksHandler
import io.udash.utils.FilteringUtils._

import scala.annotation.tailrec

final case class StateChangeEvent[S <: State](currentState: S, oldState: S) extends AbstractCase

/**
 * RoutingEngine handles URL changes by resolving application [[io.udash.core.State]] with
 * matching [[io.udash.core.ViewFactory]]s and rendering views via passed [[io.udash.ViewRenderer]].
 */
class RoutingEngine[HierarchyRoot >: Null <: GState[HierarchyRoot] : PropertyCreator](
  routingRegistry: RoutingRegistry[HierarchyRoot],
  viewFactoryRegistry: ViewFactoryRegistry[HierarchyRoot],
  viewRenderer: ViewRenderer
) extends CrossLogging {

  private val currentStateProp = Property(null: HierarchyRoot)
  private val callbacks = new CallbacksHandler[StateChangeEvent[HierarchyRoot]]
  private val statesMap = MLinkedHashMap.empty[HierarchyRoot, (View, Presenter[_ <: HierarchyRoot])]

  /**
   * Handles the URL change. Gets a routing states hierarchy for the provided URL and redraws <b>only</b> changed ViewFactories.
   *
   * @param url URL to be resolved
   */
  def handleUrl(url: Url, fullReload: Boolean = false): Try[Unit] = Try {
    if (fullReload) {
      cleanup(statesMap.values)
      statesMap.clear()
    }

    val newState = routingRegistry.matchUrl(url)
    val oldState = currentStateProp.get
    currentStateProp.set(newState)

    val currentStatePath = statesMap.keys.toList
    val newStatePath = getStatePath(Some(newState))

    val samePath = findEqPrefix(newStatePath.iterator, currentStatePath.iterator)
    val diffPath = findDiffSuffix(newStatePath.iterator, currentStatePath.iterator)

    val (viewsToLeave, viewsToAdd) = {
      val toUpdateStatesSize = getUpdatablePathSize(diffPath.iterator, statesMap.keys.view(samePath.size, statesMap.size).iterator)
      val toRemoveStates = statesMap.slice(samePath.size + toUpdateStatesSize, statesMap.size)
      cleanup(toRemoveStates.values)

      val oldViewFactories =
        newStatePath
          .slice(samePath.size, samePath.size + toUpdateStatesSize)
          .zip(statesMap.slice(samePath.size, samePath.size + toUpdateStatesSize).values)
      var i = samePath.size
      statesMap.retain { (_, _) =>
        i -= 1
        i >= 0
      }: @silent("deprecated")
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

    viewRenderer.renderView(viewsToLeave.iterator, viewsToAdd)

    if (fullReload || newState != oldState) callbacks.fire(StateChangeEvent(newState, oldState))
  }.recover { case ex: Throwable => statesMap.clear(); throw ex }

  /**
   * Register a callback for the routing state change.
   *
   * @param callback Callback getting StateChangeEvent as arguments
   */
  def onStateChange(callback: StateChangeEvent[HierarchyRoot] => Any): Registration =
    onStateChange({
      case x => callback(x)
    }: callbacks.CallbackType)

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

  private def getUpdatablePathSize(path: Iterator[HierarchyRoot], oldPath: Iterator[HierarchyRoot]): Int =
    path.zip(oldPath).takeWhile {
      case (h1, h2) => viewFactoryRegistry.matchStateToResolver(h1) == viewFactoryRegistry.matchStateToResolver(h2)
    }.length

  private def cleanup(state: Iterable[(View, Presenter[_])]): Unit = {
    state.foreach { case (view, presenter) =>
      Try(view.onClose()).failed.foreach(logger.warn("Error closing view.", _))
      Try(presenter.onClose()).failed.foreach(logger.warn("Error closing presenter.", _))
    }
  }

  private def resolvePath(path: List[HierarchyRoot]): List[View] = {
    path.map { state =>
      val (view, presenter) = viewFactoryRegistry.matchStateToResolver(state).create()
      statesMap(state) = (view, presenter)
      view
    }
  }
}
