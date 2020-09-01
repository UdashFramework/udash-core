package io.udash.rest.tsgen

import com.avsystem.commons.Opt
import com.avsystem.commons.serialization.json.JsonStringOutput

//TODO: use actual TypeScript enum?
case class TsEnum(module: TsModule, name: String, values: Seq[String])
  extends TsJsonType with TsPlainType with TsDefinition {

  def contents(gen: TsGenerator): String =
    s"export type $name = ${values.iterator.map(JsonStringOutput.write(_)).mkString(" | ")}\n"

  def jsonCodecRef: Opt[TsReference] = Opt.Empty
  def plainCodecRef: Opt[TsReference] = Opt.Empty
}
