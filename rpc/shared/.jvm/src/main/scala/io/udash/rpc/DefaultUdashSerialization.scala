package io.udash.rpc

import com.avsystem.commons.serialization.{GenCodec, Input, Output}
import io.udash.rpc.serialization.{JsonInput, JsonOutput}
import io.udash.rpc.serialization.jawn.JawnFacade
import jawn.Parser

/** Provides JAWN based serialization to JSON `String`. */
trait DefaultUdashSerialization {
  def inputSerialization(value: String): Input =
    try { new JsonInput(Parser.parseFromString(value)(JawnFacade).get) }
    catch { case ex: jawn.ParseException => throw new GenCodec.ReadFailure("JAWN parse error!", ex) }

  def outputSerialization(valueConsumer: String => Unit): Output =
    new JsonOutput(valueConsumer)
}
