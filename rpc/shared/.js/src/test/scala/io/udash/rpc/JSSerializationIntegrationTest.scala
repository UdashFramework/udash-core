package io.udash.rpc

import com.avsystem.commons.serialization.json.{JsonReader, JsonStringInput, JsonStringOutput}
import com.avsystem.commons.serialization.{GenCodec, Input}
import io.udash.rpc.serialization.{NativeJsonInput, NativeJsonOutput}

import scala.scalajs.js.JSON


class JSSerializationIntegrationTest extends SerializationIntegrationTestBase {
  override val repeats = 3
  object NativeJsonUdashRPCFramework extends ClientUdashRPCFramework {
    def inputSerialization(value: String): Input = new NativeJsonInput(JSON.parse(value))
    def write[T: GenCodec](value: T): String = NativeJsonOutput.write(value)
  }
  object JsonStringUdashRPCFramework extends ClientUdashRPCFramework {
    def inputSerialization(value: String): Input = new JsonStringInput(new JsonReader(value))
    def write[T: GenCodec](value: T): String = JsonStringOutput.write(value)
  }
  "DefaultUdashRPCFramework -> DefaultUdashRPCFramework default serialization" should tests(DefaultClientUdashRPCFramework, DefaultClientUdashRPCFramework)
  "NativeJsonUdashRPCFramework -> NativeJsonUdashRPCFramework default serialization" should tests(NativeJsonUdashRPCFramework, NativeJsonUdashRPCFramework)
  "JsonStringUdashRPCFramework -> JsonStringUdashRPCFramework default serialization" should tests(JsonStringUdashRPCFramework, JsonStringUdashRPCFramework)
}