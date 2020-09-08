package io.udash.rest
package typescript

import com.avsystem.commons.meta.MacroInstances
import com.avsystem.commons.serialization.GenCodec
import com.avsystem.commons.{ClassTag, classTag}
import io.udash.rest.openapi.{OpenApiMetadata, RestSchema}
import io.udash.rest.raw.{RawRest, RestMetadata}

trait TsRestImplicits extends DefaultRestImplicits {
  implicit def moduleTag[T: ClassTag]: TsModuleTag[T] = {
    val pkgName = classTag[T].runtimeClass.getPackage.getName
    TsModuleTag(TsModule(pkgName.stripPrefix("io.udash.rest.").split("\\.").toList))
  }
}
object TsRestImplicits extends TsRestImplicits

trait TsRestDataInstances[T] extends CodecWithStructure[T] {
  def tsTypeMetadata: TsTypeMetadata[T]
}

abstract class TsRestDataCompanion[T](implicit instances: MacroInstances[TsRestImplicits, TsRestDataInstances[T]]) {
  implicit lazy val tsTypeTag: TsJsonTypeTag[T] = TsJsonTypeTag(instances(TsRestImplicits, this).tsTypeMetadata)
  implicit lazy val codec: GenCodec[T] = instances(TsRestImplicits, this).codec
  implicit lazy val restSchema: RestSchema[T] = instances(TsRestImplicits, this).structure.standaloneSchema

  def tsType: TsJsonType = tsTypeTag.tsType
}

trait TsRestApiInstances[Real] extends OpenApiFullInstances[Real] {
  def tsRestApiMetadata: TsRestApiMetadata[Real]
}

abstract class TsRestApiCompanion[Real](
  implicit inst: MacroInstances[TsRestImplicits, TsRestApiInstances[Real]]
) {
  implicit final lazy val restMetadata: RestMetadata[Real] = inst(TsRestImplicits, this).metadata
  implicit final lazy val restAsRaw: RawRest.AsRawRpc[Real] = inst(TsRestImplicits, this).asRaw
  implicit final lazy val restAsReal: RawRest.AsRealRpc[Real] = inst(TsRestImplicits, this).asReal
  implicit final lazy val openapiMetadata: OpenApiMetadata[Real] = inst(TsRestImplicits, this).openapiMetadata
  implicit final lazy val tsRestApiMetadata: TsRestApiMetadata[Real] = inst(TsRestImplicits, this).tsRestApiMetadata
  implicit final lazy val tsTypeTag: TsApiTypeTag[Real] = TsApiTypeTag(tsRestApiMetadata)
}
