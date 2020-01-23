package io.udash.routing

import io.udash._
import io.udash.testing._
import org.scalactic.source.Position

class RoutingEngineTest extends UdashFrontendTest with TestRouting {

  "RoutingEngine" should {
    "render valid views on url change" in {
      val rootViewFactory = new TestViewFactory[RootState]
      val objectViewFactory = new TestViewFactory[ObjectState.type]
      val nextObjectViewFactory = new TestViewFactory[NextObjectState.type]
      val classViewFactory = new TestViewFactory[ClassState]
      val class2ViewFactory = new TestViewFactory[ClassState]
      val errorViewFactory = new TestViewFactory[ErrorState.type]
      val state2VP: Map[TestState, TestViewFactory[_ <: TestState]] = Map[TestState, TestViewFactory[_ <: TestState]](
        RootState(None) -> rootViewFactory,
        RootState(Some(1)) -> rootViewFactory,
        RootState(Some(2)) -> rootViewFactory,
        ObjectState -> objectViewFactory,
        NextObjectState -> nextObjectViewFactory,
        ClassState("abc", 1) -> classViewFactory,
        ClassState("abcd", 234) -> class2ViewFactory,
        ErrorState -> errorViewFactory
      )

      def testClosedAndReset(expectedClosed: TestViewFactory[_] => Boolean)(implicit position: Position): Unit = {
        import scala.collection.compat._
        state2VP.view.mapValues(expectedClosed).toVector.distinct.foreach { case (state, status) =>
          state -> status shouldBe state -> state2VP(state).view.closed
          state -> status shouldBe state -> state2VP(state).presenter.closed
        }
        state2VP.valuesIterator.foreach { vf =>
          vf.view.closed = false
          vf.presenter.closed = false
        }
      }

      initTestRoutingEngine(state2vp = state2VP.mapValues(() => _))

      testClosedAndReset(_ => false)

      routingEngine.handleUrl(Url("/"))

      renderer.views.size should be(2)
      renderer.views(0) should be(rootViewFactory.view)
      renderer.views(1) should be(objectViewFactory.view)
      renderer.lastSubPathToLeave should be(Nil)
      renderer.lastPathToAdd.size should be(2)
      testClosedAndReset(_ => false)

      routingEngine.handleUrl(Url("/next"))

      renderer.views.size should be(3)
      renderer.views(0) should be(rootViewFactory.view)
      renderer.views(1) should be(objectViewFactory.view)
      renderer.views(2) should be(nextObjectViewFactory.view)
      renderer.lastSubPathToLeave.size should be(2)
      renderer.lastPathToAdd should be(nextObjectViewFactory.view :: Nil)
      testClosedAndReset(_ => false)

      routingEngine.handleUrl(Url("/"))

      renderer.views.size should be(2)
      renderer.views(0) should be(rootViewFactory.view)
      renderer.views(1) should be(objectViewFactory.view)
      renderer.lastSubPathToLeave.size should be(2)
      renderer.lastPathToAdd.size should be(0)
      testClosedAndReset {
        case `nextObjectViewFactory` => true
        case _ => false
      }

      routingEngine.handleUrl(Url("/abc/1"))

      renderer.views.size should be(2)
      renderer.views(0) should be(rootViewFactory.view)
      renderer.views(1) should be(classViewFactory.view)
      renderer.lastSubPathToLeave.size should be(1)
      renderer.lastPathToAdd.size should be(1)
      testClosedAndReset {
        case `objectViewFactory` => true
        case _ => false
      }

      routingEngine.handleUrl(Url("/abcd/234"))

      renderer.views.size should be(2)
      renderer.views(0) should be(rootViewFactory.view)
      renderer.views(1) should be(class2ViewFactory.view)
      renderer.lastSubPathToLeave.size should be(1)
      renderer.lastPathToAdd.size should be(1)
      testClosedAndReset {
        case `classViewFactory` => true
        case _ => false
      }

      routingEngine.handleUrl(Url("/next"))

      renderer.views.size should be(3)
      renderer.views(0) should be(rootViewFactory.view)
      renderer.views(1) should be(objectViewFactory.view)
      renderer.views(2) should be(nextObjectViewFactory.view)
      renderer.lastSubPathToLeave.size should be(1)
      renderer.lastPathToAdd should be(objectViewFactory.view :: nextObjectViewFactory.view :: Nil)
      rootViewFactory.count shouldBe 1
      testClosedAndReset {
        case `class2ViewFactory` => true
        case _ => false
      }

      routingEngine.handleUrl(Url("/next"), fullReload = true)

      renderer.views.size should be(3)
      renderer.views(0) should be(rootViewFactory.view)
      renderer.views(1) should be(objectViewFactory.view)
      renderer.views(2) should be(nextObjectViewFactory.view)
      renderer.lastSubPathToLeave.size should be(0)
      renderer.lastPathToAdd should be(rootViewFactory.view :: objectViewFactory.view :: nextObjectViewFactory.view :: Nil)
      rootViewFactory.count shouldBe 2
      testClosedAndReset {
        case `rootViewFactory` | `objectViewFactory` | `nextObjectViewFactory` => true
        case _ => false
      }

      routingEngine.handleUrl(Url("/root/1"))

      rootViewFactory.count shouldBe 2
      testClosedAndReset {
        case `objectViewFactory` | `nextObjectViewFactory` => true
        case _ => false
      }

      routingEngine.handleUrl(Url("/root/2"))

      rootViewFactory.count shouldBe 2
      testClosedAndReset(_ => false)
    }

    "fire state change callbacks" in {
      initTestRoutingEngine()

      var calls = 0
      var lastCallbackEvent: StateChangeEvent[TestState] = null
      val reg = routingEngine.onStateChange(ev => {
        lastCallbackEvent = ev
        calls += 1
      })

      routingEngine.handleUrl(Url("/"))

      calls should be(1)
      lastCallbackEvent.oldState should be(null)
      lastCallbackEvent.currentState should be(ObjectState)

      routingEngine.handleUrl(Url("/next"))

      calls should be(2)
      lastCallbackEvent.oldState should be(ObjectState)
      lastCallbackEvent.currentState should be(NextObjectState)

      routingEngine.handleUrl(Url("/"))

      calls should be(3)
      lastCallbackEvent.oldState should be(NextObjectState)
      lastCallbackEvent.currentState should be(ObjectState)

      routingEngine.handleUrl(Url("/"))

      calls should be(3)
      lastCallbackEvent.oldState should be(NextObjectState)
      lastCallbackEvent.currentState should be(ObjectState)

      routingEngine.handleUrl(Url("/abc/1"))

      calls should be(4)
      lastCallbackEvent.oldState should be(ObjectState)
      lastCallbackEvent.currentState should be(ClassState("abc", 1))

      routingEngine.handleUrl(Url("/abc/1"))

      calls should be(4)
      lastCallbackEvent.oldState should be(ObjectState)
      lastCallbackEvent.currentState should be(ClassState("abc", 1))

      routingEngine.handleUrl(Url("/abcd/234"))

      calls should be(5)
      lastCallbackEvent.oldState should be(ClassState("abc", 1))
      lastCallbackEvent.currentState should be(ClassState("abcd", 234))

      routingEngine.handleUrl(Url("/next"))

      calls should be(6)
      lastCallbackEvent.oldState should be(ClassState("abcd", 234))
      lastCallbackEvent.currentState should be(NextObjectState)

      routingEngine.handleUrl(Url("/next"))

      calls should be(6)
      lastCallbackEvent.oldState should be(ClassState("abcd", 234))
      lastCallbackEvent.currentState should be(NextObjectState)

      reg.cancel()
      routingEngine.handleUrl(Url("/abcd/123"))

      calls should be(6)
      lastCallbackEvent.oldState should be(ClassState("abcd", 234))
      lastCallbackEvent.currentState should be(NextObjectState)

      reg.restart()
      routingEngine.handleUrl(Url("/next"))

      calls should be(7)
      lastCallbackEvent.oldState should be(ClassState("abcd", 123))
      lastCallbackEvent.currentState should be(NextObjectState)

      routingEngine.handleUrl(Url("/next"))

      calls should be(7)
      lastCallbackEvent.oldState should be(ClassState("abcd", 123))
      lastCallbackEvent.currentState should be(NextObjectState)

      routingEngine.handleUrl(Url("/next"), fullReload = true)

      calls should be(8)
      lastCallbackEvent.oldState should be(NextObjectState)
      lastCallbackEvent.currentState should be(NextObjectState)
    }

    "return valid current app state" in {
      initTestRoutingEngine()

      routingEngine.handleUrl(Url("/"))
      routingEngine.currentState should be(ObjectState)

      routingEngine.handleUrl(Url("/next"))
      routingEngine.currentState should be(NextObjectState)

      routingEngine.handleUrl(Url("/"))
      routingEngine.currentState should be(ObjectState)

      routingEngine.handleUrl(Url("/abc/1"))
      routingEngine.currentState should be(ClassState("abc", 1))

      routingEngine.handleUrl(Url("/abcd/234"))
      routingEngine.currentState should be(ClassState("abcd", 234))

      routingEngine.handleUrl(Url("/next"))
      routingEngine.currentState should be(NextObjectState)

      routingEngine.handleUrl(Url("/next"), fullReload = true)
      routingEngine.currentState should be(NextObjectState)

      routingEngine.handleUrl(Url("/abcd/234"), fullReload = true)
      routingEngine.currentState should be(ClassState("abcd", 234))
    }

    "not render views with presenters throwing exceptions on state handling" in {
      class ExceptionPresenter[S <: State] extends Presenter[S] {
        override def handleState(state: S): Unit = throw new RuntimeException("handleState")
      }

      class ExceptionViewFactory[S <: State](view: View) extends ViewFactory[S] {
        override def create(): (View, Presenter[S]) = {
          (view, new ExceptionPresenter[S])
        }
      }

      class OnCloseExceptionPresenter[S <: State] extends Presenter[S] {
        override def handleState(state: S): Unit = ()
        override def onClose(): Unit = throw new RuntimeException("onClose")
      }

      class OnCloseExceptionViewFactory[S <: State](view: View) extends ViewFactory[S] {
        override def create(): (View, Presenter[S]) = (view, new OnCloseExceptionPresenter[S])
      }

      val rootView = new TestView
      val objectView = new TestView
      val nextObjectView = new TestView
      val classView = new TestView
      val class2View = new TestView
      val errorView = new TestView
      val state2VP: Map[TestState, ViewFactory[_ <: TestState]] = Map(
        RootState(None) -> new StaticViewFactory[RootState](() => rootView) {},
        ObjectState -> new ExceptionViewFactory[ObjectState.type](objectView),
        NextObjectState -> new ExceptionViewFactory[NextObjectState.type](nextObjectView),
        ClassState("abc", 1) -> new ExceptionViewFactory[ClassState](classView),
        ClassState("abcd", 234) -> new OnCloseExceptionViewFactory[ClassState](class2View),
        ErrorState -> new ExceptionViewFactory[ErrorState.type](errorView)
      )

      initTestRoutingEngine(state2vp = state2VP.mapValues(() => _))

      routingEngine.handleUrl(Url("/"))
      renderer.views.size should be(0)

      routingEngine.handleUrl(Url("/next"))
      renderer.views.size should be(0)

      routingEngine.handleUrl(Url("/"))
      renderer.views.size should be(0)

      routingEngine.handleUrl(Url("/abc/1"))
      renderer.views.size should be(0)

      //handleState exception doesn't prevent routing to valid state
      routingEngine.handleUrl(Url("/root"))
      renderer.views shouldBe Seq(rootView)

      routingEngine.handleUrl(Url("/abcd/234"))
      renderer.views shouldBe Seq(rootView, class2View)

      //onClose exception doesn't prevent routing to valid state
      routingEngine.handleUrl(Url("/root"))
      renderer.views shouldBe Seq(rootView)

      routingEngine.handleUrl(Url("/abcd/234"))
      renderer.views shouldBe Seq(rootView, class2View)

      //onClose exception doesn't prevent routing to valid state
      routingEngine.handleUrl(Url("/root"), fullReload = true)
      renderer.views shouldBe Seq(rootView)

      routingEngine.handleUrl(Url("/next"))
      renderer.views shouldBe Seq(rootView)
    }

    "not rerender static views" in {
      val staticView = new TestView
      var staticCreateCount = 0
      def staticViewFactory() = new StaticViewFactory[RootState](() => staticView) {
        override def create(): (View, EmptyPresenter.type) = {
          staticCreateCount += 1
          super.create()
        }
      }
      val state2VP: Map[TestState, () => ViewFactory[_ <: TestState]] = Map[TestState, () => ViewFactory[_ <: TestState]](
        RootState(None) -> staticViewFactory,
        RootState(Some(1)) -> staticViewFactory,
        RootState(Some(2)) -> staticViewFactory,
      )

      initTestRoutingEngine(state2vp = state2VP)

      routingEngine.handleUrl(Url("/root"))

      renderer.views shouldBe Seq(staticView)
      renderer.lastSubPathToLeave shouldBe empty
      renderer.lastPathToAdd.size shouldBe 1
      staticCreateCount shouldBe 1

      routingEngine.handleUrl(Url("/root/1"))

      renderer.views shouldBe Seq(staticView)
      renderer.lastSubPathToLeave shouldBe List(staticView)
      renderer.lastPathToAdd.size shouldBe 0
      staticCreateCount shouldBe 1

      routingEngine.handleUrl(Url("/root/2"))

      renderer.views shouldBe Seq(staticView)
      renderer.lastSubPathToLeave shouldBe List(staticView)
      renderer.lastPathToAdd.size shouldBe 0
      staticCreateCount shouldBe 1
    }
  }
}
