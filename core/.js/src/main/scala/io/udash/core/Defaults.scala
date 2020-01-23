package io.udash.core

/**
 * Creates view with [[io.udash.core.EmptyPresenter]]. Used for static views.
 *
 * By default, instances of this class are compared by class name to prevent rerendering of static views.
 * This behaviour can be opted out of by overriding equals/hashCode.
 **/
abstract class StaticViewFactory[S <: State](viewCreator: () => View) extends ViewFactory[S] {
  override def create(): (View, EmptyPresenter.type) =
    (viewCreator(), EmptyPresenter)

  override def equals(other: Any): Boolean = getClass.equals(other.getClass)
  override def hashCode(): Int = getClass.hashCode()
}

/** Ignores state changes. Useful for static views. */
object EmptyPresenter extends Presenter[State] {
  override def handleState(state: State): Unit = ()
}