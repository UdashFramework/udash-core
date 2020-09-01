package io.udash.rest.tsgen

import com.avsystem.commons._
import com.avsystem.commons.annotation.positioned
import com.avsystem.commons.meta._
import com.avsystem.commons.serialization.json.JsonStringOutput
import com.avsystem.commons.serialization.{GenCaseInfo, GenParamInfo}

sealed trait TsTypeMetadata[T] extends TypedMetadata[T] with TsJsonType {
  final def tsTypeTag: TsJsonTypeTag[T] = TsJsonTypeTag(this)
}

object TsTypeMetadata extends AdtMetadataCompanion[TsTypeMetadata] {
  private def quote(str: String): String = JsonStringOutput.write(str)

  @positioned(positioned.here)
  final case class Record[T](
    @composite info: GenCaseInfo[T],
    @infer moduleTag: TsModuleTag[T],
    @multi @adtParamMetadata fields: List[Field[_]],
  ) extends TsTypeMetadata[T] with TsJsonType with TsDefinition { rec =>
    val module: TsModule =
      moduleTag.module

    val name: String =
      info.sourceName

    val rawName: String =
      info.annotName.map(_.name).getOrElse(info.sourceName)

    val managedFields: List[Field[_]] =
      fields.filter(_.isManaged)

    def contents(gen: TsGenerator): String = {
      val fieldDefs = fields.iterator.map(_.declaration(gen)).mkString("\n", ",\n", "\n")

      val namespaceDecl = if (managedFields.isEmpty) "" else {
        val fieldInfos = managedFields.iterator.map(_.fieldInfoDeclaration(gen)).mkString("{", ", ", "}")
        s"""
           |export namespace $name {
           |    export const codec = ${gen.codecsModule}.record<$name>($fieldInfos)
           |}""".stripMargin
      }

      s"export interface $name {$fieldDefs}$namespaceDecl\n"
    }

    def jsonCodecRef: Opt[TsReference] =
      if (managedFields.isEmpty) Opt.Empty
      else Opt(gen => s"${resolve(gen)}.codec")
  }

  final case class Field[T](
    @composite info: GenParamInfo[T],
    @infer typeTag: TsJsonTypeTag[T],
  ) extends TypedMetadata[T] {
    val name: String =
      info.sourceName

    val rawName: String =
      info.annotName.map(_.name).getOrElse(info.sourceName)

    def isManaged: Boolean =
      rawName != name || typeTag.tsType.jsonCodecRef.isDefined

    def declaration(gen: TsGenerator): String = {
      val tpeRef = typeTag.tsType.resolve(gen)
      s"    readonly $name: $tpeRef"
    }

    def fieldInfoDeclaration(gen: TsGenerator): String = {
      val rawNameDef = Opt(rawName).filter(_ != name).map(rn => s"rawName: ${quote(rn)}")
      val codecDef = typeTag.tsType.jsonCodecRef.map(c => s"codec: () => ${c.resolve(gen)}")
      (rawNameDef ++ codecDef).mkString(s"${quote(name)}: {", ", ", "}")
    }
  }
}
