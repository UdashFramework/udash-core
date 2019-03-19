package io.udash
package rest

import com.avsystem.commons.meta.MacroInstances
import com.avsystem.commons.serialization.GenCodec
import io.udash.rest.openapi.{RestSchema, RestStructure}

trait PolyCodecWithStructure[C[_]] {
  def codec[T0: GenCodec]: GenCodec[C[T0]]
  def structure[T0: RestSchema]: RestStructure[C[T0]]
}

abstract class PolyRestDataCompanion[C[_]](implicit
  instances: MacroInstances[DefaultRestImplicits, PolyCodecWithStructure[C]]
) extends {
  implicit def codec[T: GenCodec]: GenCodec[C[T]] = instances(DefaultRestImplicits, this).codec
  implicit def restStructure[T: RestSchema]: RestStructure[C[T]] = instances(DefaultRestImplicits, this).structure
  implicit def restSchema[T: RestSchema]: RestSchema[C[T]] = RestSchema.lazySchema(restStructure[T].standaloneSchema)
}
