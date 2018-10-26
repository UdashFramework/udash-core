package io.udash
package rest

import com.avsystem.commons.meta.Fallback
import com.avsystem.commons.misc.ImplicitNotFound
import com.avsystem.commons.rpc.{AsRaw, AsRawReal, AsReal, InvalidRpcCall}
import com.avsystem.commons.serialization.GenCodec.ReadFailure
import com.avsystem.commons.serialization.json.{JsonStringInput, JsonStringOutput}
import com.avsystem.commons.serialization.{GenCodec, GenKeyCodec}
import com.avsystem.commons.{Future, Promise, Success, Try, _}
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
  implicit def futureToAsyncResp[T](
    implicit respAsRaw: AsRaw[RestResponse, T]
  ): AsRaw[RawRest.Async[RestResponse], Try[Future[T]]] =
    AsRaw.create { triedFuture =>
      val future = triedFuture.fold(Future.failed, identity)
      callback => future.onCompleteNow(t => callback(t.map(respAsRaw.asRaw).recoverHttpError))
    }

  implicit def futureFromAsyncResp[T](
    implicit respAsReal: AsReal[RestResponse, T]
  ): AsReal[RawRest.Async[RestResponse], Try[Future[T]]] =
    AsReal.create { async =>
      val promise = Promise[T]
      async(t => promise.complete(t.map(respAsReal.asReal)))
      Success(promise.future)
    }

  @implicitNotFound("#{forResponse}")
  implicit def futureAsRawNotFound[T](
    implicit forResponse: ImplicitNotFound[AsRaw[RestResponse, T]]
  ): ImplicitNotFound[AsRaw[RawRest.Async[RestResponse], Try[Future[T]]]] = ImplicitNotFound()

  @implicitNotFound("#{forResponse}")
  implicit def futureAsRealNotFound[T](
    implicit forResponse: ImplicitNotFound[AsReal[RestResponse, T]]
  ): ImplicitNotFound[AsReal[RawRest.Async[RestResponse], Try[Future[T]]]] = ImplicitNotFound()

  implicit def futureHttpResponseType[T]: HttpResponseType[Future[T]] =
    HttpResponseType[Future[T]]()

  @implicitNotFound("${T} is not a valid REST HTTP method result type - it must be wrapped into a Future")
  implicit def httpResponseTypeNotFound[T]: ImplicitNotFound[HttpResponseType[T]] =
    ImplicitNotFound()

  implicit def futureRestResultType[T: RestResponses]: RestResultType[Future[T]] =
    RestResultType[Future[T]](RestResponses[T].responses)

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
  protected final def handleReadFailure[T](expr: => T): T =
    try expr catch {
      case rf: ReadFailure => throw new InvalidRpcCall(rf.getMessage, rf)
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

  @implicitNotFound("Cannot serialize ${T} into JsonValue, probably because: #{forGenCodec}")
  implicit def asRawJsonNotFound[T](
    implicit forGenCodec: ImplicitNotFound[GenCodec[T]]
  ): ImplicitNotFound[AsRaw[JsonValue, T]] = ImplicitNotFound()

  @implicitNotFound("Cannot deserialize ${T} from JsonValue, probably because: #{forGenCodec}")
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