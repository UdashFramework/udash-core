package io.udash
package rest.openapi

import com.avsystem.commons._
import com.avsystem.commons.serialization._
import com.avsystem.commons.serialization.json.JsonStringOutput
import io.udash.rest.RestDataCompanion
import io.udash.rest.openapi.adjusters.description
import org.scalatest.FunSuite

class Fuu[T](thing: T)

class RestSchemaTest extends FunSuite {
  private def schemasStr[T](implicit schema: RestSchema[T]): String = {
    val resolver = new SchemaRegistry
    resolver.resolve(schema)
    JsonStringOutput.writePretty(resolver.registeredSchemas)
  }

  trait Dependency
  object Dependency {
    implicit val codec: GenCodec[Dependency] = null
    implicit val schema: RestSchema[Dependency] = RestSchema.ref("Dependency.json")
  }

  @description("kejs klass")
  case class KejsKlass(
    @name("integer") @customWa(42) int: Int,
    @description("serious dependency") dep: Dependency,
    @description("serious string") str: Opt[String] = Opt.Empty
  )
  object KejsKlass extends RestDataCompanion[KejsKlass]

  test("case class") {
    assert(schemasStr[KejsKlass] ==
      """{
        |  "KejsKlass": {
        |    "type": "object",
        |    "description": "kejs klass",
        |    "properties": {
        |      "integer": {
        |        "type": "integer",
        |        "format": "int32",
        |        "default": 42
        |      },
        |      "dep": {
        |        "description": "serious dependency",
        |        "allOf": [
        |          {
        |            "$ref": "Dependency.json"
        |          }
        |        ]
        |      },
        |      "str": {
        |        "type": "string",
        |        "description": "serious string",
        |        "nullable": true,
        |        "default": null
        |      }
        |    },
        |    "required": [
        |      "dep"
        |    ]
        |  }
        |}""".stripMargin)
  }

  @description("wrapped string")
  @transparent case class Wrap(str: String)
  object Wrap extends RestDataCompanion[Wrap]

  test("transparent wrapper") {
    assert(schemasStr[Wrap] ==
      """{
        |  "Wrap": {
        |    "type": "string",
        |    "description": "wrapped string"
        |  }
        |}""".stripMargin)
  }

  case class GenCC[+T >: Null](@customWa[T](null) value: T)
  object GenCC extends RestDataCompanion[GenCC[String]]

  test("generic case class") {
    assert(schemasStr[GenCC[String]] ==
      """{
        |  "GenCC": {
        |    "type": "object",
        |    "properties": {
        |      "value": {
        |        "type": "string",
        |        "default": null
        |      }
        |    }
        |  }
        |}""".stripMargin
    )
  }

  sealed trait NestedBase
  @flatten sealed trait FlatBase extends NestedBase
  case class PlainCase(int: Int) extends FlatBase
  case class SpecializedCase(str: String) extends FlatBase
  object SpecializedCase extends RestDataCompanion[SpecializedCase]
  case class ExternalCase(thing: Int) extends FlatBase
  object ExternalCase {
    implicit val schema: RestSchema[ExternalCase] =
      RestSchema.ref("external.json")
  }
  case class UnnamedCase(foo: Double) extends FlatBase
  object UnnamedCase {
    implicit val schema: RestSchema[UnnamedCase] =
      RestSchema.plain(Schema(`type` = DataType.Object, title = "unnamed"))
  }
  case object SingletonCase extends FlatBase
  object FlatBase extends RestDataCompanion[FlatBase]
  object NestedBase extends RestDataCompanion[NestedBase]

  test("nested sealed hierarchy") {
    assert(schemasStr[NestedBase] ==
      """{
        |  "NestedBase": {
        |    "oneOf": [
        |      {
        |        "type": "object",
        |        "properties": {
        |          "PlainCase": {
        |            "$ref": "#/components/schemas/PlainCase"
        |          }
        |        },
        |        "required": [
        |          "PlainCase"
        |        ]
        |      },
        |      {
        |        "type": "object",
        |        "properties": {
        |          "SpecializedCase": {
        |            "$ref": "#/components/schemas/SpecializedCase"
        |          }
        |        },
        |        "required": [
        |          "SpecializedCase"
        |        ]
        |      },
        |      {
        |        "type": "object",
        |        "properties": {
        |          "ExternalCase": {
        |            "$ref": "external.json"
        |          }
        |        },
        |        "required": [
        |          "ExternalCase"
        |        ]
        |      },
        |      {
        |        "type": "object",
        |        "properties": {
        |          "UnnamedCase": {
        |            "type": "object",
        |            "title": "unnamed"
        |          }
        |        },
        |        "required": [
        |          "UnnamedCase"
        |        ]
        |      },
        |      {
        |        "type": "object",
        |        "properties": {
        |          "SingletonCase": {
        |            "$ref": "#/components/schemas/SingletonCase"
        |          }
        |        },
        |        "required": [
        |          "SingletonCase"
        |        ]
        |      }
        |    ]
        |  },
        |  "PlainCase": {
        |    "type": "object",
        |    "properties": {
        |      "int": {
        |        "type": "integer",
        |        "format": "int32"
        |      }
        |    },
        |    "required": [
        |      "int"
        |    ]
        |  },
        |  "SingletonCase": {
        |    "type": "object"
        |  },
        |  "SpecializedCase": {
        |    "type": "object",
        |    "properties": {
        |      "str": {
        |        "type": "string"
        |      }
        |    },
        |    "required": [
        |      "str"
        |    ]
        |  }
        |}""".stripMargin)
  }

  test("flat sealed hierarchy") {
    assert(schemasStr[FlatBase] ==
      """{
        |  "FlatBase": {
        |    "oneOf": [
        |      {
        |        "$ref": "#/components/schemas/PlainCase"
        |      },
        |      {
        |        "$ref": "#/components/schemas/SpecializedCase"
        |      },
        |      {
        |        "$ref": "external.json"
        |      },
        |      {
        |        "$ref": "#/components/schemas/UnnamedCase"
        |      },
        |      {
        |        "$ref": "#/components/schemas/SingletonCase"
        |      }
        |    ],
        |    "discriminator": {
        |      "propertyName": "_case",
        |      "mapping": {
        |        "ExternalCase": "external.json"
        |      }
        |    }
        |  },
        |  "PlainCase": {
        |    "type": "object",
        |    "properties": {
        |      "int": {
        |        "type": "integer",
        |        "format": "int32"
        |      }
        |    },
        |    "required": [
        |      "int"
        |    ]
        |  },
        |  "SingletonCase": {
        |    "type": "object"
        |  },
        |  "SpecializedCase": {
        |    "type": "object",
        |    "properties": {
        |      "str": {
        |        "type": "string"
        |      }
        |    },
        |    "required": [
        |      "str"
        |    ]
        |  },
        |  "UnnamedCase": {
        |    "type": "object",
        |    "title": "unnamed"
        |  }
        |}""".stripMargin)
  }
}
