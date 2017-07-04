package io.udash.testing

import io.udash._

sealed abstract class TestState(val parentState: Option[ContainerTestState]) extends State
sealed abstract class ContainerTestState(parentState: Option[ContainerTestState]) extends TestState(parentState) with ContainerState
sealed abstract class FinalTestState(parentState: Option[ContainerTestState]) extends TestState(parentState) with FinalState

case object RootState extends ContainerTestState(None)
case class ClassState(arg: String, arg2: Int) extends FinalTestState(Some(RootState))
case object ObjectState extends ContainerTestState(Some(RootState))
case object NextObjectState extends FinalTestState(Some(ObjectState))
case object ErrorState extends FinalTestState(Some(RootState))
