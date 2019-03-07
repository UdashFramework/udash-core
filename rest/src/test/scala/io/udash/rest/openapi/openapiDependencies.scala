package io.udash
package rest.openapi

import com.avsystem.commons.meta.{MacroInstances, infer}
import com.avsystem.commons.serialization.GenCodec
import io.udash.rest.openapi.adjusters.description
import io.udash.rest.raw.{RawRest, RestMetadata}
import io.udash.rest.{DefaultRestImplicits, FullInstances}

import scala.concurrent.Future

trait I18N {
  def t(key: String): String
}

trait CodecWithI18NStructure[T] {
  def codec: GenCodec[T]
  def structure(implicit i18n: I18N): RestStructure[T]
}

abstract class I18NRestDataCompanion[T](
  implicit instances: MacroInstances[DefaultRestImplicits, CodecWithI18NStructure[T]]
) {
  implicit lazy val codec: GenCodec[T] = instances(DefaultRestImplicits, this).codec
  implicit def restSchema(implicit i18N: I18N): RestSchema[T] =
    RestSchema.lazySchema(instances(DefaultRestImplicits, this).structure.restSchema)
}

trait I18NOpenApiFullInstances[Real] extends FullInstances[Real] {
  def openapiMetadata(implicit i18N: I18N): OpenApiMetadata[Real]
}

abstract class RestI18NOpenApiCompanion[Real](
  implicit inst: MacroInstances[DefaultRestImplicits, I18NOpenApiFullInstances[Real]]
) {
  implicit final lazy val restMetadata: RestMetadata[Real] = inst(DefaultRestImplicits, this).metadata
  implicit final lazy val restAsRaw: RawRest.AsRawRpc[Real] = inst(DefaultRestImplicits, this).asRaw
  implicit final lazy val restAsReal: RawRest.AsRealRpc[Real] = inst(DefaultRestImplicits, this).asReal
  implicit final def openapiMetadata(implicit i18N: I18N): OpenApiMetadata[Real] =
    inst(DefaultRestImplicits, this).openapiMetadata

  final def fromHandleRequest(handleRequest: RawRest.HandleRequest): Real =
    RawRest.fromHandleRequest(handleRequest)
  final def asHandleRequest(real: Real): RawRest.HandleRequest =
    RawRest.asHandleRequest(real)
}

class descriptionKey(key: String, @infer i18n: I18N = infer.value)
  extends description(i18n.t(key))

@descriptionKey("person.desc")
case class Person(
  @descriptionKey("name.desc") name: String
)
object Person extends I18NRestDataCompanion[Person]

trait Api {
  @descriptionKey("update.desc")
  def update(@descriptionKey("update.person.desc") person: Person): Future[Unit]
}
object Api extends RestI18NOpenApiCompanion[Api]