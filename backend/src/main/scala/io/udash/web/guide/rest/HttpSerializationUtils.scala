package io.udash.web.guide.rest

import akka.http.scaladsl.marshalling.Marshaller
import akka.http.scaladsl.model.HttpEntity.Strict
import akka.http.scaladsl.model.{HttpEntity, MediaTypes}
import akka.http.scaladsl.unmarshalling.Unmarshaller
import com.avsystem.commons.serialization.GenCodec
import io.udash.rpc.DefaultUdashSerialization

trait HttpSerializationUtils extends DefaultUdashSerialization {
//  implicit def optionMarshaller[T](implicit codec: GenCodec[T]) =
//    ToResponseMarshaller.fromMarshaller()(gencodecMarshaller[Option[T]](GenCodec.optionCodec(codec)))

  implicit def gencodecMarshaller[T](implicit codec: GenCodec[T]): Marshaller[T, Strict] =
    Marshaller.withFixedContentType(MediaTypes.`application/json`)(
      (obj: T) => {
        var string: String = null
        val output = outputSerialization((serialized) => string = serialized)
        codec.write(output, obj)
        HttpEntity(MediaTypes.`application/json`, string)
      }
    )

  implicit def gencodecUnmarshaller[T](implicit codec: GenCodec[T]): Unmarshaller[HttpEntity, T] =
    Unmarshaller
      .stringUnmarshaller
      .forContentTypes(MediaTypes.`application/json`)
      .map(data => codec.read(inputSerialization(data)))
}
