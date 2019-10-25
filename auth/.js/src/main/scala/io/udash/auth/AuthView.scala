package io.udash.auth

import io.udash.auth.PermissionCombinator.AllowAll
import scalatags.JsDom.all._

trait AuthView {
  /** Renders provided `view` only if user context has required permissions. */
  def require(permission: PermissionCombinator, requireAuthenticated: Boolean = false)(view: => Modifier)(implicit userCtx: UserCtx): Modifier =
    if (canRenderView(permission, requireAuthenticated)) view else ()

  /** Renders provided `view` only if user is authenticated. */
  def requireAuthenticated(view: => Modifier)(implicit userCtx: UserCtx): Modifier =
    require(AllowAll, requireAuthenticated = true)(view)

  /** Renders provided view if user is authenticated or fallback view otherwise */
  def requireWithFallback(permission: PermissionCombinator, requireAuthenticated: Boolean)
    (view: => Modifier, fallback: => Modifier)(implicit userCtx: UserCtx): Modifier =
    if (canRenderView(permission, requireAuthenticated)) view else fallback

  def requireAuthenticatedWithFallback(view: => Modifier, fallback: => Modifier)(implicit userCtx: UserCtx): Modifier =
    requireWithFallback(AllowAll, requireAuthenticated = true)(view, fallback)

  private def canRenderView(permission: PermissionCombinator, requireAuthenticated: Boolean)(implicit userCtx: UserCtx): Boolean =
    (!requireAuthenticated || userCtx.isAuthenticated) && permission.check(userCtx)
}

object AuthView extends AuthView
