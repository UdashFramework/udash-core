package io.udash.auth

import io.udash.testing.UdashSharedTest

class AuthRequiresTest extends UdashSharedTest with AuthTestUtils {
  import PermissionCombinator.AllowAll

  "AuthRequires utils" should {
    "check user's permissions" in {
      implicit val user: UserCtx = User(Set(P1, P2))

      AuthRequires.require(P1.and(P2))
      intercept[UnauthorizedException] { AuthRequires.require(P1.and(P3)) }
      intercept[UnauthorizedException] { AuthRequires.require(P2.and(P3)) }
      AuthRequires.require(P1.or(P3))
      AuthRequires.require(AllowAll)
    }

    "check is user is authenticated" in {
      implicit val user: UserCtx = UnauthenticatedUser

      AuthRequires.require(AllowAll)
      intercept[UnauthenticatedException] { AuthRequires.require(AllowAll, requireAuthenticated = true) }
      intercept[UnauthenticatedException] { AuthRequires.requireAuthenticated() }
      intercept[UnauthenticatedException] { AuthRequires.require(P2.and(P3), requireAuthenticated = true) }
      intercept[UnauthorizedException] { AuthRequires.require(P2.and(P3)) }
    }
  }
}
