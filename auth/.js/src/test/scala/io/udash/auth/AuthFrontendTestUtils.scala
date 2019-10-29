package io.udash.auth

import io.udash.State

trait AuthFrontendTestUtils {
  sealed trait TestStates extends State {
    override type HierarchyRoot = TestStates
    override def parentState: Option[HierarchyRoot] = None
  }
  case object SomeState extends TestStates
  case object SecondState extends TestStates
  case object ThirdState extends TestStates
}
