package io.udash.rest.tsgen

abstract class TsNewtype extends TsDefinition { this: TsType =>
  type TsT >: this.type <: TsType

  def wrapped: TsT

  def contents(gen: TsGenerator): String =
    s"export type $name = (${wrapped.resolve(gen)}) & {readonly __tag: unique symbol}\n"
}

case class TsPlainNewtype(module: TsModule, name: String, wrapped: TsPlainType)
  extends TsNewtype with TsPlainType {

  type TsT = TsPlainType

  def mkPlainWrite(gen: TsGenerator, valueRef: String): String = valueRef

  // If this type wraps a string or a number then we can use it as a dictionary key type
  // because its TS representation extends string | number
  override def dictionaryKeyType: TsType =
    if (wrapped == TsType.String || wrapped == TsType.Number) this else wrapped.dictionaryKeyType
}

case class TsJsonNewtype(module: TsModule, name: String, wrapped: TsJsonType)
  extends TsNewtype with TsJsonType {

  type TsT = TsJsonType

  def transparent: Boolean = wrapped.transparent

  def mkJsonWrite(gen: TsGenerator, valueRef: String): String =
    wrapped.mkJsonWrite(gen, valueRef)

  def mkJsonRead(gen: TsGenerator, valueRef: String): String =
    s"${wrapped.mkJsonRead(gen, valueRef)} as ${resolve(gen)}"
}

case class TsBodyNewtype(module: TsModule, name: String, wrapped: TsBodyType)
  extends TsNewtype with TsBodyType {

  type TsT = TsBodyType

  def mkBodyWrite(gen: TsGenerator, valueRef: String): String =
    wrapped.mkBodyWrite(gen, valueRef)

  def mkBodyRead(gen: TsGenerator, valueRef: String): String =
    s"${wrapped.mkBodyRead(gen, valueRef)} as ${resolve(gen)}"
}

case class TsResponseNewtype(module: TsModule, name: String, wrapped: TsResponseType)
  extends TsNewtype with TsResponseType {

  type TsT = TsResponseType

  def mkResponseRead(gen: TsGenerator, valueRef: String): String =
    s"${wrapped.mkResponseRead(gen, valueRef)} as ${resolve(gen)}"
}
