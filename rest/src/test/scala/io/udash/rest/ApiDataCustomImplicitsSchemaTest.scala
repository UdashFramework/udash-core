package io.udash
package rest

import com.avsystem.commons.misc.{NamedEnum, Opt}
import com.avsystem.commons.serialization.GenCodec.ReadFailure
import com.avsystem.commons.serialization.json.{JsonStringInput, JsonStringOutput}
import com.avsystem.commons.serialization.{GenCodec, GenObjectCodec, defaultCase, flatten}
import io.udash.rest.openapi.{InliningResolver, RestSchema, RestStructure}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

// Flat sealed hierarchy whose companions are derived through the custom-implicits bundle.
@flatten("kind") sealed trait AuditEvent
object AuditEvent extends CustomRestApis.ApiDataCompanion[AuditEvent]

final case class LoginEvent(user: String) extends AuditEvent
object LoginEvent extends CustomRestApis.ApiSealedCaseCompanion[LoginEvent, AuditEvent]

sealed trait SysEvent extends AuditEvent
object SysEvent extends CustomRestApis.ApiSealedSubHierarchyCompanion[SysEvent, AuditEvent]

final case class ShutdownEvent(reason: String) extends SysEvent
object ShutdownEvent extends CustomRestApis.ApiSealedCaseCompanion[ShutdownEvent, AuditEvent]

// Flat sealed hierarchy with a @defaultCase: a missing discriminator makes the root codec read the default case.
@flatten("kind") sealed trait Animal
object Animal extends CustomRestApis.ApiDataCompanion[Animal]

@defaultCase final case class Cat(name: String) extends Animal
object Cat extends CustomRestApis.ApiSealedCaseCompanion[Cat, Animal]

final case class Dog(name: String) extends Animal
object Dog extends CustomRestApis.ApiSealedCaseCompanion[Dog, Animal]

// Case class using Tag, whose codec and schema exist only in the custom-implicits bundle.
final case class Tagged(tag: Tag, n: Int)
object Tagged extends CustomRestApis.ApiDataCompanion[Tagged]

// NamedEnum whose named RestSchema is provided by RestNamedEnumCompanion.
sealed trait TaskPriority extends NamedEnum
object TaskPriority extends RestNamedEnumCompanion[TaskPriority] {
  case object Low extends TaskPriority { override val name: String = "Low" }
  case object High extends TaskPriority { override val name: String = "High" }
  override val values: List[TaskPriority] = caseObjects
}

// Generic wrappers whose codec/structure are derived through the custom-implicits bundle.
final case class PolyBox[T](value: T)
object PolyBox extends CustomRestApis.PolyApiDataCompanion[PolyBox]

final case class PolyRec[T](item: T)
object PolyRec extends CustomRestApis.PolyObjectApiDataCompanion[PolyRec]

final case class PolyPair[A, B](first: A, second: B)
object PolyPair extends CustomRestApis.Poly2ApiDataCompanion[PolyPair]

// A type with NO default codec/schema; its instances are provided only by the deps object below.
final case class Dep(n: Int)

// Deps object supplying serialization/schema for Dep only; everything else (including Tag) comes from the
// enclosing custom-implicits bundle.
object HolderDeps {
  implicit val depCodec: GenCodec[Dep] = GenCodec.materialize[Dep]
  implicit val depSchema: RestSchema[Dep] = RestStructure.materialize[Dep].standaloneSchema
}

final case class Holder(dep: Dep, tag: Tag)
object Holder extends CustomRestApis.ApiDataCompanionWithDeps[HolderDeps.type, Holder]

class ApiDataCustomImplicitsSchemaTest extends AnyFunSuite with Matchers {
  private def schemaStr[T](implicit schema: RestSchema[T]): String =
    JsonStringOutput.writePretty(new InliningResolver().resolve(schema))

  test("custom-implicits bundle reaches the derived codec and schema of a data type") {
    // Tag has no default codec/schema - the only instances come from TestRestImplicits.
    val json = JsonStringOutput.write(Tagged(Tag("x"), 1))
    json shouldBe """{"tag":"tag:x","n":1}"""
    JsonStringInput.read[Tagged](json) shouldBe Tagged(Tag("x"), 1)

    schemaStr[Tagged] shouldBe
      """{
        |  "type": "object",
        |  "properties": {
        |    "tag": {
        |      "type": "string"
        |    },
        |    "n": {
        |      "type": "integer",
        |      "format": "int32"
        |    }
        |  },
        |  "required": [
        |    "tag",
        |    "n"
        |  ]
        |}""".stripMargin
  }

  test("ApiSealedCaseCompanion codec includes and validates the discriminator") {
    val json = JsonStringOutput.write(LoginEvent("bob"))(LoginEvent.codec)
    json shouldBe """{"kind":"LoginEvent","user":"bob"}"""
    JsonStringInput.read[LoginEvent](json)(LoginEvent.codec) shouldBe LoginEvent("bob")

    intercept[ReadFailure] {
      JsonStringInput.read[LoginEvent]("""{"kind":"ShutdownEvent","reason":"x"}""")(LoginEvent.codec)
    }
  }

  test("ApiSealedCaseCompanion codec rejects a different default case instead of casting it") {
    // no discriminator: the root codec falls back to the @defaultCase Cat, which is not a Dog
    intercept[ReadFailure] {
      JsonStringInput.read[Dog]("""{"name":"x"}""")(Dog.codec)
    }
    JsonStringInput.read[Cat]("""{"name":"x"}""")(Cat.codec) shouldBe Cat("x")
  }

  test("ApiSealedCaseCompanion schema carries the discriminator field") {
    val schema = schemaStr[LoginEvent]
    schema should include("kind")
    schema should include("user")
  }

  test("ApiSealedSubHierarchyCompanion codec round-trips a sub-hierarchy value") {
    val json = JsonStringOutput.write[SysEvent](ShutdownEvent("boom"))(SysEvent.codec)
    json shouldBe """{"kind":"ShutdownEvent","reason":"boom"}"""
    JsonStringInput.read[SysEvent](json)(SysEvent.codec) shouldBe ShutdownEvent("boom")
  }

  test("RestNamedEnumCompanion produces a named string enum schema") {
    implicitly[RestSchema[TaskPriority]].name shouldBe Opt("TaskPriority")
    val schema = schemaStr[TaskPriority]
    schema should include(""""enum"""")
    schema should include("Low")
    schema should include("High")
  }

  test("PolyApiDataCompanion derives codec and schema for a single-parameter wrapper") {
    val codec = implicitly[GenCodec[PolyBox[Int]]]
    JsonStringOutput.write(PolyBox(42))(codec) shouldBe """{"value":42}"""
    JsonStringInput.read[PolyBox[Int]]("""{"value":42}""")(codec) shouldBe PolyBox(42)

    val schema = schemaStr[PolyBox[String]]
    schema should include("value")
    schema should include("string")
  }

  test("PolyObjectApiDataCompanion derives a GenObjectCodec for the wrapper") {
    val codec: GenObjectCodec[PolyRec[String]] = PolyRec.codec[String]
    JsonStringOutput.write(PolyRec("hi"))(codec) shouldBe """{"item":"hi"}"""
    JsonStringInput.read[PolyRec[String]]("""{"item":"hi"}""")(codec) shouldBe PolyRec("hi")

    schemaStr[PolyRec[Int]] should include("item")
  }

  test("Poly2ApiDataCompanion derives codec and schema for a two-parameter wrapper") {
    val codec = implicitly[GenCodec[PolyPair[Int, String]]]
    JsonStringOutput.write(PolyPair(1, "x"))(codec) shouldBe """{"first":1,"second":"x"}"""
    JsonStringInput.read[PolyPair[Int, String]]("""{"first":1,"second":"x"}""")(codec) shouldBe PolyPair(1, "x")

    val schema = schemaStr[PolyPair[Int, String]]
    schema should include("first")
    schema should include("second")
  }

  test("ApiDataCompanionWithDeps derives codec and schema from the deps object and the bundle") {
    // Dep's instances come only from HolderDeps, Tag's only from the enclosing TestRestImplicits bundle.
    val codec = implicitly[GenCodec[Holder]]
    JsonStringOutput.write(Holder(Dep(7), Tag("x")))(codec) shouldBe """{"dep":{"n":7},"tag":"tag:x"}"""
    JsonStringInput.read[Holder]("""{"dep":{"n":7},"tag":"tag:x"}""")(codec) shouldBe Holder(Dep(7), Tag("x"))

    schemaStr[Holder] shouldBe
      """{
        |  "type": "object",
        |  "properties": {
        |    "dep": {
        |      "type": "object",
        |      "properties": {
        |        "n": {
        |          "type": "integer",
        |          "format": "int32"
        |        }
        |      },
        |      "required": [
        |        "n"
        |      ]
        |    },
        |    "tag": {
        |      "type": "string"
        |    }
        |  },
        |  "required": [
        |    "dep",
        |    "tag"
        |  ]
        |}""".stripMargin
  }
}
