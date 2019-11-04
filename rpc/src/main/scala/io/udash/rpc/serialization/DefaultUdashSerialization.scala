package io.udash.rpc.serialization

import com.avsystem.commons.meta.Fallback
import com.avsystem.commons.misc.ImplicitNotFound
import com.avsystem.commons.rpc.{AsRaw, AsReal}
import com.avsystem.commons.serialization.GenCodec
import com.avsystem.commons.serialization.json.{JsonStringInput, JsonStringOutput}
import io.udash.rpc.JsonStr

import scala.annotation.implicitNotFound

trait DefaultUdashSerialization {
  implicit def genCodecBasedAsReal[T: GenCodec]: Fallback[AsReal[JsonStr, T]] =
    Fallback(jsonStr => JsonStringInput.read[T](jsonStr.json))

  implicit def genCodecBasedAsRaw[T: GenCodec]: Fallback[AsRaw[JsonStr, T]] =
    Fallback(value => JsonStr(JsonStringOutput.write[T](value)))

  @implicitNotFound("#{forGenCodec}")
  implicit def asRealNotFound[T](
    implicit forGenCodec: ImplicitNotFound[GenCodec[T]]
  ): ImplicitNotFound[AsReal[JsonStr, T]] = ImplicitNotFound()

  @implicitNotFound("#{forGenCodec}")
  implicit def asRawNotFound[T](
    implicit forGenCodec: ImplicitNotFound[GenCodec[T]]
  ): ImplicitNotFound[AsRaw[JsonStr, T]] = ImplicitNotFound()
}
object DefaultUdashSerialization extends DefaultUdashSerialization
