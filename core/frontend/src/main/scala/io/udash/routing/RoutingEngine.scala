package io.udash.routing

import io.udash._
import io.udash.properties.{ImmutableValue, PropertyCreator}
import io.udash.utils.FilteringUtils._

import scala.annotation.tailrec
import scala.collection.mutable
import scala.reflect.ClassTag
import scala.scalajs.concurrent.JSExecutionContext

case class StateChangeEvent[S <: State : ClassTag](currentState: S, oldState: S)

/**
  * RoutingEngine handles URL changes by resolving application [[io.udash.core.State]] with
  * matching [[io.udash.core.ViewPresenter]]s and rendering views via passed [[io.udash.ViewRenderer]].
  */
class RoutingEngine[S <: State : ClassTag : ImmutableValue](routingRegistry: RoutingRegistry[S], viewPresenterRegistry: ViewPresenterRegistry[S],
                                                            viewRenderer: ViewRenderer, rootState: S) {
  private val currentStateProp = Property[S](implicitly[PropertyCreator[S]], JSExecutionContext.queue)
  private val callbacks = mutable.ArrayBuffer[StateChangeEvent[S] => Any]()
  private val statesMap = mutable.LinkedHashMap[S, (View, Presenter[_ <: S])]()

  /**
    * Handles the URL change. Gets a routing states hierarchy for the provided URL and redraws <b>only</b> changed ViewPresenters.
    *
    * @param url URL to be resolved
    */
  def handleUrl(url: Url): Unit = {
    val newState = routingRegistry.matchUrl(url)
    val oldState = currentStateProp.get
    currentStateProp.set(newState)

    val currentStatePath = statesMap.keys.toList
    val newStatePath = getStatePath(newState)

    val samePath = findEqPrefix(newStatePath, currentStatePath)
    val diffPath = findDiffSuffix(newStatePath, currentStatePath)

    val (viewsToLeave, viewsToAdd) =
      if (samePath.isEmpty) {
        val views = renderPath(diffPath)
        (Nil, views)
      } else {
        val toUpdateStatesSize = getUpdatablePathSize(diffPath, statesMap.keys.slice(samePath.size, statesMap.size).toList)
        val toRemoveStates = statesMap.slice(samePath.size + toUpdateStatesSize, statesMap.size)
        toRemoveStates.values.foreach { case (_, presenter) => presenter.onClose() }

        val oldViewPresenters =
          newStatePath
            .slice(samePath.size, samePath.size + toUpdateStatesSize)
            .zip(statesMap.slice(samePath.size, samePath.size + toUpdateStatesSize).values)(scala.collection.breakOut)
        var i = samePath.size
        statesMap.retain { (_, _) =>
          i -= 1
          i >= 0
        }
        statesMap ++= oldViewPresenters

        val viewsToLeave = statesMap.values.map(_._1).toList
        val views = renderPath(diffPath.slice(toUpdateStatesSize, diffPath.size))
        (viewsToLeave, views)
      }

    viewRenderer.renderView(viewsToLeave, viewsToAdd)

    diffPath.foldRight(newState) { (currentState, previousState) =>
      statesMap.get(previousState).foreach { case (_, presenter) =>
        presenter.asInstanceOf[Presenter[S]].handleState(currentState)
      }
      currentState.parentState.asInstanceOf[S]
    }

    if (newState != oldState) callbacks.foreach(_.apply(StateChangeEvent(newState, oldState)))
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
  def currentState: S = currentStateProp.get

  /** @return Property reflecting current routing state */
  def currentStateProperty: ReadableProperty[S] = currentStateProp.transform(identity)

  @tailrec
  private def getStatePath(forState: S, acc: List[S] = Nil): List[S] = forState match {
    case state if state == rootState => rootState :: acc
    case state: S => getStatePath(state.parentState.asInstanceOf[S], state :: acc)
    case _ => acc
  }

  @tailrec
  private def getUpdatablePathSize(path: List[S], oldPath: List[S], acc: Int = 0): Int = {
    (path, oldPath) match {
      case (head1 :: tail1, head2 :: tail2)
        if viewPresenterRegistry.matchStateToResolver(head1) == viewPresenterRegistry.matchStateToResolver(head2) =>
        getUpdatablePathSize(tail1, tail2, acc + 1)
      case _ => acc
    }
  }

  private def renderPath(path: List[S]): List[View] = {
    path.map { state =>
      val (view, presenter) = viewPresenterRegistry.matchStateToResolver(state).create()
      statesMap += (state -> (view, presenter))
      view
    }
  }
}
