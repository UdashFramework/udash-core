package io.udash.rpc

import com.avsystem.commons.serialization._

import scala.language.postfixOps

trait AutoUdashRPCFramework extends UdashRPCFramework {
  type RawValue = String

  val RawValueCodec = implicitly[GenCodec[String]]

  def stringToRaw(string: String): RawValue = string
  def rawToString(raw: RawValue): String = raw
}

