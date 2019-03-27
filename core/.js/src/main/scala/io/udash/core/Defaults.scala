package io.udash.core

/** Creates view with [[io.udash.core.EmptyPresenter]]. Useful for static views. */
class StaticViewFactory[S <: State](viewCreator: () => View) extends ViewFactory[S] {
  override def create(): (View, EmptyPresenter.type) =
    (viewCreator(), EmptyPresenter)
}

/** Ignores state changes. Useful for static views. */
object EmptyPresenter extends Presenter[State] {
  override def handleState(state: State): Unit = ()
}