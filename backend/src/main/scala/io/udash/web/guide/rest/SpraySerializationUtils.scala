package io.udash.web.guide.rest

import com.avsystem.commons.serialization.GenCodec
import io.udash.rpc.DefaultUdashSerialization
import spray.http.{HttpEntity, MediaTypes}
import spray.httpx.marshalling.{Marshaller, ToResponseMarshaller}
import spray.httpx.unmarshalling.{Unmarshaller, _}

trait SpraySerializationUtils extends DefaultUdashSerialization {
  implicit def optionMarshaller[T](implicit codec: GenCodec[T]) =
    ToResponseMarshaller.fromMarshaller()(gencodecMarshaller[Option[T]](GenCodec.optionCodec(codec)))

  implicit def gencodecMarshaller[T](implicit codec: GenCodec[T]): Marshaller[T] =
    Marshaller.of[T](MediaTypes.`application/json`)(
      (value, contentType, ctx) => {
        var string: String = null
        val output = outputSerialization((serialized) => string = serialized)
        codec.write(output, value)
        ctx.marshalTo(HttpEntity(contentType, string))
      }
    )

  implicit def gencodecUnmarshaller[T](implicit codec: GenCodec[T]): Unmarshaller[T] =
    Unmarshaller[T](MediaTypes.`application/json`) {
      case HttpEntity.NonEmpty(contentType, data) =>
        val input = inputSerialization(data.asString)
        codec.read(input)
    }
}
