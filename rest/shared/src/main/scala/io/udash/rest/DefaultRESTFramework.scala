package io.udash.rest

import com.avsystem.commons.serialization.GenCodec
import io.udash.rpc.GenCodecSerializationFramework
import io.udash.rpc.serialization.{DefaultUdashSerialization, JsonStr}

object DefaultRESTFramework extends UdashRESTFramework with DefaultUdashSerialization with GenCodecSerializationFramework {
  implicit val bodyValuesCodec: GenCodec[Map[String, JsonStr]] = GenCodec.mapCodec

  override val bodyValuesWriter = bodyValuesCodec
  override val bodyValuesReader = bodyValuesCodec
}
