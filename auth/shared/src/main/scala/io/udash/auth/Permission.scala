package io.udash.auth

import scala.language.implicitConversions

/**
  * Base class for permissions used with PermissionControl. Permissions are compared by ID.
  */
trait Permission {
  def id: PermissionId

  override def equals(other: Any): Boolean = other match {
    case that: Permission => id == that.id
    case _ => false
  }

  override def hashCode(): Int =
    id.hashCode()

  override def toString =
    s"Permission(${id.value}"
}

object Permission {
  /** Single permission as a combinator resolved implicitly. */
  implicit class Single(val permission: Permission) extends AnyVal with PermissionCombinator {
    override def check(ctx: UserCtx): Boolean =
      ctx.has(permission)

    override def toString: String =
      permission.toString
  }
}