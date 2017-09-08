package io.udash.auth

import io.udash.testing.UdashSharedTest

class PermissionTest extends UdashSharedTest {
  "Permission" should {
    "be compared by id" in {
      class PermAType(override val id: PermissionId) extends Permission
      class PermBType(override val id: PermissionId) extends Permission

      new PermAType(PermissionId("1")) should be(new PermAType(PermissionId("1")))
      new PermAType(PermissionId("1")) shouldNot be(new PermAType(PermissionId("2")))
      new PermAType(PermissionId("1")) should be(new PermBType(PermissionId("1")))
      new PermBType(PermissionId("1")) should be(new PermAType(PermissionId("1")))
      new PermBType(PermissionId("1")) should be(new PermBType(PermissionId("1")))
      new PermAType(PermissionId("1")) shouldNot be(new PermBType(PermissionId("2")))
      new PermBType(PermissionId("1")) shouldNot be(new PermBType(PermissionId("2")))
    }

    "use hashCode of id" in {
      class PermAType(override val id: PermissionId) extends Permission
      class PermBType(override val id: PermissionId) extends Permission

      Set(new PermAType(PermissionId("1")), new PermAType(PermissionId("1")),
        new PermAType(PermissionId("1")), new PermAType(PermissionId("2")),
        new PermAType(PermissionId("1")), new PermBType(PermissionId("1")),
        new PermBType(PermissionId("1")), new PermAType(PermissionId("1")),
        new PermBType(PermissionId("1")), new PermBType(PermissionId("1")),
        new PermAType(PermissionId("1")), new PermBType(PermissionId("2")),
        new PermBType(PermissionId("1")), new PermBType(PermissionId("2"))).size should be(2)
    }
  }
}
