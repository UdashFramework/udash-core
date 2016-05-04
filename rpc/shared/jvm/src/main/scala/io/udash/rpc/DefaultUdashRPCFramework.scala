package io.udash.rpc

import com.avsystem.commons.serialization.{GenCodec, Input, Output}
import io.udash.rpc.serialization.{JsonInput, JsonOutput}
import io.udash.rpc.serialization.jawn.JawnFacade
import jawn.Parser

import scala.language.postfixOps

object DefaultUdashRPCFramework extends AutoUdashRPCFramework {
  def inputSerialization(value: String): Input =
    new JsonInput(Parser.parseFromString(value)(JawnFacade).get)

  def outputSerialization(valueConsumer: String => Unit): Output =
    new JsonOutput(valueConsumer)
}