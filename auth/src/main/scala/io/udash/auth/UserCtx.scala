package io.udash.auth

trait UserCtx {
  def has(permission: Permission): Boolean
  def isAuthenticated: Boolean
}

object UserCtx {
  trait Unauthenticated extends UserCtx {
    override def has(permission: Permission): Boolean = false
    override def isAuthenticated: Boolean = false
  }
}