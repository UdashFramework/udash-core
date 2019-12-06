package io.udash.auth

import io.udash.auth.PermissionCombinator.AllowAll
import io.udash.bindings.modifiers.EmptyModifier
import scalatags.JsDom.all._

trait AuthView {
  /** Renders provided `view` only if user context has required permissions. */
  def require(permission: PermissionCombinator, requireAuthenticated: Boolean = false)(view: => Modifier)(implicit userCtx: UserCtx): Modifier =
    requireWithFallback(permission, requireAuthenticated)(view)()

  /** Renders provided `view` only if user is authenticated. */
  def requireAuthenticated(view: => Modifier)(implicit userCtx: UserCtx): Modifier =
    require(AllowAll, requireAuthenticated = true)(view)

  /** Renders provided `view` if user context has required permissions or `fallback` view otherwise. */
  def requireWithFallback(permission: PermissionCombinator, requireAuthenticated: Boolean = false)
    (view: => Modifier)(fallback: => Modifier = new EmptyModifier())(implicit userCtx: UserCtx): Modifier =
    if ((!requireAuthenticated || userCtx.isAuthenticated) && permission.check(userCtx)) view else fallback

  /** Renders provided `view` if user is authenticated or `fallback` view otherwise. */
  def requireAuthenticatedWithFallback(view: => Modifier)(fallback: => Modifier)(implicit userCtx: UserCtx): Modifier =
    requireWithFallback(AllowAll, requireAuthenticated = true)(view)(fallback)
}

object AuthView extends AuthView
