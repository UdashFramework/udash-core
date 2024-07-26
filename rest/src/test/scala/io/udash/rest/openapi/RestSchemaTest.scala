package io.udash
package rest.openapi

import com.avsystem.commons._
import com.avsystem.commons.misc.{AbstractValueEnum, AbstractValueEnumCompanion, EnumCtx}
import com.avsystem.commons.serialization.json.JsonStringOutput
import com.avsystem.commons.serialization.{GenCodec, flatten, name, optionalParam, transparent}
import io.udash.rest.openapi.adjusters.description
import io.udash.rest.{DefaultRestImplicits, PolyRestDataCompanion, RestDataCompanion, RestDataCompanionWithDeps}
import org.scalatest.funsuite.AnyFunSuite

class Fuu[T](thing: T)

object FullyQualifiedNames extends DefaultRestImplicits {
  implicit def fullyQualifiedSchemaName[T: ClassTag]: GeneratedSchemaName[T] =
    GeneratedSchemaName.some(classTag[T].runtimeClass.getName.stripSuffix("$").replace('$', '.'))
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
  @description("optional thing") @optionalParam opty: Opt[String] = Opt("defaultThatMustBeIgnored"),
  @description("serious string") str: Opt[String] = Opt.Empty
)
object KejsKlass extends RestDataCompanion[KejsKlass]

@description("wrapped string")
@transparent case class Wrap(str: String)
object Wrap extends RestDataCompanion[Wrap]

case class PlainGenericCC[+T](thing: T)
object PlainGenericCC extends PolyRestDataCompanion[PlainGenericCC]

case class GenCC[+T >: Null](@customWa[T](null) value: T)
object GenCC extends RestDataCompanion[GenCC[String]]

final class KeyEnum(implicit enumCtx: EnumCtx) extends AbstractValueEnum
object KeyEnum extends AbstractValueEnumCompanion[KeyEnum] {
  final val First, Second, Third, Fourth: Value = new KeyEnum
}

@flatten("tpe")
sealed trait HierarchyRoot[+T]
case class HierarchyCase[+T](value: T) extends HierarchyRoot[T]
object HierarchyCase {
  implicit val stringRestSchema: RestSchema[HierarchyCase[String]] =
    RestStructure.materialize[HierarchyCase[String]] match {
      case caseStructure: RestStructure.Case[HierarchyCase[String]] =>
        caseStructure.caseSchema("tpe".opt).named("CustomHierarchyCaseName")
      case _ =>
        throw new Exception("expected Record or Singleton REST structure")
    }
}
object HierarchyRoot {
  implicit val stringRestSchema: RestSchema[HierarchyRoot[String]] =
    RestStructure.materialize[HierarchyRoot[String]].standaloneSchema.named("StringHierarchy")
}

@flatten("case") sealed trait FullyQualifiedHierarchy
object FullyQualifiedHierarchy extends RestDataCompanionWithDeps[FullyQualifiedNames.type, FullyQualifiedHierarchy] {
  case class Foo(str: String) extends FullyQualifiedHierarchy
  case class Bar(int: Int) extends FullyQualifiedHierarchy
  object Bar extends RestDataCompanionWithDeps[FullyQualifiedNames.type, Bar]
  case object Baz extends FullyQualifiedHierarchy
}

@flatten("case")
sealed trait CustomSchemaNameHierarchy
object CustomSchemaNameHierarchy extends RestDataCompanion[CustomSchemaNameHierarchy] {
  // annotation value should be used as schema name, but NOT as type discriminator value
  @schemaName("CustomSchemaName123")
  case class CustomSchemaName(str: String) extends CustomSchemaNameHierarchy

  // annotation value should be used as both schema name and type discriminator value
  @name("CustomName123")
  case class CustomName(str: String) extends CustomSchemaNameHierarchy

  // @schemaName should "win" with @name as schema name
  @schemaName("CustomSchemaNameBoth123") @name("CustomNameBoth123")
  case class CustomNameBoth(str: String) extends CustomSchemaNameHierarchy
}

class RestSchemaTest extends AnyFunSuite {
  private def schemaStr[T](implicit schema: RestSchema[T]): String =
    printSchema(new InliningResolver().resolve(schema))

  private def resolvedSchemas[T](implicit schema: RestSchema[T]): IMap[String, RefOr[Schema]] = {
    val registry = new SchemaRegistry(name => s"#/testSchemas/$name")
    registry.resolve(schema)
    registry.registeredSchemas
  }

  private def allSchemasStr[T: RestSchema]: String =
    JsonStringOutput.writePretty(resolvedSchemas[T])

  private def printSchema(schema: RefOr[Schema]): String =
    JsonStringOutput.writePretty(schema)

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

  test("transparent wrapper") {
    assert(schemaStr[Wrap] ==
      """{
        |  "type": "string",
        |  "description": "wrapped string"
        |}""".stripMargin)
  }

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

  test("nullable enum") {
    assert(schemaStr[Opt[KeyEnum]] ==
      """{
        |  "type": "string",
        |  "nullable": true,
        |  "enum": [
        |    null,
        |    "First",
        |    "Second",
        |    "Third",
        |    "Fourth"
        |  ]
        |}""".stripMargin)

    assert(schemaStr[Opt[Opt[KeyEnum]]] == schemaStr[Opt[KeyEnum]])
  }

  test("Poly sealed hierarchy") {
    val schemas = resolvedSchemas[HierarchyRoot[String]]

    assert(schemas.contains("CustomHierarchyCaseName"))
    assert(printSchema(schemas("CustomHierarchyCaseName")) ==
      """{
        |  "type": "object",
        |  "properties": {
        |    "tpe": {
        |      "type": "string",
        |      "enum": [
        |        "HierarchyCase"
        |      ]
        |    },
        |    "value": {
        |      "type": "string"
        |    }
        |  },
        |  "required": [
        |    "tpe",
        |    "value"
        |  ]
        |}""".stripMargin)

    assert(printSchema(schemas("StringHierarchy")) ==
      """{
        |  "type": "object",
        |  "oneOf": [
        |    {
        |      "$ref": "#/testSchemas/CustomHierarchyCaseName"
        |    }
        |  ],
        |  "discriminator": {
        |    "propertyName": "tpe",
        |    "mapping": {
        |      "HierarchyCase": "#/testSchemas/CustomHierarchyCaseName"
        |    }
        |  }
        |}""".stripMargin)
  }

  test("flat sealed hierarchy schema with customized schema names") {
    assert(allSchemasStr[FullyQualifiedHierarchy] ==
      """{
        |  "io.udash.rest.openapi.FullyQualifiedHierarchy": {
        |    "type": "object",
        |    "oneOf": [
        |      {
        |        "$ref": "#/testSchemas/io.udash.rest.openapi.FullyQualifiedHierarchy.Foo"
        |      },
        |      {
        |        "$ref": "#/testSchemas/taggedio.udash.rest.openapi.FullyQualifiedHierarchy.Bar"
        |      },
        |      {
        |        "$ref": "#/testSchemas/io.udash.rest.openapi.FullyQualifiedHierarchy.Baz"
        |      }
        |    ],
        |    "discriminator": {
        |      "propertyName": "case",
        |      "mapping": {
        |        "Foo": "#/testSchemas/io.udash.rest.openapi.FullyQualifiedHierarchy.Foo",
        |        "Bar": "#/testSchemas/taggedio.udash.rest.openapi.FullyQualifiedHierarchy.Bar",
        |        "Baz": "#/testSchemas/io.udash.rest.openapi.FullyQualifiedHierarchy.Baz"
        |      }
        |    }
        |  },
        |  "io.udash.rest.openapi.FullyQualifiedHierarchy.Bar": {
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
        |  "io.udash.rest.openapi.FullyQualifiedHierarchy.Baz": {
        |    "type": "object",
        |    "properties": {
        |      "case": {
        |        "type": "string",
        |        "enum": [
        |          "Baz"
        |        ]
        |      }
        |    },
        |    "required": [
        |      "case"
        |    ]
        |  },
        |  "io.udash.rest.openapi.FullyQualifiedHierarchy.Foo": {
        |    "type": "object",
        |    "properties": {
        |      "case": {
        |        "type": "string",
        |        "enum": [
        |          "Foo"
        |        ]
        |      },
        |      "str": {
        |        "type": "string"
        |      }
        |    },
        |    "required": [
        |      "case",
        |      "str"
        |    ]
        |  },
        |  "taggedio.udash.rest.openapi.FullyQualifiedHierarchy.Bar": {
        |    "allOf": [
        |      {
        |        "type": "object",
        |        "properties": {
        |          "case": {
        |            "type": "string",
        |            "enum": [
        |              "Bar"
        |            ]
        |          }
        |        },
        |        "required": [
        |          "case"
        |        ]
        |      },
        |      {
        |        "$ref": "#/testSchemas/io.udash.rest.openapi.FullyQualifiedHierarchy.Bar"
        |      }
        |    ]
        |  }
        |}""".stripMargin)
  }

  test("Customized schema name") {
    assert(allSchemasStr[CustomSchemaNameHierarchy] ==
      """{
        |  "CustomName123": {
        |    "type": "object",
        |    "properties": {
        |      "case": {
        |        "type": "string",
        |        "enum": [
        |          "CustomName123"
        |        ]
        |      },
        |      "str": {
        |        "type": "string"
        |      }
        |    },
        |    "required": [
        |      "case",
        |      "str"
        |    ]
        |  },
        |  "CustomSchemaName123": {
        |    "type": "object",
        |    "properties": {
        |      "case": {
        |        "type": "string",
        |        "enum": [
        |          "CustomSchemaName"
        |        ]
        |      },
        |      "str": {
        |        "type": "string"
        |      }
        |    },
        |    "required": [
        |      "case",
        |      "str"
        |    ]
        |  },
        |  "CustomSchemaNameBoth123": {
        |    "type": "object",
        |    "properties": {
        |      "case": {
        |        "type": "string",
        |        "enum": [
        |          "CustomNameBoth123"
        |        ]
        |      },
        |      "str": {
        |        "type": "string"
        |      }
        |    },
        |    "required": [
        |      "case",
        |      "str"
        |    ]
        |  },
        |  "CustomSchemaNameHierarchy": {
        |    "type": "object",
        |    "oneOf": [
        |      {
        |        "$ref": "#/testSchemas/CustomSchemaName123"
        |      },
        |      {
        |        "$ref": "#/testSchemas/CustomName123"
        |      },
        |      {
        |        "$ref": "#/testSchemas/CustomSchemaNameBoth123"
        |      }
        |    ],
        |    "discriminator": {
        |      "propertyName": "case",
        |      "mapping": {
        |        "CustomSchemaName": "#/testSchemas/CustomSchemaName123",
        |        "CustomName123": "#/testSchemas/CustomName123",
        |        "CustomNameBoth123": "#/testSchemas/CustomSchemaNameBoth123"
        |      }
        |    }
        |  }
        |}""".stripMargin)
  }
}
