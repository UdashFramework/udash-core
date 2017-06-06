package io.udash.testing

import io.udash._
import org.scalajs.dom._

class TestViewPresenter[T <: TestState] extends ViewPresenter[T] {
  val view = new TestView
  val presenter = new TestPresenter[T]

  override def create(): (View, Presenter[T]) = (view, presenter)
}

class TestView extends View {
  import scalatags.JsDom.all._
  var lastChild: Option[View] = _
  var renderingCounter = 0

  override def renderChild(view: Option[View]): Unit = {
    view.foreach(_.getTemplate)
    lastChild = view
  }

  override def getTemplate: Modifier = {
    renderingCounter += 1
    div().render
  }
}

class TestPresenter[T <: TestState] extends Presenter[T] {
  var lastHandledState: State = _
  override def handleState(state: T): Unit = lastHandledState = state
}