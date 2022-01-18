package io.udash.auth

import io.udash.testing.AsyncUdashSharedTest
import org.scalatest.Succeeded

import scala.concurrent.Future

class AsyncAuthRequiresTest extends AsyncUdashSharedTest with AuthTestUtils {
  import PermissionCombinator.AllowAll

  "AsyncAuthRequiresTest utils" should {
    "check user's permissions" in {
      implicit val user: Future[UserCtx] = Future.successful(User(Set(P1, P2)))

      for {
        _ <- AsyncAuthRequires.require(P1.and(P2))
        _ <- recoverToSucceededIf[UnauthorizedException] { AsyncAuthRequires.require(P1.and(P3)) }
        _ <- recoverToSucceededIf[UnauthorizedException] { AsyncAuthRequires.require(P2.and(P3)) }
        _ <- AsyncAuthRequires.require(P1.or(P3))
        _ <- AsyncAuthRequires.require(AllowAll)
      } yield Succeeded
    }

    "check is user is authenticated" in {
      implicit val user: Future[UserCtx] = Future.successful(UnauthenticatedUser)
      
      for {
        _ <- AsyncAuthRequires.require(AllowAll)
        _ <- recoverToSucceededIf[UnauthenticatedException] {AsyncAuthRequires.require (AllowAll, requireAuthenticated = true)}
        _ <- recoverToSucceededIf[UnauthenticatedException] {AsyncAuthRequires.requireAuthenticated()}
        _ <- recoverToSucceededIf[UnauthenticatedException] {AsyncAuthRequires.require (P2.and (P3), requireAuthenticated = true)}
        _ <- recoverToSucceededIf[UnauthorizedException] {AsyncAuthRequires.require (P2.and (P3))}
      } yield Succeeded
    }
  }
}
