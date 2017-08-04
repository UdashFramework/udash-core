package io.udash.core

/** Creates view with [[io.udash.core.EmptyPresenter]]. Useful for static views. */
abstract class StaticViewFactory[S <: State](viewCreator: () => View) extends ViewFactory[S] {
  override def create(): (View, Presenter[S]) =
    (viewCreator.apply(), new EmptyPresenter[S])
}

/** Ignores state changes. Useful for static views. */
class EmptyPresenter[S <: State] extends Presenter[S] {
  override def handleState(state: S): Unit = ()
}