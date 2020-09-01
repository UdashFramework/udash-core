package io.udash.rest.tsgen

import com.avsystem.commons._
import com.avsystem.commons.annotation.positioned
import com.avsystem.commons.meta._
import com.avsystem.commons.misc.ValueOf
import com.avsystem.commons.serialization.json.JsonStringOutput
import com.avsystem.commons.serialization.{GenCaseInfo, GenParamInfo, GenUnionInfo}
import io.udash.rest.tsgen.TsTypeMetadata.Record.recursionBreaker

import scala.util.DynamicVariable

sealed trait TsTypeMetadata[T] extends TypedMetadata[T] with TsJsonType

object TsTypeMetadata extends AdtMetadataCompanion[TsTypeMetadata] {
  private def quote(str: String): String = JsonStringOutput.write(str)

  @positioned(positioned.here)
  final case class Union[T](
    @composite info: GenUnionInfo[T],
    @infer moduleTag: TsModuleTag[T],
    @multi @adtCaseMetadata cases: List[Case[_]]
  ) extends TsTypeMetadata[T] with TsDefinition {
    val module: TsModule =
      moduleTag.module

    lazy val managedCases: List[Case[_]] =
      cases.filter(_.managed)

    val name: String =
      info.sourceName

    val rawName: String =
      info.annotName.map(_.name).getOrElse(info.sourceName)

    def contents(gen: TsGenerator): String = {
      val discriminator = info.flatten.map(_.caseFieldName)

      def caseType(cse: Case[_]): String = discriminator match {
        case Opt(discr) => s"{readonly $discr: ${quote(cse.name)}} & ${cse.tsType.resolve(gen)}"
        case Opt.Empty => s"{readonly ${cse.name}: ${cse.tsType.resolve(gen)}}"
      }

      val namespaceDecl = if (managedCases.isEmpty) "" else {
        val caseInfos = managedCases.iterator.map(_.caseInfoDeclaration(gen)).mkString("{", ", ", "}")
        val codecDecl = discriminator match {
          case Opt(discr) => s"${gen.codecsModule}.flatUnion(${quote(discr)}, $caseInfos)"
          case Opt.Empty => s"${gen.codecsModule}.nestedUnion($caseInfos)"
        }

        s"""
           |export namespace $name {
           |    export const codec: ${gen.codecsModule}.JsonCodec<$name> = $codecDecl
           |}""".stripMargin
      }

      s"export type $name = ${cases.iterator.map(caseType).mkString(" | ")}$namespaceDecl\n"
    }

    def jsonCodecRef: Opt[TsReference] =
      if (managedCases.isEmpty) Opt.Empty
      else Opt(gen => s"${resolve(gen)}.codec")
  }

  sealed abstract class Case[T] extends TypedMetadata[T] {
    def info: GenCaseInfo[T]
    def moduleTag: TsModuleTag[T]
    def managed: Boolean
    def tsType: TsJsonType

    lazy val module: TsModule =
      moduleTag.module

    lazy val name: String =
      info.sourceName

    lazy val rawName: String =
      info.annotName.map(_.name).getOrElse(info.sourceName)

    def caseInfoDeclaration(gen: TsGenerator): String = {
      val rawNameDef = Opt(rawName).filter(_ != name).map(rn => s"rawName: ${quote(rn)}")
      val codecDef = tsType.jsonCodecRef.map(c => s"codec: () => ${c.resolve(gen)}")
      (rawNameDef ++ codecDef).mkString(s"${quote(name)}: {", ", ", "}")
    }
  }

  // for case classes/objects in sealed hierarchy which have manually defined TsJsonTypeTag
  @positioned(positioned.here)
  final case class CustomCase[T](
    @composite info: GenCaseInfo[T],
    @infer moduleTag: TsModuleTag[T],
    @infer @checked jsonTypeTag: TsJsonTypeTag[T]
  ) extends Case[T] {
    def tsType: TsJsonType = jsonTypeTag.tsType
    def managed: Boolean = tsType.jsonCodecRef.isDefined
  }

  @positioned(positioned.here)
  final case class Record[T](
    @composite info: GenCaseInfo[T],
    @infer moduleTag: TsModuleTag[T],
    @multi @adtParamMetadata fields: List[Field[_]],
  ) extends Case[T] with TsTypeMetadata[T] with TsDefinition { rec =>
    lazy val managedFields: List[Field[_]] =
      fields.filter(_.managed)

    def managed: Boolean = recursionBreaker.value match {
      case Opt(thiz) if thiz == this => false
      case Opt(_) => isManaged
      case Opt.Empty => recursionBreaker.withValue(Opt(this))(isManaged)
    }

    private def isManaged: Boolean =
      name != rawName || managedFields.nonEmpty

    def tsType: TsJsonType = this

    def contents(gen: TsGenerator): String = {
      val fieldDefs = fields.iterator.map(_.declaration(gen)).mkString("\n", ",\n", "\n")

      val namespaceDecl = if (managedFields.isEmpty) "" else {
        val fieldInfos = managedFields.iterator.map(_.fieldInfoDeclaration(gen)).mkString("{", ", ", "}")
        s"""
           |export namespace $name {
           |    export const codec: ${gen.codecsModule}.JsonCodec<$name> = ${gen.codecsModule}.record($fieldInfos)
           |}""".stripMargin
      }

      s"export interface $name {$fieldDefs}$namespaceDecl\n"
    }

    def jsonCodecRef: Opt[TsReference] =
      if (managedFields.isEmpty) Opt.Empty
      else Opt(gen => s"${resolve(gen)}.codec")
  }
  object Record {
    private val recursionBreaker =
      new DynamicVariable[Opt[TsTypeMetadata[_]]](Opt.Empty)
  }

  @positioned(positioned.here)
  final case class Singleton[T](
    @composite info: GenCaseInfo[T],
    @infer moduleTag: TsModuleTag[T],
    @infer @checked value: ValueOf[T],
  ) extends Case[T] with TsTypeMetadata[T] with TsDefinition {
    def managed: Boolean = false

    def tsType: TsJsonType = this

    def contents(gen: TsGenerator): String =
      s"export type $name = {}\n"

    def jsonCodecRef: Opt[TsReference] = Opt.Empty
  }

  final case class Field[T](
    @composite info: GenParamInfo[T],
    @infer typeTag: TsJsonTypeTag[T],
  ) extends TypedMetadata[T] {
    val name: String =
      info.sourceName

    val rawName: String =
      info.annotName.map(_.name).getOrElse(info.sourceName)

    def managed: Boolean =
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
