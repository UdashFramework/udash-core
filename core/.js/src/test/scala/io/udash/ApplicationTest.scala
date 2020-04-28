package io.udash

import io.udash.testing._

class ApplicationTest extends UdashFrontendTest with TestRouting {

  "Application" should {
    initTestRouting(default = () => new TestViewFactory[TestState])
    val initUrl = Url("/")
    val urlProvider: TestUrlChangeProvider = new TestUrlChangeProvider(initUrl)
    val app = new Application[TestState](routing, vpRegistry, urlProvider)

    app.run(emptyComponent())

    "register for url changes on start and handle initial state" in {
      urlProvider.changeListeners.size should be(1)
      routing.urlsHistory should contain(initUrl)
      vpRegistry.statesHistory should contain(RootState(None))
    }

    "change URL basing on state" in {
      app.goTo(NextObjectState)
      urlProvider.currUrl.value should be("/next")
      app.goTo(ObjectState)
      urlProvider.currUrl.value should be("/")
      app.goTo(ClassState("abc", 1))
      urlProvider.currUrl.value should be("/abc/1")
      app.goTo(ClassState("abcd", 234))
      urlProvider.currUrl.value should be("/abcd/234")
    }

    "redirect to URL" in {
      app.redirectTo("http://www.avsystem.com/")
      urlProvider.currUrl.value should be("http://www.avsystem.com/")
    }

    "register callback for state change" in {
      var counter = 0
      app.onStateChange(_ => counter += 1)

      app.goTo(ObjectState)
      app.goTo(NextObjectState)
      app.goTo(ObjectState)
      app.goTo(NextObjectState)
      app.goTo(ObjectState)
      app.goTo(NextObjectState)
      app.reload()

      counter should be(7)
    }

    "register callback for routing failure" in {
      var failCounter = 0
      def callback: PartialFunction[Throwable, Any] = { case _ =>
        failCounter += 1
        throw new NullPointerException
      }

      var counter = 0
      app.onStateChange(_ => counter += 1)

      app.onRoutingFailure(callback)
      app.onRoutingFailure(callback)
      app.onRoutingFailure(callback)
      app.onRoutingFailure(callback)

      app.goTo(ObjectState)
      app.goTo(NextObjectState)
      app.goTo(ThrowExceptionState)
      app.goTo(NextObjectState)
      app.goTo(ThrowExceptionState)
      app.goTo(NextObjectState)

      counter should be(4)
      failCounter should be(8)
    }

    "return URL of state" in {
      app.matchState(ObjectState).value should be("/")
      app.matchState(NextObjectState).value should be("/next")
      app.matchState(ClassState("abc", 1)).value should be("/abc/1")
      app.matchState(ClassState("abcd", 234)).value should be("/abcd/234")
    }
  }
}
