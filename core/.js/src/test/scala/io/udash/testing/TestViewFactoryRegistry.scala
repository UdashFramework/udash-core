package io.udash.testing

import io.udash._

import scala.collection.mutable

class TestViewFactoryRegistry(vp: Map[TestState, () => ViewFactory[_ <: TestState]],
  default: () => ViewFactory[_ <: TestState]) extends ViewFactoryRegistry[TestState] {
  var statesHistory: mutable.ArrayBuffer[TestState] = mutable.ArrayBuffer.empty

  override def matchStateToResolver(state: TestState): ViewFactory[_ <: TestState] = {
    if (state == ThrowExceptionState) throw new RuntimeException("ThrowExceptionState")
    statesHistory.append(state)
    vp.get(state).map(_.apply()).getOrElse(default())
  }
}
