package io

package object udash extends io.udash.bindings.Bindings
                        with io.udash.properties.Properties
                        with io.udash.routing.Routing
{
  // Defaults
  type DefaultViewPresenterFactory[S <: State] = io.udash.core.DefaultViewPresenterFactory[S]
  type EmptyPresenter[S <: State] = io.udash.core.EmptyPresenter[S]
  val  Window = io.udash.core.Window

  // Definitions
  val  Url = io.udash.core.Url
  type Url = io.udash.core.Url
  type Presenter[S <: State] = io.udash.core.Presenter[S]
  type ViewPresenter[S <: State] = io.udash.core.ViewPresenter[S]
  type View = io.udash.core.View
  type FinalView = io.udash.core.FinalView
  type State = io.udash.core.State
  type RoutingRegistry[S <: State] = io.udash.core.RoutingRegistry[S]
  type ViewPresenterRegistry[S <: State] = io.udash.core.ViewPresenterRegistry[S]

  // Utils
  type Registration = io.udash.utils.Registration
  type StrictLogging = io.udash.utils.StrictLogging

  // View
  type ViewRenderer = io.udash.view.ViewRenderer
}
