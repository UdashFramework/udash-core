package io.udash.auth

import scalatags.JsDom.all._

trait AuthView {
  /** Renders provided `view` only if user context has required permissions. */
  def require(permission: PermissionCombinator, requireAuthenticated: Boolean = false)(view: => Modifier)(implicit userCtx: UserCtx): Modifier =
    if ((!requireAuthenticated || userCtx.isAuthenticated) && permission.check(userCtx)) view else ()

  /** Renders provided `view` only if user is authenticated. */
  def requireAuthenticated(view: => Modifier)(implicit userCtx: UserCtx): Modifier =
    require(PermissionCombinator.AllowAll, requireAuthenticated = true)(view)
}

object AuthView extends AuthView
