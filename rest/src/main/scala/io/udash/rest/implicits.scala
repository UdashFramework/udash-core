package io.udash
package rest

import com.avsystem.commons.*
import com.avsystem.commons.meta.Fallback
import com.avsystem.commons.misc.ImplicitNotFound
import com.avsystem.commons.rpc.{AsRaw, AsRawReal, AsReal, InvalidRpcCall}
import com.avsystem.commons.serialization.json.{JsonStringInput, JsonStringOutput}
import com.avsystem.commons.serialization.{GenCodec, GenKeyCodec}
import io.udash.rest.openapi.{OpenApiMetadata, RestSchema}
import io.udash.rest.raw.*
import io.udash.rest.raw.RawRest.FromTask
import monix.eval.Task
import monix.execution.Scheduler

import scala.annotation.implicitNotFound

trait FloatingPointRestImplicits {
  implicit final val floatPlainValueAsRealRaw: AsRawReal[PlainValue, Float] =
    AsRawReal.create(v => PlainValue(v.toString), _.value.toFloat)
  implicit final val doublePlainValueAsRealRaw: AsRawReal[PlainValue, Double] =
    AsRawReal.create(v => PlainValue(v.toString), _.value.toDouble)
}
object FloatingPointRestImplicits extends FloatingPointRestImplicits

trait FutureRestImplicits {
  /**
   * This `Scheduler` is used only on client-side, effectively only to translate between [[RestRequest]]/[[RestResponse]]
   * and native representations of requests and responses for the HTTP client being used (e.g. Jetty).
   * Despite that, it is recommended to override this method and provide a customized `Scheduler`
   * (possibly shared with other parts of your application) in order to avoid creating too many thread pools.
   */
  implicit def clientScheduler: Scheduler = Scheduler.global

  implicit def futureFromTask: FromTask[Future] =
    new FromTask[Future] {
      override def fromTask[A](task: Task[A]): Future[A] = task.runToFuture
    }

  @implicitNotFound("${T} is not a valid result type of HTTP REST method - it must be a Future")
  implicit def httpResponseTypeNotFound[T]: ImplicitNotFound[HttpResponseType[T]] =
    ImplicitNotFound()
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
  implicit def plainValueFallbackAsRealRaw[T: GenKeyCodec]: Fallback[AsRawReal[PlainValue, T]] =
    Fallback(AsRawReal.create(
      v => PlainValue(GenKeyCodec.write[T](v)),
      v => handleReadFailure(GenKeyCodec.read[T](v.value))
    ))

  implicit def jsonValueDefaultAsRealRaw[T: GenCodec]: Fallback[AsRawReal[JsonValue, T]] =
    Fallback(AsRawReal.create(
      v => JsonValue(JsonStringOutput.write[T](v)),
      v => handleReadFailure(JsonStringInput.read[T](v.value))
    ))

  @implicitNotFound("Cannot serialize ${T} into PlainValue, most likely because:\n#{forKeyCodec}")
  implicit def asRawPlainNotFound[T](
    implicit forKeyCodec: ImplicitNotFound[GenKeyCodec[T]]
  ): ImplicitNotFound[AsRaw[PlainValue, T]] = ImplicitNotFound()

  @implicitNotFound("Cannot deserialize ${T} from PlainValue, most likely because:\n#{forKeyCodec}")
  implicit def asRealPlainNotFound[T](
    implicit forKeyCodec: ImplicitNotFound[GenKeyCodec[T]]
  ): ImplicitNotFound[AsReal[PlainValue, T]] = ImplicitNotFound()

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