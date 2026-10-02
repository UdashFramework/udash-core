package io.udash
package rest

import com.avsystem.commons.misc.NamedEnum
import com.avsystem.commons.serialization.GenCodec.ReadFailure
import com.avsystem.commons.serialization.json.{JsonStringInput, JsonStringOutput}
import com.avsystem.commons.serialization.{GenCodec, GenObjectCodec, flatten}
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

// NamedEnum whose named RestSchema is provided by RestNamedValueEnumCompanion.
sealed trait TaskPriority extends NamedEnum
object TaskPriority extends RestNamedValueEnumCompanion[TaskPriority] {
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

// Self-contained deps object (must extend DefaultRestImplicits, since ApiDataCompanionWithDeps
// materializes solely from D) that additionally supplies serialization/schema for Dep.
object HolderDeps extends DefaultRestImplicits {
  implicit val depCodec: GenCodec[Dep] = GenCodec.materialize[Dep]
  implicit val depSchema: RestSchema[Dep] = RestStructure.materialize[Dep].standaloneSchema
}

final case class Holder(dep: Dep, label: String)
object Holder extends CustomRestApis.ApiDataCompanionWithDeps[HolderDeps.type, Holder]

class ApiDataCustomImplicitsSchemaTest extends AnyFunSuite with Matchers {
  private def schemaStr[T](implicit schema: RestSchema[T]): String =
    JsonStringOutput.writePretty(new InliningResolver().resolve(schema))

  test("custom implicit schema is used for a type with no default schema") {
    // Tag has no default RestSchema - the only instance comes from TestRestImplicits.
    import TestRestImplicits.tagSchema
    schemaStr[Tag] shouldBe
      """{
        |  "type": "string"
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

  test("RestNamedValueEnumCompanion produces a string enum schema") {
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

  test("ApiDataCompanionWithDeps derives codec and schema from the deps object") {
    // Dep has no default codec/schema; Holder only works because HolderDeps supplies them.
    val codec = implicitly[GenCodec[Holder]]
    JsonStringOutput.write(Holder(Dep(7), "hi"))(codec) shouldBe """{"dep":{"n":7},"label":"hi"}"""
    JsonStringInput.read[Holder]("""{"dep":{"n":7},"label":"hi"}""")(codec) shouldBe Holder(Dep(7), "hi")

    val schema = schemaStr[Holder]
    schema should include("dep")
    schema should include("label")
    schema should include("n") // the nested Dep schema, resolved via the deps object
  }
}
