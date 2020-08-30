package io.udash.rest
package tsgen

import com.avsystem.commons.Opt
import com.avsystem.commons.serialization.json.JsonStringOutput

sealed trait TsType {
  def reference(ctx: TsGenerationCtx): String
}
sealed abstract class TsTypeCompanion[TsT <: TsType, Tag[X] <: TsTypeTag[TsT, X]] {
  def apply[T](implicit tag: Tag[T]): TsT = tag.tsType
}

sealed trait TsParamType extends TsType

sealed trait TsPlainType extends TsParamType {
  def plainCodecRef(ctx: TsGenerationCtx): String
}
object TsPlainType extends TsTypeCompanion[TsPlainType, TsPlainTypeTag]

sealed trait TsJsonType extends TsParamType {
  def jsonCodecRef(ctx: TsGenerationCtx): String
}
object TsJsonType extends TsTypeCompanion[TsJsonType, TsJsonTypeTag]

sealed trait TsBodyType extends TsParamType {
  def bodyCodecRef(ctx: TsGenerationCtx): String
}
object TsBodyType extends TsTypeCompanion[TsBodyType, TsBodyTypeTag]

sealed trait TsResponseType extends TsType {
  def responseReaderRef(ctx: TsGenerationCtx): String
}
object TsResponseType extends TsTypeCompanion[TsResponseType, TsResponseTypeTag]

object TsType {
  def stdPlainAndJson(repr: String, codec: String): TsPlainType with TsJsonType =
    new TsPlainType with TsJsonType {
      def reference(ctx: TsGenerationCtx): String = repr
      def plainCodecRef(ctx: TsGenerationCtx): String = s"${ctx.codecsModule}.$codec"
      def jsonCodecRef(ctx: TsGenerationCtx): String = s"${ctx.codecsModule}.$codec"
    }

  def nullableJson(tpe: TsJsonType): TsJsonType = new TsJsonType {
    def reference(ctx: TsGenerationCtx): String =
      s"${tpe.reference(ctx)} | null"
    def jsonCodecRef(ctx: TsGenerationCtx): String =
      s"${ctx.codecsModule}.nullable(${tpe.jsonCodecRef(ctx)})"
  }

  def arrayJson(tpe: TsJsonType): TsJsonType = new TsJsonType {
    def reference(ctx: TsGenerationCtx): String =
      s"${tpe.reference(ctx)}[]"
    def jsonCodecRef(ctx: TsGenerationCtx): String =
      s"${ctx.codecsModule}.array(${tpe.jsonCodecRef(ctx)})"
  }

  def jsonAsBody(tpe: TsJsonType): TsBodyType = new TsBodyType {
    def reference(ctx: TsGenerationCtx): String = tpe.reference(ctx)
    def bodyCodecRef(ctx: TsGenerationCtx): String =
      s"${ctx.codecsModule}.bodyFromJson(${tpe.jsonCodecRef(ctx)})"
  }

  def bodyAsResponse(tpe: TsBodyType): TsResponseType = new TsResponseType {
    def reference(ctx: TsGenerationCtx): String = tpe.reference(ctx)
    def responseReaderRef(ctx: TsGenerationCtx): String =
      s"${ctx.codecsModule}.responseFromBody(${tpe.bodyCodecRef(ctx)})"
  }

  final val Void: TsResponseType = new TsResponseType {
    def reference(ctx: TsGenerationCtx): String = "void"
    def responseReaderRef(ctx: TsGenerationCtx): String = s"${ctx.codecsModule}.Void"
  }

  final val Never = stdPlainAndJson("never", "Never")
  final val Undefined = stdPlainAndJson("undefined", "Undefined")
  final val Boolean = stdPlainAndJson("boolean", "Boolean")
  final val Integer = stdPlainAndJson("number", "Integer")
  final val Float = stdPlainAndJson("number", "Float")
  final val String = stdPlainAndJson("string", "String")

  final case class Record(name: String, fields: List[TsField]) extends TsJsonType with TsDefinition {
    def reference(ctx: TsGenerationCtx): String =
      ctx.resolve(this)

    def jsonCodecRef(ctx: TsGenerationCtx): String =
      s"${reference(ctx)}.codec"

    def definition(ctx: TsGenerationCtx): String = {
      val fieldDefs = fields.iterator
        .map(f => "        " + f.decl(ctx))
        .mkString("\n", ",\n", "\n    ")

      val fieldCodecs = fields.iterator
        .map(f => s"[${JsonStringOutput.write(f.name)}, ${f.tpe().jsonCodecRef(ctx)}]")
        .mkString("[", ", ", "]")

      //TODO: respect raw names
      s"""export class $name {
         |    constructor($fieldDefs) {}
         |    static readonly codec = ${ctx.codecsModule}.record($name, $fieldCodecs)
         |}
         |""".stripMargin
    }
  }
}

final case class TsField(
  name: String,
  rawName: String,
  tpe: () => TsJsonType, // function because records may be recursively defined
  optional: Boolean = false,
  repeated: Boolean = false,
  defaultValue: Opt[String] = Opt.Empty
) {
  def decl(ctx: TsGenerationCtx): String = {
    val qmark = if (optional) "?" else ""
    val ellipsis = if (repeated) "..." else ""
    val tpeRef = tpe().reference(ctx)
    val defValue = defaultValue.fold("")(v => s" = $v")
    s"readonly $ellipsis$name$qmark: $tpeRef$defValue"
  }
}
