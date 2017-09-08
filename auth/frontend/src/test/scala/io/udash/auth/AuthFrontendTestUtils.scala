package io.udash.auth

import io.udash.{ContainerState, State}

trait AuthFrontendTestUtils {
  sealed trait TestStates extends State {
    override type HierarchyRoot = TestStates
    override def parentState: Option[ContainerState with HierarchyRoot] = None
  }
  case object SomeState extends TestStates
  case object SecondState extends TestStates
  case object ThirdState extends TestStates
}
