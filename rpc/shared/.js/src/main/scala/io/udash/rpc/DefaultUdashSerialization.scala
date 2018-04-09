package io.udash.rpc

import com.avsystem.commons.serialization.json.{JsonReader, JsonStringInput, JsonStringOutput}
import com.avsystem.commons.serialization.{GenCodec, Input}

/** Provides native browser serialization to JSON `String`. */
trait DefaultUdashSerialization {
  def inputSerialization(value: String): Input = new JsonStringInput(new JsonReader(value))
  def write[T: GenCodec](value: T): String = JsonStringOutput.write(value)
}
