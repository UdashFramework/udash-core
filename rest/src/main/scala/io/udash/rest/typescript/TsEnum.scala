package io.udash.rest.typescript

case class TsEnum(module: TsModule, name: String, values: Seq[String])
  extends TsJsonType with TsPlainType with TsDefinition {

  def contents(gen: TsGeneratorCtx): String =
    s"""export enum $name {
       |${values.iterator.map(v => s"    $v = ${quote(v)}").mkString("", ",\n", ",")}
       |}
       |""".stripMargin

  def transparentPlain: Boolean = true
  def transparent: Boolean = true

  def mkPlainWrite(gen: TsGeneratorCtx, valueRef: String): String = valueRef
  def mkJsonWrite(gen: TsGeneratorCtx, valueRef: String): String = valueRef
  def mkJsonRead(gen: TsGeneratorCtx, valueRef: String): String = s"$valueRef as ${resolve(gen)}"

  // since TS representation of enum extends string, we can use it as Dictionary key type
  override def dictionaryKeyType: TsType = this
}
