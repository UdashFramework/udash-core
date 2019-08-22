package io.udash.testing

import io.udash.properties.seq.ReadableSeqProperty
import io.udash.properties.single.ReadableProperty
import org.scalatest.{Assertion, Matchers}

trait CoreTestUtils extends Matchers {
  def ensureNoListeners(seqProperty: ReadableSeqProperty[_, _ <: ReadableProperty[_]]): Assertion = {
    seqProperty.listenersCount() should be(0)
    seqProperty.structureListenersCount() should be(0)
    seqProperty.elemProperties.map(_.listenersCount()).sum should be(0)
  }

  def valuesOfType[ReturnType](obj: Any): List[ReturnType] = macro io.udash.macros.AllValuesMacro.ofType[ReturnType]
}
