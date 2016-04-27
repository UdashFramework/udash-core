package io.udash.testing

import io.udash._

import scala.collection.mutable

class TestViewPresenterRegistry(vp: Map[TestState, ViewPresenter[_ <: TestState]], default: ViewPresenter[ErrorState.type]) extends ViewPresenterRegistry[TestState] {
  var statesHistory: mutable.ArrayBuffer[TestState] = mutable.ArrayBuffer.empty

  override def matchStateToResolver(state: TestState): ViewPresenter[_ <: TestState] = {
    statesHistory.append(state)
    vp.getOrElse(state, default)
  }
}
