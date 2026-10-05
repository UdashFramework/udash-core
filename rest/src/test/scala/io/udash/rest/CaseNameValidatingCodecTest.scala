package io.udash
package rest

import com.avsystem.commons.serialization.GenCodec.ReadFailure
import com.avsystem.commons.serialization.json.{JsonStringInput, JsonStringOutput}
import com.avsystem.commons.serialization.{GenCodec, flatten}
import io.udash.rest.openapi.{InliningResolver, RestFlattenedStructure, RestSchema, RestStructure}
import io.udash.rest.util.CaseNameValidatingCodec
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

@flatten("kind") sealed trait Shape
object Shape extends RestDataCompanion[Shape]
final case class Circle(radius: Int) extends Shape
final case class Square(side: Int) extends Shape

class CaseNameValidatingCodecTest extends AnyFunSuite with Matchers {
  // A codec for a single case that delegates to the root sealed-hierarchy codec but validates the discriminator.
  private val circleCodec: GenCodec[Circle] =
    CaseNameValidatingCodec[Circle, Shape](Shape.codec, caseFieldName = "kind", caseName = "Circle")

  test("writes the discriminator field via the root codec") {
    val json = JsonStringOutput.write(Circle(5))(circleCodec)
    json shouldBe """{"kind":"Circle","radius":5}"""
  }

  test("reads an object with the matching discriminator") {
    val circle = JsonStringInput.read[Circle]("""{"kind":"Circle","radius":5}""")(circleCodec)
    circle shouldBe Circle(5)
  }

  test("rejects an object with an unexpected discriminator value") {
    val ex = intercept[ReadFailure] {
      JsonStringInput.read[Circle]("""{"kind":"Square","side":3}""")(circleCodec)
    }
    ex.getMessage should include("kind")
    ex.getMessage should include("Circle")
    ex.getMessage should include("Square")
  }

  test("round-trips a value through the validating codec") {
    val json = JsonStringOutput.write(Circle(7))(circleCodec)
    JsonStringInput.read[Circle](json)(circleCodec) shouldBe Circle(7)
  }

  test("caseRestSchema forces the discriminator field into a case schema") {
    val schema: RestSchema[Circle] =
      RestFlattenedStructure.caseRestSchema(RestStructure.materialize[Circle], "kind")
    val resolved = JsonStringOutput.writePretty(new InliningResolver().resolve(schema))
    resolved should include("kind")
    resolved should include("radius")
  }

  test("caseRestSchema rejects a non-Case structure") {
    val schema: RestSchema[Shape] =
      RestFlattenedStructure.caseRestSchema(RestStructure.materialize[Shape], "kind")
    // the failure is produced lazily, when the schema is resolved
    intercept[IllegalArgumentException] {
      new InliningResolver().resolve(schema)
    }
  }
}
