package io.udash

import io.udash.routing.{StateChangeEvent, WindowUrlChangeProvider}
import org.scalajs.dom.Element

import scala.reflect.ClassTag

/**
  * Root application which is used to start single instance of app.
  *
  * @param routingRegistry [[io.udash.core.RoutingRegistry]] implementation, which will be used to match [[io.udash.core.Url]] to [[io.udash.core.State]]
  * @param viewPresenterRegistry [[io.udash.core.ViewPresenterRegistry]] implementation, which will be used to match [[io.udash.core.State]] into [[io.udash.core.ViewPresenter]]
  * @param rootState The instance of [[io.udash.core.State]] which will treated as main state.
  * @tparam S Should be a sealed trait which extends [[io.udash.core.State]].
  */
class Application[S <: State : ClassTag](routingRegistry: RoutingRegistry[S],
                                         viewPresenterRegistry: ViewPresenterRegistry[S],
                                         rootState: S,
                                         urlChangeProvider: UrlChangeProvider = WindowUrlChangeProvider) {
  private var rootElement: Element = _
  private lazy val viewRenderer = new ViewRenderer(rootElement)
  private lazy val routingEngine = new RoutingEngine[S](routingRegistry, viewPresenterRegistry, viewRenderer, rootState)

  /**
    * Starts the application using selected element as root.
    *
    * @param attachElement Root element of application.
    */
  def run(attachElement: Element): Unit = {
    rootElement = attachElement

    urlChangeProvider.onFragmentChange(routingEngine.handleUrl)
    routingEngine.handleUrl(urlChangeProvider.currentFragment)
  }

  /**
    * Changes application routing state to the provided one.
    *
    * @param state New application routing state,
    */
  def goTo(state: S): Unit = {
    val url = routingRegistry.matchState(state)
    urlChangeProvider.changeFragment(url)
  }

  /**
    * Redirects to selected URL.
    */
  def redirectTo(url: String): Unit = {
    urlChangeProvider.changeUrl(url)
  }

  /**
    * Register callback for routing state change.
    *
    * @param callback Callback getting newState and oldState as arguments.
    */
  def onStateChange(callback: StateChangeEvent[S] => Unit) = {
    routingEngine.onStateChange(callback)
  }

  /**
    * @return URL matched to the provided state.
    */
  def matchState(state: S): Url = routingRegistry.matchState(state)

  /** Current application routing state. */
  def currentState: S = routingEngine.currentState
}
