package io.udash.auth

import io.udash._

/**
  * Presenter which check user access in `handleState` method.
  *
  * @param permission PermissionCombinator verified against provided `userCtx`.
  * @param requireAuthenticated If `true`, the presenter requires `userCtx` to don't be Unauthenticated subclass.
  */
abstract class AuthPresenter[S <: State](permission: PermissionCombinator, requireAuthenticated: Boolean = false)
                                        (implicit userCtx: UserCtx)
  extends Presenter[S] with AuthRequires {

  override def handleState(state: S): Unit = {
    require(permission, requireAuthenticated)
  }

}
