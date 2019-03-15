package io.udash
package rest

import com.avsystem.commons.meta.MacroInstances
import io.udash.rest.openapi.{OpenApiMetadata, RestResponses, RestResultType}
import io.udash.rest.raw.RawRest.AsyncEffect
import io.udash.rest.raw._



object PolyRestImplicits extends GenCodecRestImplicits {
  implicit def asyncEffectHttpResponseType[F[_] : AsyncEffect, A]: HttpResponseType[F[A]] =
    HttpResponseType()

  implicit def asyncEffectRestResultType[F[_] : AsyncEffect, A: RestResponses]: RestResultType[F[A]] =
    RestResultType(RestResponses[A].responses)
}

trait PolyRestApiInstances[T[_[_]]] {
  def asRawRest[F[_] : AsyncEffect]: RawRest.AsRawRpc[T[F]]
  def fromRawRest[F[_] : AsyncEffect]: RawRest.AsRealRpc[T[F]]
  def restMetadata[F[_] : AsyncEffect]: RestMetadata[T[F]]
  def openapiMetadata[F[_] : AsyncEffect]: OpenApiMetadata[T[F]]
}

abstract class PolyRestApiCompanion[T[_[_]]](implicit
  instances: MacroInstances[PolyRestImplicits.type, PolyRestApiInstances[T]]
) {
  private lazy val inst = instances(PolyRestImplicits, this)
  implicit def asRawRest[F[_] : AsyncEffect]: RawRest.AsRawRpc[T[F]] = inst.asRawRest
  implicit def fromRawRest[F[_] : AsyncEffect]: RawRest.AsRealRpc[T[F]] = inst.fromRawRest
  implicit def restMetadata[F[_] : AsyncEffect]: RestMetadata[T[F]] = inst.restMetadata
  implicit def openapiMetadata[F[_] : AsyncEffect]: OpenApiMetadata[T[F]] = inst.openapiMetadata
}

trait PolyRestApi[F[_]] {
  def postThis(thing: String): F[Int]
}
object PolyRestApi extends PolyRestApiCompanion[PolyRestApi]
