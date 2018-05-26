package io.udash.auth

import io.udash.{Application, RoutingRegistry}
import io.udash.core._
import io.udash.testing.AsyncUdashFrontendTest


class AuthApplicationTest extends AsyncUdashFrontendTest with AuthTestUtils with AuthFrontendTestUtils {

  import AuthApplication.ApplicationAuthExt
  import PermissionCombinator.AllowAll

  class TestVF(p: Presenter[TestStates]) extends ViewFactory[TestStates] {
    override def create(): (View, Presenter[TestStates]) =
      (new FinalView {

        import scalatags.JsDom.all._

        override def getTemplate: Modifier = div().render
      }, p)
  }

  val rr: RoutingRegistry[TestStates] = new RoutingRegistry[TestStates] {
    val (url2State, state2Url) = bidirectional {
      case "" => SomeState
      case "/s2" => SecondState
      case "/s3" => ThirdState
    }

    override def matchUrl(url: Url): TestStates = url2State(url.value)
    override def matchState(state: TestStates): Url = Url(state2Url(state))
  }

  "AuthApplication" should {
    "should redirect after UnauthorizedException or UnauthenticatedException throw" in {
      implicit val user: UserCtx = UnauthenticatedUser
      val vfr = new ViewFactoryRegistry[TestStates] {
        override def matchStateToResolver(state: TestStates): ViewFactory[_ <: TestStates] =
          state match {
            case SomeState => new TestVF(new AuthPresenter[TestStates](P1) {})
            case SecondState => new TestVF(new AuthPresenter[TestStates](AllowAll, requireAuthenticated = true) {})
            case ThirdState => new TestVF(new Presenter[TestStates] {
              override def handleState(state: TestStates): Unit = {}
            })
          }
      }

      val root = scalatags.JsDom.all.div().render
      val app = new Application[TestStates](rr, vfr).withDefaultRoutingFailureListener(ThirdState)
      app.run(root)
      for {
        _ <- retrying { app.currentState should be(ThirdState) }
        _ = app.goTo(SecondState)
        _ <- retrying { app.currentState should be(ThirdState) }
        _ = app.goTo(SomeState)
        _ <- retrying { app.currentState should be(ThirdState) }
        _ = app.goTo(ThirdState)
        r <- retrying { app.currentState should be(ThirdState) }
      } yield r
    }

    "not loop if unauthorized redirect throws exception" in {
      implicit val user: User = User(Set.empty)
      val vfr = new ViewFactoryRegistry[TestStates] {
        override def matchStateToResolver(state: TestStates): ViewFactory[_ <: TestStates] =
          state match {
            case SomeState => new TestVF(new AuthPresenter[TestStates](P1) {})
            case SecondState => new TestVF(new AuthPresenter[TestStates](P2) {})
            case ThirdState => new TestVF(new AuthPresenter[TestStates](P3) {})
          }
      }

      val root = scalatags.JsDom.all.div().render
      val app = new Application[TestStates](rr, vfr).withDefaultRoutingFailureListener(ThirdState)
      app.run(root)
      for {
        _ <- retrying { app.currentState should be(ThirdState) }
        _ = app.goTo(SecondState)
        _ <- retrying { app.currentState should be(ThirdState) }
        _ = app.goTo(SomeState)
        _ <- retrying { app.currentState should be(ThirdState) }
        _ = app.goTo(ThirdState)
        r <- retrying { app.currentState should be(ThirdState) }
      } yield r
    }
  }
}
