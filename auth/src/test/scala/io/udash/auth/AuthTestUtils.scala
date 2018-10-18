package io.udash.auth

trait AuthTestUtils {

  class Perm(override val id: PermissionId) extends Permission

  case object P1 extends Perm(PermissionId("1"))
  case object P2 extends Perm(PermissionId("2"))
  case object P3 extends Perm(PermissionId("3"))

  case class User(perms: Set[Permission]) extends UserCtx {
    override def has(permission: Permission): Boolean =
      perms.contains(permission)

    override def isAuthenticated: Boolean = true
  }

  case class UnauthenticatedUserWithPerms(perms: Set[Permission]) extends UserCtx {
    override def has(permission: Permission): Boolean =
      perms.contains(permission)

    override def isAuthenticated: Boolean = false
  }

  case object UnauthenticatedUser extends UserCtx.Unauthenticated

}
