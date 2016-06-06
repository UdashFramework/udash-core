package io.udash.rpc

import com.avsystem.commons.serialization.{Input, Output}
import io.udash.rpc.serialization.{JsonInput, JsonOutput}
import io.udash.rpc.serialization.jawn.JawnFacade
import jawn.Parser

/** Provides JAWN based serialization to JSON `String`. */
trait DefaultUdashSerialization {
  def inputSerialization(value: String): Input =
    new JsonInput(Parser.parseFromString(value)(JawnFacade).get)

  def outputSerialization(valueConsumer: String => Unit): Output =
    new JsonOutput(valueConsumer)
}
