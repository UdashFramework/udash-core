package io.udash.auth

import scala.concurrent.{ExecutionContext, Future}

trait AsyncAuthRequires {
  /** Checks if user context will have required permissions. */
  def require(permission: PermissionCombinator, requireAuthenticated: Boolean = false)(implicit asyncUserCtx: Future[UserCtx], ec: ExecutionContext): Future[Unit] =
    asyncUserCtx map { implicit ctx =>
      AuthRequires.require(permission, requireAuthenticated)
    }

  /** Checks if user will be authenticated. */
  def requireAuthenticated()(implicit futureUserCtx: Future[UserCtx], ec: ExecutionContext): Future[Unit] =
    futureUserCtx map { implicit ctx =>
      AuthRequires.requireAuthenticated()
    }
}

object AsyncAuthRequires extends AsyncAuthRequires