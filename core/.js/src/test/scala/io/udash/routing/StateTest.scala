package io.udash.routing

import io.udash._
import io.udash.testing._

class StateTest extends UdashFrontendTest with TestRouting {

  "State" should {
    "know hierarchy root" in {
      """object TestingStates {
        sealed abstract class TestState(val parentState: Option[ContainerTestState]) extends State {
          override type HierarchyRoot = TestState
        }
        sealed abstract class ContainerTestState(parentState: Option[ContainerTestState]) extends TestState(parentState) with ContainerState
        sealed abstract class FinalTestState(parentState: Option[ContainerTestState]) extends TestState(parentState)

        case object RootState extends ContainerTestState(None)
        case class ClassState(arg: String, arg2: Int) extends FinalTestState(Some(RootState))
        case object ObjectState extends ContainerTestState(Some(RootState))
        case object NextObjectState extends FinalTestState(Some(ObjectState))
        case object ErrorState extends FinalTestState(Some(RootState))
      }""" should compile

      """object TestingStates {
        sealed abstract class TestState(val parentState: Option[ContainerTestState]) extends State {
          override type HierarchyRoot = TestState
        }
        sealed abstract class ContainerTestState(parentState: Option[ContainerTestState]) extends TestState(parentState) with ContainerState
        sealed abstract class FinalTestState(parentState: Option[ContainerTestState]) extends TestState(parentState)

        case object RootState extends ContainerTestState(None)
        case class ClassState(arg: String, arg2: Int) extends FinalTestState(Some(RootState))
        case object ObjectState extends ContainerTestState(Some(RootState))
        case object NextObjectState extends FinalTestState(Some(ObjectState))
        case object ErrorState extends FinalTestState(Some(RootState))

        sealed abstract class TestState2(val parentState: Option[ContainerTestState2]) extends State {
          override type HierarchyRoot = TestState2
        }
        sealed abstract class ContainerTestState2(parentState: Option[ContainerTestState2]) extends TestState2(parentState) with ContainerState
        sealed abstract class FinalTestState2(parentState: Option[ContainerTestState2]) extends TestState2(parentState)

        case object RootState2 extends ContainerTestState2(None)
        case class ClassState2(arg: String, arg2: Int) extends FinalTestState2(Some(RootState2))
        case object ObjectState2 extends ContainerTestState2(Some(RootState2))
        case object NextObjectState2 extends FinalTestState2(Some(ObjectState2))
        case object ErrorState2 extends FinalTestState2(Some(RootState2))
      }""" should compile

      """object TestingStates {
        sealed abstract class TestState(val parentState: Option[ContainerTestState]) extends State {
          override type HierarchyRoot = TestState
        }
        sealed abstract class ContainerTestState(parentState: Option[ContainerTestState]) extends TestState(parentState) with ContainerState
        sealed abstract class FinalTestState(parentState: Option[ContainerTestState]) extends TestState(parentState)

        case object RootState extends ContainerTestState(None)
        case class ClassState(arg: String, arg2: Int) extends FinalTestState(Some(RootState))
        case object ObjectState extends ContainerTestState(Some(RootState))
        case object NextObjectState extends FinalTestState(Some(ObjectState))
        case object ErrorState extends FinalTestState(Some(RootState))

        sealed abstract class TestState2(val parentState: Option[ContainerTestState2]) extends State {
          override type HierarchyRoot = TestState2
        }
        sealed abstract class ContainerTestState2(parentState: Option[ContainerTestState2]) extends TestState2(parentState) with ContainerState
        sealed abstract class FinalTestState2(parentState: Option[ContainerTestState2]) extends TestState2(parentState)

        case object RootState2 extends ContainerTestState2(None)
        case class ClassState2(arg: String, arg2: Int) extends FinalTestState2(Some(RootState2))
        case object ObjectState2 extends ContainerTestState2(Some(RootState2))
        case object NextObjectState2 extends FinalTestState2(Some(ObjectState2))
        case object ErrorState2 extends FinalTestState2(Some(RootState)) // wrong parent
      }""" shouldNot compile

      """object TestingStates {
        sealed abstract class TestState(val parentState: Option[ContainerTestState]) extends State {
          override type HierarchyRoot = TestState
        }
        sealed abstract class ContainerTestState(parentState: Option[ContainerTestState]) extends TestState(parentState) with ContainerState
        sealed abstract class FinalTestState(parentState: Option[ContainerTestState]) extends TestState(parentState)

        case object RootState extends ContainerTestState(None)
        case class ClassState(arg: String, arg2: Int) extends FinalTestState(Some(RootState))
        case object ObjectState extends ContainerTestState(Some(RootState))
        case object NextObjectState extends FinalTestState(Some(ObjectState))
        case object ErrorState extends FinalTestState(Some(RootState))

        sealed abstract class TestState2(val parentState: Option[ContainerTestState2]) extends State {
          override type HierarchyRoot = TestState2
        }
        sealed abstract class ContainerTestState2(parentState: Option[ContainerTestState2]) extends TestState2(parentState) with ContainerState
        sealed abstract class FinalTestState2(parentState: Option[ContainerTestState2]) extends TestState2(parentState)

        case object RootState2 extends ContainerTestState2(None)
        case class ClassState2(arg: String, arg2: Int) extends FinalTestState2(Some(RootState2))
        case object ObjectState2 extends ContainerTestState2(Some(RootState2))
        case object NextObjectState2 extends FinalTestState2(Some(ObjectState2))
        case object ErrorState2 extends FinalTestState(Some(RootState2)) // wrong extend
      }""" shouldNot compile
    }

    "not allow to use regular state as parent" in {
      """object TestingStates {
        sealed abstract class TestState(val parentState: Option[ContainerTestState]) extends State {
          override type HierarchyRoot = TestState
        }
        sealed abstract class ContainerTestState(parentState: Option[ContainerTestState]) extends TestState(parentState) with ContainerState
        sealed abstract class FinalTestState(parentState: Option[ContainerTestState]) extends TestState(parentState)

        case object RootState extends ContainerTestState(None)
        case class ClassState(arg: String, arg2: Int) extends FinalTestState(Some(RootState))
        case object ObjectState extends ContainerTestState(Some(RootState))
        case object NextObjectState extends FinalTestState(Some(ObjectState))
        case object ErrorState extends FinalTestState(Some(RootState))
      }""" should compile

      """object TestingStates {
        sealed abstract class TestState(val parentState: Option[FinalTestState]) extends State {  // wrong parent type
          override type HierarchyRoot = TestState
        }
        sealed abstract class ContainerTestState(parentState: Option[FinalTestState]) extends TestState(parentState) with ContainerState
        sealed abstract class FinalTestState(parentState: Option[FinalTestState]) extends TestState(parentState)

        case object RootState extends ContainerTestState(None)
        case class ClassState(arg: String, arg2: Int) extends FinalTestState(Some(RootState))
        case object ObjectState extends ContainerTestState(Some(RootState))
        case object NextObjectState extends FinalTestState(Some(ObjectState))
        case object ErrorState extends FinalTestState(Some(RootState))
      }""" shouldNot compile

      """object TestingStates {
        sealed abstract class TestState(val parentState: Option[ContainerTestState]) extends State {
          override type HierarchyRoot = TestState
        }
        sealed abstract class ContainerTestState(parentState: Option[ContainerTestState]) extends TestState(parentState) with ContainerState
        sealed abstract class FinalTestState(parentState: Option[ContainerTestState]) extends TestState(parentState)

        case object RootState extends ContainerTestState(None)
        case class ClassState(arg: String, arg2: Int) extends FinalTestState(Some(RootState))
        case object ObjectState extends ContainerTestState(Some(RootState))
        case object NextObjectState extends FinalTestState(Some(ObjectState))
        case object ErrorState extends FinalTestState(Some(ClassState)) // wrong parent (final)
      }""" shouldNot compile
    }
  }
}
