package io.udash.rest.tsgen

import com.avsystem.commons._
import com.avsystem.commons.annotation.positioned
import com.avsystem.commons.meta._
import com.avsystem.commons.misc.ValueOf
import com.avsystem.commons.serialization.json.JsonStringOutput
import com.avsystem.commons.serialization.{GenCaseInfo, GenParamInfo, GenUnionInfo}
import io.udash.rest.tsgen.TsTypeMetadata.Record.recursionBreaker

import scala.util.DynamicVariable

sealed trait TsTypeMetadata[T] extends TypedMetadata[T] with TsJsonType {
  def mkJsonWrite(gen: TsGenerator, valueRef: String): String =
    if(transparent) valueRef
    else s"${resolve(gen)}.toJson($valueRef)"

  def mkJsonRead(gen: TsGenerator, valueRef: String): String =
    if(transparent) s"$valueRef as ${resolve(gen)}"
    else s"${resolve(gen)}.fromJson($valueRef)"

  override def mkJsonWriter(gen: TsGenerator): String =
    if(!transparent) s"${resolve(gen)}.toJson" else super.mkJsonWriter(gen)

  override def mkJsonReader(gen: TsGenerator): String =
    if(!transparent) s"${resolve(gen)}.fromJson" else super.mkJsonReader(gen)
}

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
      cases.filterNot(_.transparent)

    def transparent: Boolean =
      managedCases.isEmpty

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

      def readerOrWriter(reader: Boolean): String = discriminator match {
        case Opt(discr) =>
          val funName = if (reader) "flatJsonToUnion" else "unionToFlatJson"
          s"${gen.codecsModule}.$funName(${quote(discr)}, managedCases)"
        case Opt.Empty =>
          val funName = if (reader) "nestedJsonToUnion" else "unionToNestedJson"
          s"${gen.codecsModule}.$funName(managedCases)"
      }

      val namespaceDecl = if (managedCases.isEmpty) "" else {
        val caseInfos = managedCases.iterator.map(_.caseInfoDeclaration(gen)).mkString("{", ", ", "}")

        s"""
           |export namespace $name {
           |    const managedCases: ${gen.codecsModule}.CaseInfos = $caseInfos
           |    export const fromJson: ${gen.codecsModule}.JsonReader<$name> =
           |        ${readerOrWriter(reader = true)}
           |    export const toJson: ${gen.codecsModule}.JsonWriter<$name> =
           |        ${readerOrWriter(reader = false)}
           |}""".stripMargin
      }

      s"export type $name = ${cases.iterator.map(caseType).mkString(" | ")}$namespaceDecl\n"
    }
  }

  sealed abstract class Case[T] extends TypedMetadata[T] {
    def info: GenCaseInfo[T]
    def moduleTag: TsModuleTag[T]
    def transparent: Boolean
    def tsType: TsJsonType

    lazy val module: TsModule =
      moduleTag.module

    lazy val name: String =
      info.sourceName

    lazy val rawName: String =
      info.annotName.map(_.name).getOrElse(info.sourceName)

    def caseInfoDeclaration(gen: TsGenerator): String = {
      val rawNameDef = Opt(rawName).filter(_ != name).map(rn => s"rawName: ${quote(rn)}")
      val readerDef = if(tsType.transparent) Opt.Empty else Opt(s"reader: ${tsType.mkJsonReader(gen)}")
      val writerDef = if(tsType.transparent) Opt.Empty else Opt(s"writer: ${tsType.mkJsonWriter(gen)}")
      (rawNameDef ++ readerDef ++ writerDef).mkString(s"${quote(name)}: {", ", ", "}")
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
    def transparent: Boolean = tsType.transparent
  }

  @positioned(positioned.here)
  final case class Record[T](
    @composite info: GenCaseInfo[T],
    @infer moduleTag: TsModuleTag[T],
    @multi @adtParamMetadata fields: List[Field[_]],
  ) extends Case[T] with TsTypeMetadata[T] with TsDefinition { rec =>
    lazy val managedFields: List[Field[_]] =
      fields.filterNot(_.transparent)

    def transparent: Boolean = recursionBreaker.value match {
      case Opt(thiz) if thiz == this => true
      case Opt(_) => isTransparent
      case Opt.Empty => recursionBreaker.withValue(Opt(this))(isTransparent)
    }

    private def isTransparent: Boolean =
      name == rawName && managedFields.isEmpty

    def tsType: TsJsonType = this

    def contents(gen: TsGenerator): String = {
      val fieldDefs = fields.iterator.map(_.declaration(gen)).mkString("\n", ",\n", "\n")

      val namespaceDecl = if (managedFields.isEmpty) "" else {
        val fieldInfos = managedFields.iterator.map(_.fieldInfoDeclaration(gen)).mkString("{", ", ", "}")
        s"""
           |export namespace $name {
           |    const managedFields: ${gen.codecsModule}.FieldInfos = $fieldInfos
           |    export const fromJson: ${gen.codecsModule}.JsonReader<$name> =
           |        ${gen.codecsModule}.jsonToRecord(managedFields)
           |    export const toJson: ${gen.codecsModule}.JsonWriter<$name> =
           |        ${gen.codecsModule}.recordToJson(managedFields)
           |}""".stripMargin
      }

      s"export interface $name {$fieldDefs}$namespaceDecl\n"
    }
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
    def tsType: TsJsonType = this

    def contents(gen: TsGenerator): String =
      s"export type $name = {}\n"

    def transparent: Boolean = true

    override def mkJsonWrite(gen: TsGenerator, valueRef: String): String =
      valueRef

    override def mkJsonRead(gen: TsGenerator, valueRef: String): String =
      s"$valueRef as ${resolve(gen)}"
  }

  final case class Field[T](
    @composite info: GenParamInfo[T],
    @infer typeTag: TsJsonTypeTag[T],
  ) extends TypedMetadata[T] {
    val name: String =
      info.sourceName

    val rawName: String =
      info.annotName.map(_.name).getOrElse(info.sourceName)

    def tsType: TsJsonType = typeTag.tsType

    def transparent: Boolean =
      rawName == name && tsType.transparent

    def declaration(gen: TsGenerator): String = {
      val tpeRef = typeTag.tsType.resolve(gen)
      s"    readonly $name: $tpeRef"
    }

    def fieldInfoDeclaration(gen: TsGenerator): String = {
      val rawNameDef = Opt(rawName).filter(_ != name).map(rn => s"rawName: ${quote(rn)}")
      val readerDef = if(tsType.transparent) Opt.Empty else Opt(s"reader: ${tsType.mkJsonReader(gen)}")
      val writerDef = if(tsType.transparent) Opt.Empty else Opt(s"writer: ${tsType.mkJsonWriter(gen)}")
      (rawNameDef ++ readerDef ++ writerDef).mkString(s"${quote(name)}: {", ", ", "}")
    }
  }
}
