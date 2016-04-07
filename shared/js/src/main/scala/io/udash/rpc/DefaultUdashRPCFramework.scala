package io.udash.rpc

import com.avsystem.commons.serialization._
import io.udash.rpc.serialization.{NativeJsonInput, NativeJsonOutput}

import scala.language.postfixOps
import scala.scalajs.js.JSON

object DefaultUdashRPCFramework extends UdashRPCFramework {
  type RawValue = String

  def inputSerialization(value: String): Input =
    new NativeJsonInput(JSON.parse(value))

  def outputSerialization(valueConsumer: String => Unit): Output =
    new NativeJsonOutput(value => valueConsumer(JSON.stringify(value)))

  implicit val RawValueCodec: GenCodec[RawValue] =
    GenCodec.StringCodec

  def stringToRaw(string: String): RawValue = string
  def rawToString(raw: RawValue): String = raw
}

