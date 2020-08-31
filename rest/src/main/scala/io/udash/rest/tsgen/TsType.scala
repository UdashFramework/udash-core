package io.udash.rest
package tsgen

import com.avsystem.commons.Opt

sealed trait TsType extends TsReference
sealed abstract class TsTypeCompanion[TsT <: TsType, Tag[X] <: TsTypeTag[TsT, X]] {
  def apply[T](implicit tag: Tag[T]): TsT = tag.tsType
}

sealed trait TsParamType extends TsType

trait TsPlainType extends TsParamType {
  def plainCodecRef: Opt[TsReference]
}
object TsPlainType extends TsTypeCompanion[TsPlainType, TsPlainTypeTag]

trait TsJsonType extends TsParamType {
  def jsonCodecRef: Opt[TsReference]
}
object TsJsonType extends TsTypeCompanion[TsJsonType, TsJsonTypeTag]

trait TsBodyType extends TsParamType {
  def bodyCodecRef: Opt[TsReference]
}
object TsBodyType extends TsTypeCompanion[TsBodyType, TsBodyTypeTag]

trait TsResponseType extends TsType {
  def responseReaderRef: Opt[TsReference]
}
object TsResponseType extends TsTypeCompanion[TsResponseType, TsResponseTypeTag]

object TsType {
  def nullableJson(tpe: TsJsonType): TsJsonType = new TsJsonType {
    def resolve(ctx: TsGenerationCtx): String =
      s"${tpe.resolve(ctx)} | null"
    def jsonCodecRef: Opt[TsReference] =
      tpe.jsonCodecRef.map(ref => ctx => s"${ctx.codecsModule}.nullable(${ref.resolve(ctx)})")
  }

  def arrayJson(tpe: TsJsonType): TsJsonType = new TsJsonType {
    def resolve(ctx: TsGenerationCtx): String =
      s"${tpe.resolve(ctx)}[]"
    def jsonCodecRef: Opt[TsReference] =
      tpe.jsonCodecRef.map(ref => ctx => s"${ctx.codecsModule}.array(${ref.resolve(ctx)})")
  }

  def jsonAsBody(tpe: TsJsonType): TsBodyType = new TsBodyType {
    def resolve(ctx: TsGenerationCtx): String = tpe.resolve(ctx)
    def bodyCodecRef: Opt[TsReference] =
      tpe.jsonCodecRef.map(ref => ctx => s"${ctx.codecsModule}.bodyFromJson(${ref.resolve(ctx)})")
  }

  def bodyAsResponse(tpe: TsBodyType): TsResponseType = new TsResponseType {
    def resolve(ctx: TsGenerationCtx): String = tpe.resolve(ctx)
    def responseReaderRef: Opt[TsReference] =
      tpe.bodyCodecRef.map(ref => ctx => s"${ctx.codecsModule}.responseFromBody(${ref.resolve(ctx)})")
  }

  final val Void: TsResponseType = new TsResponseType {
    def resolve(ctx: TsGenerationCtx): String = "void"
    def responseReaderRef: Opt[TsReference] = Opt(ctx => s"${ctx.codecsModule}.Void")
  }

  final val Never: TsPlainType with TsJsonType = new TsPlainType with TsJsonType {
    def resolve(ctx: TsGenerationCtx): String = "never"
    def plainCodecRef: Opt[TsReference] = Opt.Empty
    def jsonCodecRef: Opt[TsReference] = Opt.Empty
  }

  final val Boolean: TsPlainType with TsJsonType = new TsPlainType with TsJsonType {
    def resolve(ctx: TsGenerationCtx): String = "boolean"
    def plainCodecRef: Opt[TsReference] = Opt(ctx => s"${ctx.codecsModule}.Boolean")
    def jsonCodecRef: Opt[TsReference] = Opt.Empty
  }

  final val Integer: TsPlainType with TsJsonType = new TsPlainType with TsJsonType {
    def resolve(ctx: TsGenerationCtx): String = "number"
    def plainCodecRef: Opt[TsReference] = Opt(ctx => s"${ctx.codecsModule}.Integer")
    def jsonCodecRef: Opt[TsReference] = Opt.Empty
  }

  final val Float: TsPlainType with TsJsonType = new TsPlainType with TsJsonType {
    def resolve(ctx: TsGenerationCtx): String = "number"
    def plainCodecRef: Opt[TsReference] = Opt(ctx => s"${ctx.codecsModule}.Float")
    def jsonCodecRef: Opt[TsReference] = Opt.Empty
  }

  final val String: TsPlainType with TsJsonType = new TsPlainType with TsJsonType {
    def resolve(ctx: TsGenerationCtx): String = "string"
    def plainCodecRef: Opt[TsReference] = Opt.Empty
    def jsonCodecRef: Opt[TsReference] = Opt.Empty
  }
}
