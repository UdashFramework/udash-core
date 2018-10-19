package io.udash.legacyrest

import com.avsystem.commons.serialization.GenCodec
import com.avsystem.commons.serialization.json.{JsonStringInput, JsonStringOutput}
import io.udash.rpc.JsonStr
import io.udash.rpc.serialization.DefaultUdashSerialization

object DefaultRESTFramework extends UdashRESTFramework with DefaultUdashSerialization {
  type Writer[T] = GenCodec[T]
  type Reader[T] = GenCodec[T]

  def read[T: GenCodec](raw: JsonStr): T = JsonStringInput.read[T](raw.json)
  def write[T: GenCodec](value: T): JsonStr = JsonStr(JsonStringOutput.write[T](value))

  implicit val bodyValuesCodec: GenCodec[Map[String, JsonStr]] = GenCodec.mapCodec

  override val bodyValuesWriter = bodyValuesCodec
  override val bodyValuesReader = bodyValuesCodec
}
