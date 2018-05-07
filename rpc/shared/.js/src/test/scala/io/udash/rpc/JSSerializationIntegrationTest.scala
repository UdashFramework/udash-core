package io.udash.rpc

import com.avsystem.commons.serialization.GenCodec
import com.avsystem.commons.serialization.json.{JsonStringInput, JsonStringOutput}
import io.udash.rpc.serialization.{DefaultUdashSerialization, JsonStr, NativeJsonInput, NativeJsonOutput}

class JSSerializationIntegrationTest extends SerializationIntegrationTestBase {
  override val repeats = 3
  object NativeJsonUdashRPCFramework extends ClientUdashRPCFramework {
    val rawValueCodec: GenCodec[RawValue] = GenCodec.createNonNull(
      {
        case jsi: JsonStringInput => JsonStr(jsi.readRawJson())
        case in => JsonStr(in.readString())
      },
      {
        case (jso: JsonStringOutput, v) => jso.writeRawJson(v.json)
        case (out, v) => out.writeString(v.json)
      }
    )

    def read[T: GenCodec](value: RawValue): T = NativeJsonInput.read[T](value.json)
    def write[T: GenCodec](value: T): RawValue = JsonStr(NativeJsonOutput.write[T](value))
  }
  object JsonStringUdashRPCFramework extends ClientUdashRPCFramework with DefaultUdashSerialization

  "DefaultUdashRPCFramework -> DefaultUdashRPCFramework default serialization" should tests(DefaultClientUdashRPCFramework, DefaultClientUdashRPCFramework)
  "NativeJsonUdashRPCFramework -> NativeJsonUdashRPCFramework default serialization" should tests(NativeJsonUdashRPCFramework, NativeJsonUdashRPCFramework)
  "JsonStringUdashRPCFramework -> JsonStringUdashRPCFramework default serialization" should tests(JsonStringUdashRPCFramework, JsonStringUdashRPCFramework)
}
