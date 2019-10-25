package io.udash.auth

import io.udash.testing.UdashFrontendTest

class AuthViewTest extends UdashFrontendTest with AuthTestUtils {
  "AuthView" should {
    "not render data if permission is missing" in {
      import AuthView._
      import scalatags.JsDom.all._

      implicit val user = User(Set(P1, P2))

      val el = div(
        require(P1.and(P2)) { "1" },
        require(P1.and(P3)) { "2" },
        require(P1.or(P3)) { "3" },
        require(P3.or(P1)) { "4" },
        require(P1) { "5" },
        require(P3) { "6" },
        require(P3.and(P1.or(P2))) { "7" },
        require(P1.and(P3.or(P2))) { "8" },
        require(P1.and(P2.or(P3))) { "9" }
      ).render

      el.textContent should be("134589")
    }

    "not render data if user is not authenticated" in {
      import AuthView._
      import PermissionCombinator.AllowAll
      import scalatags.JsDom.all._

      implicit val user = UnauthenticatedUser

      val el = div(
        require(AllowAll, requireAuthenticated = true) { "1" },
        require(AllowAll) { "2" },
        require(AllowAll, requireAuthenticated = true) { "3" },
        require(AllowAll) { "4" },
        require(AllowAll, requireAuthenticated = true) { "5" },
        require(P1) { "6" },
        require(AllowAll, requireAuthenticated = true) { "7" },
        require(AllowAll) { "8" },
        require(AllowAll, requireAuthenticated = true) { "9" }
      ).render

      el.textContent should be("248")
    }

    "render fallback view if user lacks permission " in {
      import AuthView._
      import PermissionCombinator.AllowAll
      import scalatags.JsDom.all._


      implicit val user = User(Set(P1, P2))

      val el = div(
        requireWithFallback(AllowAll, requireAuthenticated = true)("1", "-1"),
        requireWithFallback(P1.and(P2), requireAuthenticated = true)("2", "-2"),
        requireWithFallback(P1.and(P3), requireAuthenticated = true)("3", "-3"),
        requireWithFallback(P2.and(P3), requireAuthenticated = true)("4", "-4"),
      ).render
      el.textContent should be("12-3-4")
    }
  }
}
