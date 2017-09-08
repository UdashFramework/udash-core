package io.udash.auth

import io.udash.properties.ImmutableValue

trait PermissionCombinator extends Any {
  /** Should return `true` if provided user context passes this combinator test.
    * It should not check `isAuthenticated` flag of user context. */
  def check(ctx: UserCtx): Boolean

  /**
    * Combines this person combinator with another,
    * creating a new one granting permission only in case both combinators do so.
    */
  def and(other: PermissionCombinator): PermissionCombinator = new PermissionCombinator {
    override def check(ctx: UserCtx): Boolean =
      other.check(ctx) && PermissionCombinator.this.check(ctx)

    override def toString: String =
      s"($this && $other)"
  }

  /**
    * Combines this person combinator with another,
    * creating a new one granting permission only in case any of combinators does so.
    */
  def or(other: PermissionCombinator): PermissionCombinator = new PermissionCombinator {
    override def check(ctx: UserCtx): Boolean =
      other.check(ctx) || PermissionCombinator.this.check(ctx)

    override def toString: String =
      s"($this || $other)"
  }
}

object PermissionCombinator {
  /** PermissionCombinator always granting permission. */
  val AllowAll: PermissionCombinator = new PermissionCombinator {
    override def check(ctx: UserCtx): Boolean = true
  }

  /** PermissionCombinator granting permission only when all of given combinators do so. */
  def allOf(permissions: Permission*): PermissionCombinator = new PermissionCombinator {
    override def check(ctx: UserCtx): Boolean =
      permissions.forall(ctx.has)

    override def toString: String =
      permissions.map(_.toString).mkString("(", " && ", ")")
  }

  /** PermissionCombinator granting permission when any of given combinators does so. */
  def anyOf(permissions: Permission*): PermissionCombinator = new PermissionCombinator {
    override def check(ctx: UserCtx): Boolean =
      permissions.exists(ctx.has)

    override def toString: String =
      permissions.map(_.toString).mkString("(", " || ", ")")
  }

  implicit val immutableValueEvidence: ImmutableValue[PermissionCombinator] = null
}

