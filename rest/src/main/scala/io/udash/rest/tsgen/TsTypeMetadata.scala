package io.udash.rest.tsgen

import com.avsystem.commons.annotation.positioned
import com.avsystem.commons.meta._
import com.avsystem.commons.serialization.{GenCaseInfo, GenParamInfo}

sealed trait TsTypeMetadata[T] extends TypedMetadata[T] {
  def tsType: TsJsonType

  final def tsTypeTag: TsJsonTypeTag[T] = TsJsonTypeTag(tsType)
}

object TsTypeMetadata extends AdtMetadataCompanion[TsTypeMetadata] {
  @positioned(positioned.here)
  final case class Record[T](
    @composite info: GenCaseInfo[T],
    @multi @adtParamMetadata fields: List[Field[_]],
  ) extends TsTypeMetadata[T] { rec =>
    def name: String =
      info.annotName.map(_.name).getOrElse(info.sourceName)

    def tsType: TsJsonType =
      TsType.Record(name, fields.map(_.tsField))
  }

  final case class Field[T](
    @composite info: GenParamInfo[T],
    @infer typeTag: TsJsonTypeTag[T],
  ) extends TypedMetadata[T] {
    def tsField: TsField = TsField(
      info.sourceName,
      info.annotName.map(_.name).getOrElse(info.sourceName),
      () => typeTag.tsType,
      info.hasWhenAbsent || info.flags.hasDefaultValue,
      info.flags.isRepeated
      // TODO: default value (only when not transient default)?
    )
  }
}
