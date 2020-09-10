package io.udash.rest
package typescript

sealed trait TsType extends TsReference
sealed abstract class TsTypeCompanion[TsT <: TsType, Tag[X] <: TsTypeTag[TsT, X]] {
  def apply[T](implicit tag: Tag[T]): TsT = tag.tsType
}

trait TsPlainType extends TsType {
  def mkPlainWrite(gen: TsGeneratorCtx, valueRef: String): String

  def mkOptionalPlainWrite(gen: TsGeneratorCtx, valueRef: String, optional: Boolean): String =
    if (optional) s"${gen.codecs}.mapUndefined(${mkPlainWriter(gen)}, $valueRef)"
    else mkPlainWrite(gen, valueRef)

  def mkPlainWriter(gen: TsGeneratorCtx): String = s"(v => ${mkPlainWrite(gen, "v")})"
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

  def mkJsonWrite(gen: TsGeneratorCtx, valueRef: String): String
  def mkJsonRead(gen: TsGeneratorCtx, valueRef: String): String

  def mkOptionalJsonWrite(gen: TsGeneratorCtx, valueRef: String, optional: Boolean): String =
    if (optional && !transparent) s"${gen.codecs}.mapUndefined(${mkJsonWriter(gen)}, $valueRef)"
    else mkJsonWrite(gen, valueRef)

  def mkJsonWriter(gen: TsGeneratorCtx): String = s"(v => ${mkJsonWrite(gen, "v")})"
  def mkJsonReader(gen: TsGeneratorCtx): String = s"(v => ${mkJsonRead(gen, "v")})"
}
object TsJsonType extends TsTypeCompanion[TsJsonType, TsJsonTypeTag]

trait TsBodyType extends TsType {
  def mkBodyWrite(gen: TsGeneratorCtx, valueRef: String): String
  def mkBodyRead(gen: TsGeneratorCtx, valueRef: String): String

  def mkBodyWriter(gen: TsGeneratorCtx): String = s"(v => ${mkBodyWrite(gen, "v")})"
  def mkBodyReader(gen: TsGeneratorCtx): String = s"(v => ${mkBodyRead(gen, "v")})"
}
object TsBodyType extends TsTypeCompanion[TsBodyType, TsBodyTypeTag]

trait TsResponseType extends TsType {
  def mkResponseRead(gen: TsGeneratorCtx, valueRef: String): String
  def mkResponseReader(gen: TsGeneratorCtx): String = s"(v => ${mkResponseRead(gen, "v")})"
}
object TsResponseType extends TsTypeCompanion[TsResponseType, TsResponseTypeTag]

trait TsResultType extends TsType {
  def mkSendRequest(gen: TsGeneratorCtx, httpClientRef: String, rawRequestRef: String): String
}

trait TsApiType extends TsType {
  def definition(pathPrefix: Vector[String]): TsDefinition
  def subApis: List[(Seq[String], TsApiType)]

  // override so that API proxy class can accept something else than base URL
  def constructorArgName: String = "baseUrl"
  def constructorArgType(gen: TsGeneratorCtx): String = "string"
}
object TsApiType extends TsTypeCompanion[TsApiType, TsApiTypeTag]

object TsType {
  def nullableJson(tpe: TsJsonType): TsJsonType = new TsJsonType {
    def resolve(gen: TsGeneratorCtx): String =
      s"${tpe.resolve(gen)} | null"

    def transparent: Boolean = tpe.transparent

    def mkJsonWrite(gen: TsGeneratorCtx, valueRef: String): String =
      if (transparent) valueRef
      else s"${gen.codecs}.mapNullable(${tpe.mkJsonWriter(gen)}, $valueRef)"

    def mkJsonRead(gen: TsGeneratorCtx, valueRef: String): String =
      if (transparent) s"$valueRef as ${resolve(gen)}"
      else s"${gen.codecs}.mapNullable(${tpe.mkJsonReader(gen)}, $valueRef)"
  }

  def arrayJson(tpe: TsJsonType): TsJsonType = new TsJsonType {
    def resolve(gen: TsGeneratorCtx): String =
      s"${tpe.resolve(gen)}[]"

    def transparent: Boolean = tpe.transparent

    def mkJsonWrite(gen: TsGeneratorCtx, valueRef: String): String =
      if (transparent) valueRef
      else s"$valueRef.map(${tpe.mkJsonWriter(gen)})"

    def mkJsonRead(gen: TsGeneratorCtx, valueRef: String): String =
      if (transparent) s"$valueRef as ${resolve(gen)}"
      else s"($valueRef as any[]).map(${tpe.mkJsonReader(gen)})"
  }

  // TypeScript Record type (dictionary)
  def recordJson(keyType: TsPlainType, valueType: TsJsonType): TsJsonType = new TsJsonType {
    def transparent: Boolean = valueType.transparent

    def mkJsonWrite(gen: TsGeneratorCtx, valueRef: String): String =
      if (transparent) valueRef
      else s"${gen.codecs}.mapValues($valueRef, ${valueType.mkJsonWriter(gen)})"

    def mkJsonRead(gen: TsGeneratorCtx, valueRef: String): String =
      if (transparent) s"$valueRef as ${resolve(gen)}"
      else {
        val castValueRef = s"$valueRef as Record<${keyType.dictionaryKeyType.resolve(gen)}, any>"
        s"${gen.codecs}.mapValues($castValueRef, ${valueType.mkJsonReader(gen)}, false)"
      }

    def resolve(gen: TsGeneratorCtx): String =
      s"Record<${keyType.dictionaryKeyType.resolve(gen)}, ${valueType.resolve(gen)}>"
  }

  def jsonAsBody(tpe: TsJsonType): TsBodyType = new TsBodyType {
    def resolve(gen: TsGeneratorCtx): String = tpe.resolve(gen)

    def mkBodyWrite(gen: TsGeneratorCtx, valueRef: String): String =
      s"${gen.codecs}.jsonToBody(${tpe.mkJsonWrite(gen, valueRef)})"

    def mkBodyRead(gen: TsGeneratorCtx, valueRef: String): String =
      tpe.mkJsonRead(gen, s"${gen.codecs}.bodyToJson($valueRef)")
  }

  def bodyAsResponse(tpe: TsBodyType): TsResponseType = new TsResponseType {
    def resolve(gen: TsGeneratorCtx): String = tpe.resolve(gen)

    def mkResponseRead(gen: TsGeneratorCtx, valueRef: String): String =
      tpe.mkBodyRead(gen, s"${gen.codecs}.successfulResponseToBody($valueRef)")
  }

  def resultAsPromise(tpe: TsResponseType): TsResultType = new TsResultType {
    def mkSendRequest(gen: TsGeneratorCtx, httpClientRef: String, rawRequestRef: String): String =
      s"${gen.importModule(TsModule.ClientModule)}.handleUsingFetch($httpClientRef, $rawRequestRef).then(${tpe.mkResponseReader(gen)})"

    def resolve(gen: TsGeneratorCtx): String =
      s"Promise<${tpe.resolve(gen)}>"
  }

  final val Void: TsResponseType = new TsResponseType {
    def resolve(gen: TsGeneratorCtx): String = "void"
    def mkResponseRead(gen: TsGeneratorCtx, valueRef: String): String = "void 0"
  }

  final val Undefined: TsJsonType with TsBodyType = new TsJsonType with TsBodyType {
    def resolve(gen: TsGeneratorCtx): String = "undefined"
    def transparent: Boolean = false
    def mkJsonWrite(gen: TsGeneratorCtx, valueRef: String): String = "null"
    def mkJsonRead(gen: TsGeneratorCtx, valueRef: String): String = "undefined"
    def mkBodyWrite(gen: TsGeneratorCtx, valueRef: String): String = "null"
    def mkBodyRead(gen: TsGeneratorCtx, valueRef: String): String = "undefined"
  }

  final val Null: TsJsonType = new TsJsonType {
    def resolve(gen: TsGeneratorCtx): String = "null"
    def transparent: Boolean = true
    def mkJsonWrite(gen: TsGeneratorCtx, valueRef: String): String = valueRef
    def mkJsonRead(gen: TsGeneratorCtx, valueRef: String): String = valueRef
  }

  final val Never: TsPlainType with TsJsonType = new TsPlainType with TsJsonType {
    def resolve(gen: TsGeneratorCtx): String = "never"
    def transparent: Boolean = true
    def mkPlainWrite(gen: TsGeneratorCtx, valueRef: String): String = valueRef
    def mkJsonWrite(gen: TsGeneratorCtx, valueRef: String): String = valueRef
    def mkJsonRead(gen: TsGeneratorCtx, valueRef: String): String = valueRef
  }

  final val Boolean: TsPlainType with TsJsonType = new TsPlainType with TsJsonType {
    def resolve(gen: TsGeneratorCtx): String = "boolean"
    def transparent: Boolean = true
    def mkPlainWrite(gen: TsGeneratorCtx, valueRef: String): String = s"$valueRef.toString()"
    def mkJsonWrite(gen: TsGeneratorCtx, valueRef: String): String = valueRef
    def mkJsonRead(gen: TsGeneratorCtx, valueRef: String): String = s"$valueRef as boolean"

    override def mkOptionalPlainWrite(gen: TsGeneratorCtx, valueRef: String, optional: Boolean): String =
      if (optional) s"$valueRef?.toString()" else mkPlainWrite(gen, valueRef)
  }

  final val Number: TsPlainType with TsJsonType = new TsPlainType with TsJsonType {
    def resolve(gen: TsGeneratorCtx): String = "number"
    def transparent: Boolean = true
    def mkPlainWrite(gen: TsGeneratorCtx, valueRef: String): String = s"$valueRef.toString()"
    def mkJsonWrite(gen: TsGeneratorCtx, valueRef: String): String = valueRef
    def mkJsonRead(gen: TsGeneratorCtx, valueRef: String): String = s"$valueRef as number"

    override def mkOptionalPlainWrite(gen: TsGeneratorCtx, valueRef: String, optional: Boolean): String =
      if (optional) s"$valueRef?.toString()" else mkPlainWrite(gen, valueRef)

    override def dictionaryKeyType: TsType = this
  }

  final val String: TsPlainType with TsJsonType = new TsPlainType with TsJsonType {
    def resolve(gen: TsGeneratorCtx): String = "string"
    def transparent: Boolean = true
    def mkPlainWrite(gen: TsGeneratorCtx, valueRef: String): String = valueRef
    def mkJsonWrite(gen: TsGeneratorCtx, valueRef: String): String = valueRef
    def mkJsonRead(gen: TsGeneratorCtx, valueRef: String): String = s"$valueRef as string"
  }

  final val Timestamp: TsPlainType with TsJsonType = new TsPlainType with TsJsonType {
    def resolve(gen: TsGeneratorCtx): String = "Date"
    def transparent: Boolean = false
    def mkPlainWrite(gen: TsGeneratorCtx, valueRef: String): String = s"$valueRef.toISOString()"
    def mkJsonWrite(gen: TsGeneratorCtx, valueRef: String): String = valueRef // Date has .toJSON()
    def mkJsonRead(gen: TsGeneratorCtx, valueRef: String): String = s"new Date($valueRef as string)"

    override def mkOptionalPlainWrite(gen: TsGeneratorCtx, valueRef: String, optional: Boolean): String =
      if (optional) s"$valueRef?.toISOString()" else mkPlainWrite(gen, valueRef)

    override def mkOptionalJsonWrite(gen: TsGeneratorCtx, valueRef: String, optional: Boolean): String =
      valueRef
  }

  final val Int8Array: TsJsonType with TsBodyType = new TsJsonType with TsBodyType {
    def resolve(gen: TsGeneratorCtx): String = "Int8Array"
    def transparent: Boolean = false

    def mkJsonWrite(gen: TsGeneratorCtx, valueRef: String): String =
      s"Array.prototype.slice.call($valueRef)" // https://stackoverflow.com/a/29676964/931323
    def mkJsonRead(gen: TsGeneratorCtx, valueRef: String): String =
      s"Int8Array.from($valueRef as number[])"
    def mkBodyWrite(gen: TsGeneratorCtx, valueRef: String): String =
      s"${gen.codecs}.binaryToBody($valueRef)"
    def mkBodyRead(gen: TsGeneratorCtx, valueRef: String): String =
      s"${gen.codecs}.bodyToBinary($valueRef)"
  }
}
