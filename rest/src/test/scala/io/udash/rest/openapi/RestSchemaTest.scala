package io.udash
package rest.openapi

import com.avsystem.commons._
import com.avsystem.commons.misc.{AbstractValueEnum, AbstractValueEnumCompanion, EnumCtx}
import com.avsystem.commons.serialization.json.JsonStringOutput
import com.avsystem.commons.serialization.{GenCodec, name, optionalParam, transparent}
import io.udash.rest.openapi.adjusters.description
import io.udash.rest.{PolyRestDataCompanion, RestDataCompanion}
import org.scalatest.FunSuite

class Fuu[T](thing: T)

class RestSchemaTest extends FunSuite {
  private def schemaStr[T](implicit schema: RestSchema[T]): String =
    JsonStringOutput.writePretty(new InliningResolver().resolve(schema))

  trait Dependency
  object Dependency {
    implicit val codec: GenCodec[Dependency] = null
    implicit val schema: RestSchema[Dependency] = RestSchema.ref("Dependency.json")
  }

  @description("kejs klass")
  case class KejsKlass(
    @name("integer") @customWa(42) int: Int,
    @description("serious dependency") dep: Dependency,
    @description("optional thing") @optionalParam opty: Opt[String],
    @description("serious string") str: Opt[String] = Opt.Empty
  )
  object KejsKlass extends RestDataCompanion[KejsKlass]

  test("case class") {
    assert(schemaStr[KejsKlass] ==
      """{
        |  "type": "object",
        |  "description": "kejs klass",
        |  "properties": {
        |    "integer": {
        |      "type": "integer",
        |      "format": "int32",
        |      "default": 42
        |    },
        |    "dep": {
        |      "description": "serious dependency",
        |      "allOf": [
        |        {
        |          "$ref": "Dependency.json"
        |        }
        |      ]
        |    },
        |    "opty": {
        |      "type": "string",
        |      "description": "optional thing"
        |    },
        |    "str": {
        |      "type": "string",
        |      "description": "serious string",
        |      "nullable": true,
        |      "default": null
        |    }
        |  },
        |  "required": [
        |    "dep"
        |  ]
        |}""".stripMargin)
  }

  @description("wrapped string")
  @transparent case class Wrap(str: String)
  object Wrap extends RestDataCompanion[Wrap]

  test("transparent wrapper") {
    assert(schemaStr[Wrap] ==
      """{
        |  "type": "string",
        |  "description": "wrapped string"
        |}""".stripMargin)
  }

  case class PlainGenericCC[+T](thing: T)
  object PlainGenericCC extends PolyRestDataCompanion[PlainGenericCC]

  test("generic case class") {
    assert(schemaStr[PlainGenericCC[String]] ==
      """{
        |  "type": "object",
        |  "properties": {
        |    "thing": {
        |      "type": "string"
        |    }
        |  },
        |  "required": [
        |    "thing"
        |  ]
        |}""".stripMargin)
  }

  case class GenCC[+T >: Null](@customWa[T](null) value: T)
  object GenCC extends RestDataCompanion[GenCC[String]]

  test("generic case class with with annotation") {
    assert(schemaStr[GenCC[String]] ==
      """{
        |  "type": "object",
        |  "properties": {
        |    "value": {
        |      "type": "string",
        |      "default": null
        |    }
        |  }
        |}""".stripMargin
    )
  }

  final class KeyEnum(implicit enumCtx: EnumCtx) extends AbstractValueEnum
  object KeyEnum extends AbstractValueEnumCompanion[KeyEnum] {
    final val First, Second, Third, Fourth: Value = new KeyEnum
  }

  test("map with enum key") {
    assert(schemaStr[Map[KeyEnum, String]] ==
      """{
        |  "type": "object",
        |  "properties": {
        |    "First": {
        |      "type": "string"
        |    },
        |    "Second": {
        |      "type": "string"
        |    },
        |    "Third": {
        |      "type": "string"
        |    },
        |    "Fourth": {
        |      "type": "string"
        |    }
        |  },
        |  "additionalProperties": false
        |}""".stripMargin)
  }
}
