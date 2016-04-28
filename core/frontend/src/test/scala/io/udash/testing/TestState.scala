package io.udash.testing

import io.udash._

sealed abstract class TestState(val parentState: TestState) extends State
case object RootState extends TestState(null)
case class ClassState(arg: String, arg2: Int) extends TestState(RootState)
case object ObjectState extends TestState(RootState)
case object NextObjectState extends TestState(ObjectState)
case object ErrorState extends TestState(RootState)
