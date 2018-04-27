package io.udash.rpc

import com.avsystem.commons.serialization.GenCodec
import com.avsystem.commons.serialization.json.{JsonStringInput, JsonStringOutput}

trait DefaultUdashSerialization {
  protected val rawValueCodec: GenCodec[String] = GenCodec.createNonNull(
    {
      case jsi: JsonStringInput => jsi.readRawJson()
      case in => in.readString()
    },
    {
      case (jso: JsonStringOutput, json) => jso.writeRawJson(json)
      case (out, v) => out.writeString(v)
    }
  )

  def read[T: GenCodec](value: String): T = JsonStringInput.read[T](value)
  def write[T: GenCodec](value: T): String = JsonStringOutput.write(value)
}
