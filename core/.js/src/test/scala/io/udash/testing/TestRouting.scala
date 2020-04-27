package io.udash.testing

import io.udash._
import io.udash.routing.RoutingEngine

trait TestRouting {

  var routing: TestRoutingRegistry = _
  var viewFactory: TestViewFactory[ErrorState.type] = _
  var vpRegistry: TestViewFactoryRegistry = _
  var renderer: TestViewRenderer = _
  private[udash] var routingEngine: RoutingEngine[TestState] = _

  protected final def initTestRouting(routing: TestRoutingRegistry = new TestRoutingRegistry,
    state2vp: Map[TestState, () => ViewFactory[_ <: TestState]] = Map.empty
  ): Unit = {
    this.routing = routing
    viewFactory = new TestViewFactory[ErrorState.type]
    vpRegistry = new TestViewFactoryRegistry(state2vp, viewFactory)
    renderer = new TestViewRenderer
  }

  protected final def initTestRoutingEngine(routing: TestRoutingRegistry = new TestRoutingRegistry,
    state2vp: Map[TestState, () => ViewFactory[_ <: TestState]] = Map.empty
  ): Unit = {
    initTestRouting(routing, state2vp)
    routingEngine = new RoutingEngine[TestState](routing, vpRegistry, renderer)
  }
}