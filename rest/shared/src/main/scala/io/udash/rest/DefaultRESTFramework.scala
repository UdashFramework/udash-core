package io.udash.rest

import com.avsystem.commons.serialization.GenCodec
import io.udash.rpc.{AutoUdashRPCFramework, DefaultUdashSerialization}

object DefaultRESTFramework extends UdashRESTFramework with AutoUdashRPCFramework with DefaultUdashSerialization {
  private val bodyValuesCodec = GenCodec.Auto(GenCodec.create[Map[String, DefaultRESTFramework.RawValue]](
    in => {
      val data = Map.newBuilder[String, DefaultRESTFramework.RawValue]
      val obj = in.readObject()
      while (obj.hasNext) {
        val f = obj.nextField()
        data += (f.fieldName -> stringToRaw(f.readString()))
      }
      data.result()
    },
    (out, e) => {
      val obj = out.writeObject()
      for ((key, value) <- e) {
        obj.writeField(key).writeString(rawToString(value))
      }
      obj.finish()
    }
  ))

  override def bodyValuesWriter: DefaultRESTFramework.Writer[Map[String, DefaultRESTFramework.RawValue]] =
    bodyValuesCodec

  override def bodyValuesReader: DefaultRESTFramework.Reader[Map[String, DefaultRESTFramework.RawValue]] =
    bodyValuesCodec
}
