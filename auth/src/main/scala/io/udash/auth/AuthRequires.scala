package io.udash.auth

trait AuthRequires {
  /** Checks if user context has required permissions. */
  def require(permission: PermissionCombinator, requireAuthenticated: Boolean = false)(implicit userCtx: UserCtx): Unit = {
    if (requireAuthenticated && !userCtx.isAuthenticated) throw new UnauthenticatedException()
    if (!permission.check(userCtx)) throw new UnauthorizedException()
  }

  /** Checks if user is authenticated. */
  def requireAuthenticated()(implicit userCtx: UserCtx): Unit =
    require(PermissionCombinator.AllowAll, requireAuthenticated = true)
}

object AuthRequires extends AuthRequires
