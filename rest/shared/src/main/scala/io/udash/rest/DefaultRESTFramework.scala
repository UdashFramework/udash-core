package io.udash.rest

import io.udash.rpc.{DefaultUdashSerialization, GenCodecSerializationFramework}

object DefaultRESTFramework extends UdashRESTFramework with DefaultUdashSerialization with GenCodecSerializationFramework {
  override val bodyValuesWriter: DefaultRESTFramework.Writer[Map[String, String]] = implicitly
  override val bodyValuesReader: DefaultRESTFramework.Reader[Map[String, String]] = implicitly
}
