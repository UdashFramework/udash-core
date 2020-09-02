package io.udash.rest.tsgen

import com.avsystem.commons.serialization.json.JsonStringOutput

//TODO: use actual TypeScript enum?
case class TsEnum(module: TsModule, name: String, values: Seq[String])
  extends TsJsonType with TsPlainType with TsDefinition {

  def contents(gen: TsGenerator): String =
    s"export type $name = ${values.iterator.map(JsonStringOutput.write(_)).mkString(" | ")}\n"

  def transparentPlain: Boolean = true
  def transparent: Boolean = true

  def mkPlainWrite(gen: TsGenerator, valueRef: String): String = valueRef
  def mkJsonWrite(gen: TsGenerator, valueRef: String): String = valueRef
  def mkJsonRead(gen: TsGenerator, valueRef: String): String = s"$valueRef as ${resolve(gen)}"
}
