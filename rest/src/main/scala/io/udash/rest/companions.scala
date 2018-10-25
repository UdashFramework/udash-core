package io.udash
package rest

import com.avsystem.commons.meta.MacroInstances
import io.udash.rest.openapi.OpenApiMetadata
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

/** @see [[RestApiCompanion]]*/
abstract class RestClientApiCompanion[Implicits, Real](protected val implicits: Implicits)(
  implicit inst: MacroInstances[Implicits, ClientInstances[Real]]
) {
  implicit final lazy val restMetadata: RestMetadata[Real] = inst(implicits, this).metadata
  implicit final lazy val restAsReal: RawRest.AsRealRpc[Real] = inst(implicits, this).asReal

  final def fromHandleRequest(handleRequest: RawRest.HandleRequest): Real =
    RawRest.fromHandleRequest(handleRequest)
}

/** @see [[RestApiCompanion]]*/
abstract class RestServerApiCompanion[Implicits, Real](protected val implicits: Implicits)(
  implicit inst: MacroInstances[Implicits, ServerInstances[Real]]
) {
  implicit final lazy val restMetadata: RestMetadata[Real] = inst(implicits, this).metadata
  implicit final lazy val restAsRaw: RawRest.AsRawRpc[Real] = inst(implicits, this).asRaw

  final def asHandleRequest(real: Real): RawRest.HandleRequest =
    RawRest.asHandleRequest(real)
}

/** @see [[RestApiCompanion]]*/
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
  * into macro materialization of these instances, e.g. [[DefaultRestImplicits]].
  * Usually, for even less boilerplate, this base class is extended by yet another abstract class which fixes
  * the `Implicits` type, e.g. [[DefaultRestApiCompanion]].
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

/** @see [[RestApiCompanion]]*/
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