package io.udash.testing

import io.udash._

class TestViewFactory[T <: TestState] extends ViewFactory[T] {
  val view = new TestView
  val presenter = new TestPresenter[T]

  override def create(): (View, Presenter[T]) = (view, presenter)
}

class TestView extends ContainerView {
  import scalatags.JsDom.all._
  var lastChild: View = _
  var renderingCounter = 0

  override def renderChild(view: Option[View]): Unit = {
    view.foreach(_.getTemplate)
    lastChild = view.orNull
  }

  override def getTemplate: Modifier = {
    renderingCounter += 1
    div().render
  }
}

class TestFinalView extends FinalView {
  import scalatags.JsDom.all._
  var renderingCounter = 0

  override def getTemplate: Modifier = {
    renderingCounter += 1
    div().render
  }
}

class TestPresenter[T <: TestState] extends Presenter[T] {
  var lastHandledState: T = _
  override def handleState(state: T): Unit = lastHandledState = state
}