package io.udash.testing

import io.udash._
import io.udash.routing.RoutingEngine

trait TestRouting {
  self: UdashFrontendTest =>
  var routing: TestRoutingRegistry = _
  var viewPresenter: TestViewPresenter[ErrorState.type] = _
  var vpRegistry: TestViewPresenterRegistry = _
  var renderer: TestViewRenderer = _
  var routingEngine: RoutingEngine[TestState] = _

  protected def initTestRouting(routing: TestRoutingRegistry = new TestRoutingRegistry,
                                state2vp: Map[TestState, ViewPresenter[_ <: TestState]] = Map.empty
                               ): Unit = {
    this.routing = routing
    viewPresenter = new TestViewPresenter[ErrorState.type]
    vpRegistry = new TestViewPresenterRegistry(state2vp, viewPresenter)
    renderer = new TestViewRenderer
  }

  protected def initTestRoutingEngine(routing: TestRoutingRegistry = new TestRoutingRegistry,
                                      state2vp: Map[TestState, ViewPresenter[_ <: TestState]] = Map.empty
                                     ): Unit = {
    initTestRouting(routing, state2vp)
    routingEngine = new RoutingEngine[TestState](routing, vpRegistry, renderer, RootState)
  }
}