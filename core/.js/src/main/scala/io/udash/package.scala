package io

package object udash
  extends io.udash.bindings.Bindings
  with io.udash.properties.Properties
    with io.udash.routing.Routing {

  // Defaults
  type StaticViewFactory[S <: State] = io.udash.core.StaticViewFactory[S]
  final val EmptyPresenter = io.udash.core.EmptyPresenter

  // Definitions
  final val Url = io.udash.core.Url
  type Url = io.udash.core.Url

  type Presenter[S <: State] = io.udash.core.Presenter[S]

  type ViewFactory[S <: State] = io.udash.core.ViewFactory[S]

  type View = io.udash.core.View
  type ContainerView = io.udash.core.ContainerView

  type State = io.udash.core.State
  type GState[HRoot <: State] = io.udash.core.State {type HierarchyRoot = HRoot}

  type RoutingRegistry[HierarchyRoot <: State] = io.udash.routing.RoutingRegistry[HierarchyRoot]

  type ViewFactoryRegistry[HierarchyRoot <: State] = io.udash.core.ViewFactoryRegistry[HierarchyRoot]

  // Utils
  type HasModelPropertyCreator[T] = io.udash.properties.HasModelPropertyCreator[T]
  type Registration = io.udash.utils.Registration
  type FileUploader = io.udash.utils.FileUploader
  final val FileUploader = io.udash.utils.FileUploader

  // View
  type ViewRenderer = io.udash.view.ViewRenderer
}
