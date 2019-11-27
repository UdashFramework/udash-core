package io.udash.routing

import io.udash._
import io.udash.testing._

class RoutingEngineTest extends UdashFrontendTest with TestRouting {

  "RoutingEngine" should {
    "render valid views on url change" in {
      val rootView = new TestView

      object RootViewFactory extends ViewFactory[RootState] {
        var calls = 0
        var closed = false
        override def create() = {
          calls += 1
          (rootView, new Presenter[RootState] {
            override def handleState(state: RootState): Unit = ()
            override def onClose(): Unit = closed = true
          })
        }
      }

      val objectView = new TestView
      val nextObjectView = new TestView
      val classView = new TestView
      val class2View = new TestView
      val errorView = new TestView
      val state2VP: Map[TestState, ViewFactory[_ <: TestState]] = Map[TestState, ViewFactory[_ <: TestState]](
        RootState(None) -> RootViewFactory,
        RootState(Some(1)) -> RootViewFactory,
        RootState(Some(2)) -> RootViewFactory,
        ObjectState -> new StaticViewFactory[ObjectState.type](() => objectView) {},
        NextObjectState -> new StaticViewFactory[NextObjectState.type](() => nextObjectView) {},
        ClassState("abc", 1) -> new StaticViewFactory[ClassState](() => classView) {},
        ClassState("abcd", 234) -> new StaticViewFactory[ClassState](() => class2View) {},
        ErrorState -> new StaticViewFactory[ErrorState.type](() => errorView) {}
      )

      initTestRoutingEngine(state2vp = state2VP)

      RootViewFactory.closed shouldBe false

      routingEngine.handleUrl(Url("/"))

      renderer.views.size should be(2)
      renderer.views(0) should be(rootView)
      renderer.views(1) should be(objectView)
      renderer.lastSubPathToLeave should be(Nil)
      renderer.lastPathToAdd.size should be(2)
      RootViewFactory.closed shouldBe false

      routingEngine.handleUrl(Url("/next"))

      renderer.views.size should be(3)
      renderer.views(0) should be(rootView)
      renderer.views(1) should be(objectView)
      renderer.views(2) should be(nextObjectView)
      renderer.lastSubPathToLeave.size should be(2)
      renderer.lastPathToAdd should be(nextObjectView :: Nil)
      RootViewFactory.closed shouldBe false

      routingEngine.handleUrl(Url("/"))

      renderer.views.size should be(2)
      renderer.views(0) should be(rootView)
      renderer.views(1) should be(objectView)
      renderer.lastSubPathToLeave.size should be(2)
      renderer.lastPathToAdd.size should be(0)
      RootViewFactory.closed shouldBe false

      routingEngine.handleUrl(Url("/abc/1"))

      renderer.views.size should be(2)
      renderer.views(0) should be(rootView)
      renderer.views(1) should be(classView)
      renderer.lastSubPathToLeave.size should be(1)
      renderer.lastPathToAdd.size should be(1)
      RootViewFactory.closed shouldBe false

      routingEngine.handleUrl(Url("/abcd/234"))

      renderer.views.size should be(2)
      renderer.views(0) should be(rootView)
      renderer.views(1) should be(class2View)
      renderer.lastSubPathToLeave.size should be(1)
      renderer.lastPathToAdd.size should be(1)
      RootViewFactory.closed shouldBe false

      routingEngine.handleUrl(Url("/next"))

      renderer.views.size should be(3)
      renderer.views(0) should be(rootView)
      renderer.views(1) should be(objectView)
      renderer.views(2) should be(nextObjectView)
      renderer.lastSubPathToLeave.size should be(1)
      renderer.lastPathToAdd should be(objectView :: nextObjectView :: Nil)
      RootViewFactory.closed shouldBe false
      RootViewFactory.calls should be(1)

      routingEngine.handleUrl(Url("/next"), fullReload = true)

      renderer.views.size should be(3)
      renderer.views(0) should be(rootView)
      renderer.views(1) should be(objectView)
      renderer.views(2) should be(nextObjectView)
      renderer.lastSubPathToLeave.size should be(0)
      renderer.lastPathToAdd should be(rootView :: objectView :: nextObjectView :: Nil)
      RootViewFactory.closed shouldBe true //full reload
      RootViewFactory.calls should be(2)
      RootViewFactory.closed = false

      routingEngine.handleUrl(Url("/root/1"))

      RootViewFactory.closed shouldBe false
      RootViewFactory.calls should be(2)

      routingEngine.handleUrl(Url("/root/2"))

      RootViewFactory.closed shouldBe false
      RootViewFactory.calls should be(2)
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

    "not render views if presenter throws exception on state handling" in {
      class ExceptionPresenter[S <: State] extends Presenter[S] {
        override def handleState(state: S): Unit = {
          throw new RuntimeException
        }
      }

      class ExceptionViewFactory[S <: State](view: View) extends ViewFactory[S] {
        override def create(): (View, Presenter[S]) = {
          (view, new ExceptionPresenter[S])
        }
      }

      val rootView = new TestView
      val objectView = new TestView
      val nextObjectView = new TestView
      val classView = new TestView
      val class2View = new TestView
      val errorView = new TestView
      val state2VP: Map[TestState, ViewFactory[_ <: TestState]] = Map(
        RootState(None) -> new StaticViewFactory[RootState](() => rootView),
        ObjectState -> new ExceptionViewFactory[ObjectState.type](objectView),
        NextObjectState -> new ExceptionViewFactory[NextObjectState.type](nextObjectView),
        ClassState("abc", 1) -> new ExceptionViewFactory[ClassState](classView),
        ClassState("abcd", 234) -> new ExceptionViewFactory[ClassState](class2View),
        ErrorState -> new ExceptionViewFactory[ErrorState.type](errorView)
      )

      initTestRoutingEngine(state2vp = state2VP)

      routingEngine.handleUrl(Url("/"))
      renderer.views.size should be(0)

      routingEngine.handleUrl(Url("/next"))
      renderer.views.size should be(0)

      routingEngine.handleUrl(Url("/"))
      renderer.views.size should be(0)

      routingEngine.handleUrl(Url("/abc/1"))
      renderer.views.size should be(0)

      routingEngine.handleUrl(Url("/abcd/234"))
      renderer.views.size should be(0)

      routingEngine.handleUrl(Url("/next"))
      renderer.views.size should be(0)
    }
  }
}
