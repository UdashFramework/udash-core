package io.udash
package rest

import com.avsystem.commons.meta.MacroInstances
import com.avsystem.commons.meta.MacroInstances.materializeWith
import io.udash.rest.openapi.OpenApiMetadata
import io.udash.rest.raw.RawRest.AsyncEffect
import io.udash.rest.raw.{RawRest, RestMetadata}

trait ClientInstances[Real] {
  def asReal: RawRest.AsRealRpc[Real]
  def metadata: RestMetadata[Real]
}
trait ServerInstances[Real] {
  def asRaw: RawRest.AsRawRpc[Real]
  def metadata: RestMetadata[Real]
}
trait FullInstances[Real] extends ServerInstances[Real] with ClientInstances[Real]

trait OpenApiInstances[Real] {
  def openapiMetadata: OpenApiMetadata[Real]
}
trait OpenApiServerInstances[Real] extends ServerInstances[Real] with OpenApiInstances[Real]
trait OpenApiFullInstances[Real] extends FullInstances[Real] with OpenApiInstances[Real]

trait ServerImplInstances[Real] {
  @materializeWith(RawRest, "materializeApiAsRaw")
  def asRaw: RawRest.AsRawRpc[Real]
  @materializeWith(RestMetadata, "materializeForImpl")
  def metadata: RestMetadata[Real]
}
trait OpenApiImplInstances[Real] {
  @materializeWith(OpenApiMetadata, "materializeForImpl")
  def openapiMetadata: OpenApiMetadata[Real]
}
trait OpenApiServerImplInstances[Real] extends ServerImplInstances[Real] with OpenApiImplInstances[Real]

/** @see [[io.udash.rest.RestApiCompanion RestApiCompanion]] */
abstract class RestClientApiCompanion[Implicits, Real](protected val implicits: Implicits)(
  implicit inst: MacroInstances[Implicits, ClientInstances[Real]]
) {
  implicit final lazy val restMetadata: RestMetadata[Real] = inst(implicits, this).metadata
  implicit final lazy val restAsReal: RawRest.AsRealRpc[Real] = inst(implicits, this).asReal

  final def fromHandleRequest(handleRequest: RawRest.HandleRequest): Real =
    RawRest.fromHandleRequest(handleRequest)
}

/** @see [[io.udash.rest.RestApiCompanion RestApiCompanion]] */
abstract class RestServerApiCompanion[Implicits, Real](protected val implicits: Implicits)(
  implicit inst: MacroInstances[Implicits, ServerInstances[Real]]
) {
  implicit final lazy val restMetadata: RestMetadata[Real] = inst(implicits, this).metadata
  implicit final lazy val restAsRaw: RawRest.AsRawRpc[Real] = inst(implicits, this).asRaw

  final def asHandleRequest(real: Real): RawRest.HandleRequest =
    RawRest.asHandleRequest(real)
}

/** @see [[io.udash.rest.RestApiCompanion RestApiCompanion]] */
abstract class RestServerOpenApiCompanion[Implicits, Real](protected val implicits: Implicits)(
  implicit inst: MacroInstances[Implicits, OpenApiServerInstances[Real]]
) {
  implicit final lazy val restMetadata: RestMetadata[Real] = inst(implicits, this).metadata
  implicit final lazy val restAsRaw: RawRest.AsRawRpc[Real] = inst(implicits, this).asRaw
  implicit final lazy val openapiMetadata: OpenApiMetadata[Real] = inst(implicits, this).openapiMetadata

  final def asHandleRequest(real: Real): RawRest.HandleRequest =
    RawRest.asHandleRequest(real)
}

/**
  * Base class for REST trait companions. Reduces boilerplate needed in order to define appropriate instances
  * of `AsRawReal` and `RestMetadata` for given trait. The `Implicits` type parameter lets you inject additional implicits
  * into macro materialization of these instances, e.g. [[io.udash.rest.DefaultRestImplicits DefaultRestImplicits]].
  * Usually, for even less boilerplate, this base class is extended by yet another abstract class which fixes
  * the `Implicits` type, e.g. [[io.udash.rest.DefaultRestApiCompanion DefaultRestApiCompanion]].
  */
abstract class RestApiCompanion[Implicits, Real](protected val implicits: Implicits)(
  implicit inst: MacroInstances[Implicits, FullInstances[Real]]
) {
  implicit final lazy val restMetadata: RestMetadata[Real] = inst(implicits, this).metadata
  implicit final lazy val restAsRaw: RawRest.AsRawRpc[Real] = inst(implicits, this).asRaw
  implicit final lazy val restAsReal: RawRest.AsRealRpc[Real] = inst(implicits, this).asReal

  final def fromHandleRequest(handleRequest: RawRest.HandleRequest): Real =
    RawRest.fromHandleRequest(handleRequest)
  final def asHandleRequest(real: Real): RawRest.HandleRequest =
    RawRest.asHandleRequest(real)
}

/** @see [[io.udash.rest.RestApiCompanion RestApiCompanion]] */
abstract class RestOpenApiCompanion[Implicits, Real](protected val implicits: Implicits)(
  implicit inst: MacroInstances[Implicits, OpenApiFullInstances[Real]]
) {
  implicit final lazy val restMetadata: RestMetadata[Real] = inst(implicits, this).metadata
  implicit final lazy val restAsRaw: RawRest.AsRawRpc[Real] = inst(implicits, this).asRaw
  implicit final lazy val restAsReal: RawRest.AsRealRpc[Real] = inst(implicits, this).asReal
  implicit final lazy val openapiMetadata: OpenApiMetadata[Real] = inst(implicits, this).openapiMetadata

  final def fromHandleRequest(handleRequest: RawRest.HandleRequest): Real =
    RawRest.fromHandleRequest(handleRequest)
  final def asHandleRequest(real: Real): RawRest.HandleRequest =
    RawRest.asHandleRequest(real)
}

trait PolyRestApiFullInstances[T[_[_]]] {
  def asRawRest[F[_] : AsyncEffect]: RawRest.AsRawRpc[T[F]]
  def fromRawRest[F[_] : AsyncEffect]: RawRest.AsRealRpc[T[F]]
  def restMetadata[F[_] : AsyncEffect]: RestMetadata[T[F]]
  def openapiMetadata[F[_] : AsyncEffect]: OpenApiMetadata[T[F]]
}

abstract class DefaultPolyRestApiCompanion[T[_[_]]](implicit
  instances: MacroInstances[GenCodecRestImplicits, PolyRestApiFullInstances[T]]
) {
  private lazy val inst = instances(GenCodecRestImplicits, this)
  implicit def asRawRest[F[_] : AsyncEffect]: RawRest.AsRawRpc[T[F]] = inst.asRawRest
  implicit def fromRawRest[F[_] : AsyncEffect]: RawRest.AsRealRpc[T[F]] = inst.fromRawRest
  implicit def restMetadata[F[_] : AsyncEffect]: RestMetadata[T[F]] = inst.restMetadata
  implicit def openapiMetadata[F[_] : AsyncEffect]: OpenApiMetadata[T[F]] = inst.openapiMetadata
}

/**
 * Like [[RestServerApiCompanion]] but the `Real` type is supposed to be a class with REST methods
 * already implemented - as opposed to a trait with abstract methods that needs separate implementation.
 * All public methods of this class - abstract or concrete - will be interpreted as REST methods
 * (unless annotated with [[com.avsystem.commons.meta.ignore ignore]]).
 */
abstract class RestServerApiImplCompanion[Implicits, Real](protected val implicits: Implicits)(
  implicit inst: MacroInstances[Implicits, ServerImplInstances[Real]]
) {
  implicit final lazy val restMetadata: RestMetadata[Real] = inst(implicits, this).metadata
  implicit final lazy val restAsRaw: RawRest.AsRawRpc[Real] = inst(implicits, this).asRaw

  final def asHandleRequest(real: Real): RawRest.HandleRequest =
    RawRest.asHandleRequest(real)
}

/**
 * Like [[RestServerApiImplCompanion]] but additionally materializes `OpenApiMetadata` for the class.
 */
abstract class RestServerOpenApiImplCompanion[Implicits, Real](protected val implicits: Implicits)(
  implicit inst: MacroInstances[Implicits, OpenApiServerImplInstances[Real]]
) {
  implicit final lazy val restMetadata: RestMetadata[Real] = inst(implicits, this).metadata
  implicit final lazy val restAsRaw: RawRest.AsRawRpc[Real] = inst(implicits, this).asRaw
  implicit final lazy val openapiMetadata: OpenApiMetadata[Real] = inst(implicits, this).openapiMetadata

  final def asHandleRequest(real: Real): RawRest.HandleRequest =
    RawRest.asHandleRequest(real)
}
