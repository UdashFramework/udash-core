package io.udash.auth

import io.udash.testing.UdashFrontendTest

class AuthPresenterTest extends UdashFrontendTest with AuthTestUtils with AuthFrontendTestUtils {
  import PermissionCombinator.AllowAll

  "AuthPresenter" should {
    "throw an exception if user is not authenticated" in {
      class SomePresenter extends AuthPresenter[SomeState.type](AllowAll, requireAuthenticated = false)(UnauthenticatedUser)

      val p = new SomePresenter
      p.handleState(SomeState)

      class SomePresenter2 extends AuthPresenter[SomeState.type](AllowAll, requireAuthenticated = true)(UnauthenticatedUser)

      val p2 = new SomePresenter2
      intercept[UnauthenticatedException] {
        p2.handleState(SomeState)
      }
    }

    "throw an exception if user is not authorized" in {
      class SomePresenter extends AuthPresenter[SomeState.type](P1.and(P2.or(P3)))(User(Set(P1, P2)))

      val p = new SomePresenter
      p.handleState(SomeState)

      class SomePresenter2 extends AuthPresenter[SomeState.type](P1.and(P2.or(P3)))(User(Set(P1)))

      val p2 = new SomePresenter2
      intercept[UnauthorizedException] {
        p2.handleState(SomeState)
      }
    }
  }
}
