package io.udash.testing

import io.udash._
import org.scalajs.dom._

class TestViewPresenter[T <: TestState] extends ViewPresenter[T] {
  val view = new TestView
  val presenter = new TestPresenter[T]

  override def create(): (View, Presenter[T]) = (view, presenter)
}

class TestView extends View {
  var lastChild: View = _
  var renderingCounter = 0

  override def renderChild(view: View): Unit = {
    if (view != null) view.getTemplate
    lastChild = view
  }

  override def getTemplate: Element = {
    renderingCounter += 1
    null
  }
}

class TestPresenter[T <: TestState] extends Presenter[T] {
  var lastHandledState: State = _
  override def handleState(state: T): Unit = lastHandledState = state
}