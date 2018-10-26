package io.udash.testing

import io.udash._

sealed abstract class TestState(val parentState: Option[ContainerTestState]) extends State {
  override type HierarchyRoot = TestState
}
sealed abstract class ContainerTestState(parentState: Option[ContainerTestState]) extends TestState(parentState) with ContainerState
sealed abstract class FinalTestState(parentState: Option[ContainerTestState]) extends TestState(parentState) with FinalState

case class RootState(sth: Option[Int]) extends ContainerTestState(None)
case class ClassState(arg: String, arg2: Int) extends FinalTestState(Some(RootState(None)))
case object ObjectState extends ContainerTestState(Some(RootState(None)))
case object ThrowExceptionState extends ContainerTestState(Some(RootState(None)))
case object NextObjectState extends FinalTestState(Some(ObjectState))
case object ErrorState extends FinalTestState(Some(RootState(None)))
