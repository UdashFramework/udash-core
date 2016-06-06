package io.udash.rpc

import com.avsystem.commons.serialization.{Input, Output}
import io.udash.rpc.serialization.{NativeJsonInput, NativeJsonOutput}

import scala.scalajs.js.JSON

/** Provides native browser serialization to JSON `String`. */
trait DefaultUdashSerialization {
  def inputSerialization(value: String): Input =
    new NativeJsonInput(JSON.parse(value))

  def outputSerialization(valueConsumer: String => Unit): Output =
    new NativeJsonOutput(value => valueConsumer(JSON.stringify(value)))
}
