package io.udash.auth

import io.udash.testing.UdashSharedTest

class PermissionCombinatorTest extends UdashSharedTest with AuthTestUtils {
  "PermissionCombinator" should {
    "be implicitly created from single Permission" in {
      val user = User(Set(P1, P2))
      P1.check(user) should be(true)
      P2.check(user) should be(true)
      P3.check(user) should be(false)
    }

    "combine with other combinators" in {
      val user = User(Set(P1, P2))
      P1.or(P2).check(user) should be(true)
      P2.or(P3).check(user) should be(true)
      P3.or(P1).check(user) should be(true)
      P1.and(P2).check(user) should be(true)
      P2.and(P3).check(user) should be(false)
      P3.and(P1).check(user) should be(false)
      P3.and(P1).or(P2).check(user) should be(true)
      P3.and(P1.or(P2)).check(user) should be(false)

      PermissionCombinator.AllowAll.check(user) should be(true)
      PermissionCombinator.allOf(P1, P2).check(user) should be(true)
      PermissionCombinator.allOf(P1, P2, P3).check(user) should be(false)
      PermissionCombinator.anyOf(P1, P2).check(user) should be(true)
      PermissionCombinator.anyOf(P1, P2, P3).check(user) should be(true)
      PermissionCombinator.anyOf(P3).check(user) should be(false)
    }

    "ignore authentication status of the user" in {
      val user = UnauthenticatedUserWithPerms(Set(P1, P2))
      P1.or(P2).check(user) should be(true)
      P2.or(P3).check(user) should be(true)
      P3.or(P1).check(user) should be(true)
      P1.and(P2).check(user) should be(true)
      P2.and(P3).check(user) should be(false)
      P3.and(P1).check(user) should be(false)
      P3.and(P1).or(P2).check(user) should be(true)
      P3.and(P1.or(P2)).check(user) should be(false)

      PermissionCombinator.AllowAll.check(user) should be(true)
      PermissionCombinator.allOf(P1, P2).check(user) should be(true)
      PermissionCombinator.allOf(P1, P2, P3).check(user) should be(false)
      PermissionCombinator.anyOf(P1, P2).check(user) should be(true)
      PermissionCombinator.anyOf(P1, P2, P3).check(user) should be(true)
      PermissionCombinator.anyOf(P3).check(user) should be(false)
    }
  }
}
