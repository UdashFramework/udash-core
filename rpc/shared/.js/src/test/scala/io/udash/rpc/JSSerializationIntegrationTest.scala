package io.udash.rpc

import com.avsystem.commons.serialization.GenCodec
import com.avsystem.commons.serialization.json.{JsonStringInput, JsonStringOutput}
import io.udash.rpc.serialization.{NativeJsonInput, NativeJsonOutput}

class JSSerializationIntegrationTest extends SerializationIntegrationTestBase {
  override val repeats = 3
  object NativeJsonUdashRPCFramework extends ClientUdashRPCFramework {
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

    def read[T: GenCodec](value: String): T = NativeJsonInput.read[T](value)
    def write[T: GenCodec](value: T): String = NativeJsonOutput.write[T](value)
  }
  object JsonStringUdashRPCFramework extends ClientUdashRPCFramework with DefaultUdashSerialization

  "DefaultUdashRPCFramework -> DefaultUdashRPCFramework default serialization" should tests(DefaultClientUdashRPCFramework, DefaultClientUdashRPCFramework)
  "NativeJsonUdashRPCFramework -> NativeJsonUdashRPCFramework default serialization" should tests(NativeJsonUdashRPCFramework, NativeJsonUdashRPCFramework)
  "JsonStringUdashRPCFramework -> JsonStringUdashRPCFramework default serialization" should tests(JsonStringUdashRPCFramework, JsonStringUdashRPCFramework)
}
