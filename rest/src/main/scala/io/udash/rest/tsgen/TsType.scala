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
    def resolve(gen: TsGenerator): String =
      s"${tpe.resolve(gen)} | null"
    def jsonCodecRef: Opt[TsReference] =
      tpe.jsonCodecRef.map(ref => gen => s"${gen.codecsModule}.nullable(${ref.resolve(gen)})")
  }

  def arrayJson(tpe: TsJsonType): TsJsonType = new TsJsonType {
    def resolve(gen: TsGenerator): String =
      s"${tpe.resolve(gen)}[]"
    def jsonCodecRef: Opt[TsReference] =
      tpe.jsonCodecRef.map(ref => gen => s"${gen.codecsModule}.array(${ref.resolve(gen)})")
  }

  def jsonAsBody(tpe: TsJsonType): TsBodyType = new TsBodyType {
    def resolve(gen: TsGenerator): String = tpe.resolve(gen)
    def bodyCodecRef: Opt[TsReference] =
      tpe.jsonCodecRef.map(ref => gen => s"${gen.codecsModule}.bodyFromJson(${ref.resolve(gen)})")
  }

  def bodyAsResponse(tpe: TsBodyType): TsResponseType = new TsResponseType {
    def resolve(gen: TsGenerator): String = tpe.resolve(gen)
    def responseReaderRef: Opt[TsReference] =
      tpe.bodyCodecRef.map(ref => gen => s"${gen.codecsModule}.responseFromBody(${ref.resolve(gen)})")
  }

  final val Void: TsResponseType = new TsResponseType {
    def resolve(gen: TsGenerator): String = "void"
    def responseReaderRef: Opt[TsReference] = Opt(gen => s"${gen.codecsModule}.Void")
  }

  final val Never: TsPlainType with TsJsonType = new TsPlainType with TsJsonType {
    def resolve(gen: TsGenerator): String = "never"
    def plainCodecRef: Opt[TsReference] = Opt.Empty
    def jsonCodecRef: Opt[TsReference] = Opt.Empty
  }

  final val Boolean: TsPlainType with TsJsonType = new TsPlainType with TsJsonType {
    def resolve(gen: TsGenerator): String = "boolean"
    def plainCodecRef: Opt[TsReference] = Opt(gen => s"${gen.codecsModule}.Boolean")
    def jsonCodecRef: Opt[TsReference] = Opt.Empty
  }

  final val Integer: TsPlainType with TsJsonType = new TsPlainType with TsJsonType {
    def resolve(gen: TsGenerator): String = "number"
    def plainCodecRef: Opt[TsReference] = Opt(gen => s"${gen.codecsModule}.Integer")
    def jsonCodecRef: Opt[TsReference] = Opt.Empty
  }

  final val Float: TsPlainType with TsJsonType = new TsPlainType with TsJsonType {
    def resolve(gen: TsGenerator): String = "number"
    def plainCodecRef: Opt[TsReference] = Opt(gen => s"${gen.codecsModule}.Float")
    def jsonCodecRef: Opt[TsReference] = Opt.Empty
  }

  final val String: TsPlainType with TsJsonType = new TsPlainType with TsJsonType {
    def resolve(gen: TsGenerator): String = "string"
    def plainCodecRef: Opt[TsReference] = Opt.Empty
    def jsonCodecRef: Opt[TsReference] = Opt.Empty
  }

  final val Timestamp: TsPlainType with TsJsonType = new TsPlainType with TsJsonType {
    def resolve(gen: TsGenerator): String = "Date"
    def plainCodecRef: Opt[TsReference] = Opt(gen => s"${gen.codecsModule}.Timestamp")
    def jsonCodecRef: Opt[TsReference] = Opt(gen => s"${gen.codecsModule}.Timestamp")
  }
}
