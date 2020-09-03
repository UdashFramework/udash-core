package io.udash.rest.tsgen

case class TsEnum(module: TsModule, name: String, values: Seq[String])
  extends TsJsonType with TsPlainType with TsDefinition {

  def contents(gen: TsGenerator): String =
    s"""export enum $name {
       |${values.iterator.map(v => s"    $v = ${quote(v)}").mkString("", ",\n", ",")}
       |}
       |""".stripMargin

  def transparentPlain: Boolean = true
  def transparent: Boolean = true

  def mkPlainWrite(gen: TsGenerator, valueRef: String): String = valueRef
  def mkJsonWrite(gen: TsGenerator, valueRef: String): String = valueRef
  def mkJsonRead(gen: TsGenerator, valueRef: String): String = s"$valueRef as ${resolve(gen)}"

  // since TS representation of enum extends string, we can use it as Dictionary key type
  override def dictionaryKeyType: TsType = this
}
