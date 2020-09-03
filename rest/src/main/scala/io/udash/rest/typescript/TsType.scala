package io.udash.rest
package typescript

sealed trait TsType extends TsReference
sealed abstract class TsTypeCompanion[TsT <: TsType, Tag[X] <: TsTypeTag[TsT, X]] {
  def apply[T](implicit tag: Tag[T]): TsT = tag.tsType
}

trait TsPlainType extends TsType {
  def mkPlainWrite(gen: TsGenerator, valueRef: String): String
  def mkPlainWriter(gen: TsGenerator): String = s"(v => ${mkPlainWrite(gen, "v")})"
  def dictionaryKeyType: TsType = TsType.String
}
object TsPlainType extends TsTypeCompanion[TsPlainType, TsPlainTypeTag]

trait TsJsonType extends TsType {
  /**
   * When `true`, indicates that JSON writing and reading simply returns the same value (i.e. no
   * actual serialization is done), only by possibly casting it into appropriate type. This information is
   * used to avoid generating unnecessary readers and writers for sealed traits and case classes.
   */
  def transparent: Boolean

  def mkJsonWrite(gen: TsGenerator, valueRef: String): String
  def mkJsonRead(gen: TsGenerator, valueRef: String): String

  def mkJsonWriter(gen: TsGenerator): String = s"(v => ${mkJsonWrite(gen, "v")})"
  def mkJsonReader(gen: TsGenerator): String = s"(v => ${mkJsonRead(gen, "v")})"
}
object TsJsonType extends TsTypeCompanion[TsJsonType, TsJsonTypeTag]

trait TsBodyType extends TsType {
  def mkBodyWrite(gen: TsGenerator, valueRef: String): String
  def mkBodyRead(gen: TsGenerator, valueRef: String): String

  def mkBodyWriter(gen: TsGenerator): String = s"(v => ${mkBodyWrite(gen, "v")})"
  def mkBodyReader(gen: TsGenerator): String = s"(v => ${mkBodyRead(gen, "v")})"
}
object TsBodyType extends TsTypeCompanion[TsBodyType, TsBodyTypeTag]

trait TsResponseType extends TsType {
  def mkResponseRead(gen: TsGenerator, valueRef: String): String
  def mkResponseReader(gen: TsGenerator): String = s"(v => ${mkResponseRead(gen, "v")})"
}
object TsResponseType extends TsTypeCompanion[TsResponseType, TsResponseTypeTag]

object TsType {
  def nullableJson(tpe: TsJsonType): TsJsonType = new TsJsonType {
    def resolve(gen: TsGenerator): String =
      s"${tpe.resolve(gen)} | null"

    def transparent: Boolean = tpe.transparent

    def mkJsonWrite(gen: TsGenerator, valueRef: String): String =
      if (transparent) valueRef
      else s"$valueRef === null ? null : ${tpe.mkJsonWrite(gen, valueRef)}"

    def mkJsonRead(gen: TsGenerator, valueRef: String): String =
      if (transparent) s"$valueRef as ${resolve(gen)}"
      else s"$valueRef === null ? null : ${tpe.mkJsonRead(gen, valueRef)}"
  }

  def arrayJson(tpe: TsJsonType): TsJsonType = new TsJsonType {
    def resolve(gen: TsGenerator): String =
      s"${tpe.resolve(gen)}[]"

    def transparent: Boolean = tpe.transparent

    def mkJsonWrite(gen: TsGenerator, valueRef: String): String =
      if (transparent) valueRef
      else s"$valueRef.map(${tpe.mkJsonWriter(gen)})"

    def mkJsonRead(gen: TsGenerator, valueRef: String): String =
      if (transparent) s"$valueRef as ${resolve(gen)}"
      else s"($valueRef as any[]).map(${tpe.mkJsonReader(gen)})"
  }

  def dictionaryJson(keyType: TsPlainType, valueType: TsJsonType): TsJsonType = new TsJsonType {
    def transparent: Boolean = valueType.transparent

    def mkJsonWrite(gen: TsGenerator, valueRef: String): String =
      if(transparent) valueRef
      else s"${gen.codecsModule}.mapValues($valueRef, ${valueType.mkJsonWriter(gen)})"

    def mkJsonRead(gen: TsGenerator, valueRef: String): String =
      if(transparent) s"$valueRef as ${resolve(gen)}"
      else {
        val castValueRef = s"$valueRef as ${gen.codecsModule}.Dictionary<${keyType.dictionaryKeyType.resolve(gen)}, any>"
        s"${gen.codecsModule}.mapValues($castValueRef, ${valueType.mkJsonReader(gen)}, copy = false)"
      }

    def resolve(gen: TsGenerator): String =
      s"${gen.codecsModule}.Dictionary<${keyType.dictionaryKeyType.resolve(gen)}, ${valueType.resolve(gen)}>"
  }

  def jsonAsBody(tpe: TsJsonType): TsBodyType = new TsBodyType {
    def resolve(gen: TsGenerator): String = tpe.resolve(gen)

    def mkBodyWrite(gen: TsGenerator, valueRef: String): String =
      tpe.mkJsonWrite(gen, s"${gen.codecsModule}.jsonToBody($valueRef)")

    def mkBodyRead(gen: TsGenerator, valueRef: String): String =
      tpe.mkJsonRead(gen, s"${gen.codecsModule}.bodyToJson($valueRef)")
  }

  def bodyAsResponse(tpe: TsBodyType): TsResponseType = new TsResponseType {
    def resolve(gen: TsGenerator): String = tpe.resolve(gen)

    def mkResponseRead(gen: TsGenerator, valueRef: String): String =
      tpe.mkBodyRead(gen, s"${gen.codecsModule}.successfulResponseToBody($valueRef)")
  }

  final val Void: TsResponseType = new TsResponseType {
    def resolve(gen: TsGenerator): String = "void"

    def mkResponseRead(gen: TsGenerator, valueRef: String): String = "undefined"
  }

  final val Never: TsPlainType with TsJsonType = new TsPlainType with TsJsonType {
    def resolve(gen: TsGenerator): String = "never"
    def transparent: Boolean = true
    def mkPlainWrite(gen: TsGenerator, valueRef: String): String = valueRef
    def mkJsonWrite(gen: TsGenerator, valueRef: String): String = valueRef
    def mkJsonRead(gen: TsGenerator, valueRef: String): String = valueRef
  }

  final val Boolean: TsPlainType with TsJsonType = new TsPlainType with TsJsonType {
    def resolve(gen: TsGenerator): String = "boolean"
    def transparent: Boolean = true
    def mkPlainWrite(gen: TsGenerator, valueRef: String): String = s"$valueRef.toString()"
    def mkJsonWrite(gen: TsGenerator, valueRef: String): String = valueRef
    def mkJsonRead(gen: TsGenerator, valueRef: String): String = s"$valueRef as boolean"
  }

  final val Number: TsPlainType with TsJsonType = new TsPlainType with TsJsonType {
    def resolve(gen: TsGenerator): String = "number"
    def transparent: Boolean = true
    def mkPlainWrite(gen: TsGenerator, valueRef: String): String = s"$valueRef.toString()"
    def mkJsonWrite(gen: TsGenerator, valueRef: String): String = valueRef
    def mkJsonRead(gen: TsGenerator, valueRef: String): String = s"$valueRef as number"
    override def dictionaryKeyType: TsType = this
  }

  final val String: TsPlainType with TsJsonType = new TsPlainType with TsJsonType {
    def resolve(gen: TsGenerator): String = "string"
    def transparent: Boolean = true
    def mkPlainWrite(gen: TsGenerator, valueRef: String): String = valueRef
    def mkJsonWrite(gen: TsGenerator, valueRef: String): String = valueRef
    def mkJsonRead(gen: TsGenerator, valueRef: String): String = s"$valueRef as string"
  }

  final val Timestamp: TsPlainType with TsJsonType = new TsPlainType with TsJsonType {
    def resolve(gen: TsGenerator): String = "Date"
    def transparent: Boolean = false
    def mkPlainWrite(gen: TsGenerator, valueRef: String): String = s"$valueRef.toISOString()"
    def mkJsonWrite(gen: TsGenerator, valueRef: String): String = s"$valueRef.toISOString()"
    def mkJsonRead(gen: TsGenerator, valueRef: String): String = s"new Date($valueRef as string)"
  }
}
