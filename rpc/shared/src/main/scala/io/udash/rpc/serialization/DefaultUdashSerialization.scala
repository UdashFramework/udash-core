package io.udash.rpc.serialization

import com.avsystem.commons.rpc.RPCFramework
import com.avsystem.commons.serialization.GenCodec
import com.avsystem.commons.serialization.json.{JsonStringInput, JsonStringOutput}

trait DefaultUdashSerialization { this: RPCFramework =>
  override type RawValue = JsonStr

  implicit val rawValueCodec: GenCodec[JsonStr] = GenCodec.createNonNull(
    {
      case jsi: JsonStringInput => JsonStr(jsi.readRawJson())
      case in => JsonStr(in.readString())
    },
    {
      case (jso: JsonStringOutput, v) => jso.writeRawJson(v.json)
      case (out, v) => out.writeString(v.json)
    }
  )

  def read[T: GenCodec](value: RawValue): T = JsonStringInput.read[T](value.json)
  def write[T: GenCodec](value: T): RawValue = JsonStr(JsonStringOutput.write(value))
}
