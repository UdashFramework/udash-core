package io.udash.rpc

import com.avsystem.commons.serialization.{GenCodec, Input, Output}
import io.udash.rpc.serialization.{NativeJsonInput, NativeJsonOutput}

import scala.scalajs.js.{JSON, JavaScriptException}

/** Provides native browser serialization to JSON `String`. */
trait DefaultUdashSerialization {
  def inputSerialization(value: String): Input =
    try { new NativeJsonInput(JSON.parse(value)) }
    catch { case ex: JavaScriptException => throw new GenCodec.ReadFailure("JSON parse error!", ex) }


  def outputSerialization(valueConsumer: String => Unit): Output =
    new NativeJsonOutput(value => valueConsumer(JSON.stringify(value)))
}
