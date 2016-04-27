package io.udash.routing

import io.udash._
import io.udash.utils.FilteringUtils._

import scala.annotation.tailrec
import scala.collection.{immutable, mutable}
import scala.reflect.ClassTag

case class StateChangeEvent[S <: State : ClassTag](currentState: S, oldState: S)

/**
  * RoutingEngine handles URL changes by resolving application [[io.udash.core.State]] with
  * matching [[io.udash.core.ViewPresenter]]s and rendering views via passed [[io.udash.ViewRenderer]].
  */
class RoutingEngine[S <: State : ClassTag](routingRegistry: RoutingRegistry[S], viewPresenterRegistry: ViewPresenterRegistry[S], viewRenderer: ViewRenderer, rootState: S) {
  private var current: S = _
  private val callbacks = mutable.ArrayBuffer[StateChangeEvent[S] => Any]()
  private var statesMap = immutable.ListMap[S, (View, Presenter[_ <: S])]()

  /**
    * Handles the URL change. Gets a routing states hierarchy for the provided URL and redraws <b>only</b> changed ViewPresenters.
    *
    * @param url URL to be resolved
    */
  def handleUrl(url: Url): Unit = {
    val newState = routingRegistry.matchUrl(url)

    val currentStatePath = statesMap.keys.toList
    val newStatePath = getStatePath(newState)

    val samePath = findEqPrefix(newStatePath, currentStatePath)
    val diffPath = findDiffSuffix(newStatePath, currentStatePath)

    if (samePath.isEmpty) {
      val views = renderPath(diffPath)
      viewRenderer.renderView(List(), views)
    } else {
      val toUpdateStates = getUpdatablePath(diffPath, statesMap.keys.slice(samePath.size, statesMap.size).toList)
      val toRemoveStates = statesMap.slice(samePath.size + toUpdateStates.size, statesMap.size)

      toRemoveStates.values.foreach { case (view, presenter) => presenter.onClose() }

      val oldVPs = newStatePath
        .slice(samePath.size, samePath.size + toUpdateStates.size)
        .zip(statesMap.slice(samePath.size, samePath.size + toUpdateStates.size).values)
        .toMap
      statesMap = statesMap.slice(0, samePath.size) ++ oldVPs

      val viewsToLeave = statesMap.values.map(_._1).toList
      val views = renderPath(diffPath.slice(toUpdateStates.size, diffPath.size))
      viewRenderer.renderView(viewsToLeave, views)
    }

    diffPath.reverse.foldLeft(newState)((previousState, currentState) => {
      statesMap.get(previousState).foreach { case (view, presenter) =>
        presenter.asInstanceOf[Presenter[S]].handleState(currentState)
      }
      currentState.parentState.asInstanceOf[S]
    })

    val oldState = current
    current = newState
    callbacks.foreach(_.apply(StateChangeEvent(newState, oldState)))
  }

  /**
    * Register a callback for the routing state change.
    *
    * @param callback Callback getting StateChangeEvent as arguments
    */
  def onStateChange(callback: StateChangeEvent[S] => Any): Registration = {
    callbacks += callback
    new Registration {
      override def cancel(): Unit = callbacks -= callback
    }
  }

  /** @return Current routing state */
  def currentState: S = current

  @tailrec
  private def getStatePath(forState: S, acc: List[S] = Nil): List[S] = forState match {
    case state if state == rootState => rootState :: acc
    case state: S => getStatePath(state.parentState.asInstanceOf[S], state :: acc)
    case _ => acc
  }

  @tailrec
  private def getUpdatablePath(path: List[S], oldPath: List[S], acc: List[S] = Nil): List[S] = {
    (path, oldPath) match {
      case (head1 :: tail1, head2 :: tail2)
        if viewPresenterRegistry.matchStateToResolver(head1) == viewPresenterRegistry.matchStateToResolver(head2) =>
        getUpdatablePath(tail1, tail2, acc :+ head1)
      case _ => acc
    }
  }

  private def renderPath(path: List[S]): List[View] = {
    path.map(state => {
      val viewPresenter = viewPresenterRegistry.matchStateToResolver(state).create()
      statesMap = statesMap + (state -> (viewPresenter._1, viewPresenter._2))
      viewPresenter._1
    })
  }
}
