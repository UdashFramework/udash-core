package io.udash
package rest

import com.avsystem.commons._
import com.avsystem.commons.meta.Fallback
import com.avsystem.commons.misc.ImplicitNotFound
import com.avsystem.commons.rpc.{AsRaw, AsRawReal, AsReal, InvalidRpcCall}
import com.avsystem.commons.serialization.json.{JsonStringInput, JsonStringOutput}
import com.avsystem.commons.serialization.{GenCodec, GenKeyCodec}
import io.udash.rest.openapi.{OpenApiMetadata, RestResponses, RestResultType, RestSchema}
import io.udash.rest.raw._

import scala.annotation.implicitNotFound

trait FloatingPointRestImplicits {
  implicit final val floatPathValueAsRealRaw: AsRawReal[PathValue, Float] =
    AsRawReal.create(v => PathValue(v.toString), _.value.toFloat)
  implicit final val floatHeaderValueAsRealRaw: AsRawReal[HeaderValue, Float] =
    AsRawReal.create(v => HeaderValue(v.toString), _.value.toFloat)
  implicit final val floatQueryValueAsRealRaw: AsRawReal[QueryValue, Float] =
    AsRawReal.create(v => QueryValue(v.toString), _.value.toFloat)

  implicit final val doublePathValueAsRealRaw: AsRawReal[PathValue, Double] =
    AsRawReal.create(v => PathValue(v.toString), _.value.toDouble)
  implicit final val doubleHeaderValueAsRealRaw: AsRawReal[HeaderValue, Double] =
    AsRawReal.create(v => HeaderValue(v.toString), _.value.toDouble)
  implicit final val doubleQueryValueAsRealRaw: AsRawReal[QueryValue, Double] =
    AsRawReal.create(v => QueryValue(v.toString), _.value.toDouble)
}
object FloatingPointRestImplicits extends FloatingPointRestImplicits

trait FutureRestImplicits {
  implicit def futureToAsync: RawRest.ToAsync[Future] =
    new RawRest.ToAsync[Future] {
      def toAsync[A](fa: Future[A]): RawRest.Async[A] =
        fa.onCompleteNow
    }

  implicit def futureFromAsync: RawRest.FromAsync[Future] =
    new RawRest.FromAsync[Future] {
      def fromAsync[A](async: RawRest.Async[A]): Future[A] =
        Promise[A].setup(p => async(p.complete)).future
    }

  implicit def futureHttpResponseType[T]: HttpResponseType[Future[T]] =
    HttpResponseType[Future[T]]()

  implicit def futureRestResultType[T: RestResponses]: RestResultType[Future[T]] =
    RestResultType[Future[T]](RestResponses[T].responses)

  @implicitNotFound("${T} is not a valid REST HTTP method result type - it must be a Future")
  implicit def httpResponseTypeNotFound[T]: ImplicitNotFound[HttpResponseType[T]] =
    ImplicitNotFound()

  @implicitNotFound("#{forRestResponses}")
  implicit def futureRestResultTypeNotFound[T](
    implicit forRestResponses: ImplicitNotFound[RestResponses[T]]
  ): ImplicitNotFound[RestResultType[Future[T]]] = ImplicitNotFound()
}
object FutureRestImplicits extends FutureRestImplicits

/**
  * Defines `GenCodec` and `GenKeyCodec` based serialization for REST API traits.
  */
trait GenCodecRestImplicits extends FloatingPointRestImplicits {
  // read failure handling is now baked into macro-generated RPC `AsRaw` implementations but this
  // method is left for backwards compatibility - for instances materialized with previous version of macro
  protected final def handleReadFailure[T](expr: => T): T =
    try expr catch {
      case e: InvalidRpcCall => throw e
      case NonFatal(cause) => throw new InvalidRpcCall(cause.getMessage, cause)
    }

  // Implicits wrapped into `Fallback` so that they don't get higher priority just because they're imported
  // This way concrete classes may override these implicits with implicits in their companion objects
  implicit def pathValueFallbackAsRealRaw[T: GenKeyCodec]: Fallback[AsRawReal[PathValue, T]] =
    Fallback(AsRawReal.create(
      v => PathValue(GenKeyCodec.write[T](v)),
      v => handleReadFailure(GenKeyCodec.read[T](v.value))
    ))
  implicit def headerValueDefaultAsRealRaw[T: GenKeyCodec]: Fallback[AsRawReal[HeaderValue, T]] =
    Fallback(AsRawReal.create(
      v => HeaderValue(GenKeyCodec.write[T](v)),
      v => handleReadFailure(GenKeyCodec.read[T](v.value))
    ))
  implicit def queryValueDefaultAsRealRaw[T: GenKeyCodec]: Fallback[AsRawReal[QueryValue, T]] =
    Fallback(AsRawReal.create(
      v => QueryValue(GenKeyCodec.write[T](v)),
      v => handleReadFailure(GenKeyCodec.read[T](v.value))
    ))

  implicit def jsonValueDefaultAsRealRaw[T: GenCodec]: Fallback[AsRawReal[JsonValue, T]] =
    Fallback(AsRawReal.create(
      v => JsonValue(JsonStringOutput.write[T](v)),
      v => handleReadFailure(JsonStringInput.read[T](v.value))
    ))

  @implicitNotFound("Cannot serialize ${T} into PathValue, most likely because:\n#{forKeyCodec}")
  implicit def asRawPathNotFound[T](
    implicit forKeyCodec: ImplicitNotFound[GenKeyCodec[T]]
  ): ImplicitNotFound[AsRaw[PathValue, T]] = ImplicitNotFound()

  @implicitNotFound("Cannot deserialize ${T} from PathValue, most likely because:\n#{forKeyCodec}")
  implicit def asRealPathNotFound[T](
    implicit forKeyCodec: ImplicitNotFound[GenKeyCodec[T]]
  ): ImplicitNotFound[AsReal[PathValue, T]] = ImplicitNotFound()

  @implicitNotFound("Cannot serialize ${T} into HeaderValue, most likely because:\n#{forKeyCodec}")
  implicit def asRawHeaderNotFound[T](
    implicit forKeyCodec: ImplicitNotFound[GenKeyCodec[T]]
  ): ImplicitNotFound[AsRaw[HeaderValue, T]] = ImplicitNotFound()

  @implicitNotFound("Cannot deserialize ${T} from HeaderValue, most likely because:\n#{forKeyCodec}")
  implicit def asRealHeaderNotFound[T](
    implicit forKeyCodec: ImplicitNotFound[GenKeyCodec[T]]
  ): ImplicitNotFound[AsReal[HeaderValue, T]] = ImplicitNotFound()

  @implicitNotFound("Cannot serialize ${T} into QueryValue, most likely because:\n#{forKeyCodec}")
  implicit def asRawQueryNotFound[T](
    implicit forKeyCodec: ImplicitNotFound[GenKeyCodec[T]]
  ): ImplicitNotFound[AsRaw[QueryValue, T]] = ImplicitNotFound()

  @implicitNotFound("Cannot deserialize ${T} from QueryValue, most likely because:\n#{forKeyCodec}")
  implicit def asRealQueryNotFound[T](
    implicit forKeyCodec: ImplicitNotFound[GenKeyCodec[T]]
  ): ImplicitNotFound[AsReal[QueryValue, T]] = ImplicitNotFound()

  @implicitNotFound("Cannot serialize ${T} into JsonValue, because:\n#{forGenCodec}")
  implicit def asRawJsonNotFound[T](
    implicit forGenCodec: ImplicitNotFound[GenCodec[T]]
  ): ImplicitNotFound[AsRaw[JsonValue, T]] = ImplicitNotFound()

  @implicitNotFound("Cannot deserialize ${T} from JsonValue, because:\n#{forGenCodec}")
  implicit def asRealJsonNotFound[T](
    implicit forGenCodec: ImplicitNotFound[GenCodec[T]]
  ): ImplicitNotFound[AsReal[JsonValue, T]] = ImplicitNotFound()
}
object GenCodecRestImplicits extends GenCodecRestImplicits

trait DefaultRestImplicits extends FutureRestImplicits with GenCodecRestImplicits {
  @implicitNotFound("${T} is not a valid server REST API trait, does its companion extend " +
    "DefaultRestApiCompanion, DefaultRestServerApiCompanion or other companion base?")
  implicit def rawRestAsRawNotFound[T]: ImplicitNotFound[AsRaw[RawRest, T]] = ImplicitNotFound()

  @implicitNotFound("${T} is not a valid REST API trait, does it have a companion object which extends " +
    "DefaultRestApiCompanion or other companion base?")
  implicit def restMetadataNotFound[T]: ImplicitNotFound[RestMetadata[T]] = ImplicitNotFound()

  @implicitNotFound("${T} is not a valid REST API trait for OpenAPI generation, does it have a companion object " +
    "which extends DefaultRestApiCompanion or other companion base?")
  implicit def openapiMetadataNotFound[T]: ImplicitNotFound[OpenApiMetadata[T]] = ImplicitNotFound()

  @implicitNotFound("${T} is not a valid client REST API trait, does it have a companion object that extends " +
    "DefaultRestApiCompanion, DefaultRestClientApiCompanion or other companion base?")
  implicit def rawRestAsRealNotFound[T]: ImplicitNotFound[AsReal[RawRest, T]] = ImplicitNotFound()

  @implicitNotFound("RestSchema for ${T} not found. To provide it for case classes and sealed hierarchies " +
    "use RestDataCompanion (which also provides GenCodec)")
  implicit def schemaNotFound[T]: ImplicitNotFound[RestSchema[T]] = ImplicitNotFound()
}
object DefaultRestImplicits extends DefaultRestImplicits