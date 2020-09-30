package io.udash

import io.udash.logging.CrossLogging
import io.udash.properties.PropertyCreator
import io.udash.routing.{RoutingEngine, StateChangeEvent}
import io.udash.utils.CallbacksHandler
import io.udash.view.ViewRenderer
import org.scalajs.dom.Element

/**
 * Root application which is used to start single instance of app.
 *
 * @param routingRegistry     [[io.udash.routing.RoutingRegistry]] implementation, which will be used to match [[io.udash.core.Url]] to [[io.udash.core.State]]
 * @param viewFactoryRegistry [[io.udash.core.ViewFactoryRegistry]] implementation, which will be used to match [[io.udash.core.State]] into [[io.udash.core.ViewFactory]]
 * @tparam HierarchyRoot Should be a sealed trait which extends [[io.udash.core.State]].
 */
class Application[HierarchyRoot >: Null <: GState[HierarchyRoot] : PropertyCreator](
  routingRegistry: RoutingRegistry[HierarchyRoot],
  viewFactoryRegistry: ViewFactoryRegistry[HierarchyRoot],
  urlChangeProvider: UrlChangeProvider = new WindowUrlFragmentChangeProvider
) extends CrossLogging {

  private var rootElement: Element = _
  private val routingFailureListeners = new CallbacksHandler[Throwable]
  private lazy val viewRenderer = new ViewRenderer(rootElement)
  private lazy val routingEngine = new RoutingEngine[HierarchyRoot](routingRegistry, viewFactoryRegistry, viewRenderer)

  /**
   * Starts the application using selected element as root.
   *
   * @param attachElement Root element of application.
   */
  final def run(attachElement: Element): Unit = {
    rootElement = attachElement

    urlChangeProvider.initialize()
    urlChangeProvider.onFragmentChange { frag =>
      routingEngine.handleUrl(frag)
        .recover { case ex: Throwable => handleRoutingFailure(ex) }
    }
    routingEngine.handleUrl(urlChangeProvider.currentFragment)
      .recover { case ex: Throwable => handleRoutingFailure(ex) }
  }

  def reload(): Unit = {
    routingEngine.handleUrl(urlChangeProvider.currentFragment, fullReload = true)
  }

  /**
   * Registers callback which will be called after routing failure.
   *
   * The callbacks are executed in order of registration. Registration operations don't preserve callbacks order.
   * Each callback is executed once, exceptions thrown in callbacks are swallowed.
   */
  def onRoutingFailure(listener: routingFailureListeners.CallbackType): Registration =
    routingFailureListeners.register(listener)

  protected def handleRoutingFailure(ex: Throwable): Unit = {
    logger.error(s"Unhandled URL: ${urlChangeProvider.currentFragment}. Error: ${ex.getMessage}")
    routingFailureListeners.fire(ex)
  }

  /**
   * Changes application routing state to the provided one.
   *
   * @param state          New application routing state,
   * @param replaceCurrent indicates whether new state should replace the current one in history
   */
  def goTo(state: HierarchyRoot, replaceCurrent: Boolean = false): Unit = {
    val url = routingRegistry.matchState(state)
    urlChangeProvider.changeFragment(url, replaceCurrent)
  }

  /** Redirects to selected URL. */
  def redirectTo(url: Url): Unit =
    urlChangeProvider.changeUrl(url)

  /**
   * Register callback for routing state change.
   *
   * @param callback Callback getting newState and oldState as arguments.
   */
  def onStateChange(callback: StateChangeEvent[HierarchyRoot] => Unit): Registration =
    routingEngine.onStateChange(callback)

  /**
   * @return URL matched to the provided state.
   */
  def matchState(state: HierarchyRoot): Url =
    routingRegistry.matchState(state)

  /** Current application routing state. */
  def currentState: HierarchyRoot =
    routingEngine.currentState

  /** @return Property reflecting current routing state */
  def currentStateProperty: ReadableProperty[HierarchyRoot] =
    routingEngine.currentStateProperty

  /** @return the URL part representing the current frontend routing state. */
  def currentUrl: Url =
    urlChangeProvider.currentFragment

}
