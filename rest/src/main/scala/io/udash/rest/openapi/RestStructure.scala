package io.udash
package rest.openapi

import com.avsystem.commons._
import com.avsystem.commons.annotation.positioned
import com.avsystem.commons.meta._
import com.avsystem.commons.misc.ValueOf
import com.avsystem.commons.rpc.AsRaw
import com.avsystem.commons.serialization._
import com.avsystem.commons.serialization.json.JsonStringOutput
import io.udash.rest.openapi.adjusters._
import io.udash.rest.raw._

sealed trait RestStructure[T] extends TypedMetadata[T] {
  def schemaAdjusters: List[SchemaAdjuster]
  def info: GenInfo[T]

  protected def createSchema(resolver: SchemaResolver): RefOr[Schema]

  def restSchema: RestSchema[T] =
    RestSchema.create(createSchema, info.rawName)

  protected def applyAdjusters(schema: Schema): Schema =
    schemaAdjusters.foldRight(schema)(_ adjustSchema _)
}
object RestStructure extends AdtMetadataCompanion[RestStructure] {
  @positioned(positioned.here) case class Union[T](
    @multi @reifyAnnot schemaAdjusters: List[SchemaAdjuster],
    @adtCaseMetadata @multi cases: List[Case[_]],
    @composite info: GenUnionInfo[T]
  ) extends RestStructure[T] {

    protected def createSchema(resolver: SchemaResolver): RefOr[Schema] =
      info.flatten.map(_.caseFieldName) match {
        case Opt(caseFieldName) =>
          val caseSchemas = cases.map { c =>
            // discriminator field requires that all case schemas are references
            resolver.resolve(c.restSchema) match {
              case RefOr.Value(schema) =>
                resolver.resolve(RestSchema.plain(schema).named(c.info.rawName))
              case s: RefOr.Ref => s
            }
          }

          val defaultCase = cases.findOpt(_.info.defaultCase)
          val discSchema = Schema(
            `type` = DataType.Object,
            properties = Map(caseFieldName -> RefOr(Schema(
              `type` = DataType.String,
              enum = cases.map(_.caseNameJson),
              default = defaultCase.map(_.caseNameJson).toOptArg
            ))),
            required = if (defaultCase.isEmpty) List(caseFieldName) else Nil
          )

          val discriminator = Discriminator(
            caseFieldName,
            (cases zip caseSchemas).iterator.collect {
              case (c, RefOr.Ref(ref)) if !ref.endsWith(s"/${c.info.rawName}") => (c.info.rawName, ref)
            }.toMap
          )

          RefOr(applyAdjusters(Schema(allOf = List(
            RefOr(discSchema),
            RefOr(Schema(oneOf = caseSchemas, discriminator = discriminator))
          ))))

        case Opt.Empty =>
          val caseSchemas = cases.map(c => RefOr(Schema(
            `type` = DataType.Object,
            properties = Map(c.info.rawName -> resolver.resolve(c.restSchema)),
            required = List(c.info.rawName)
          )))
          RefOr(applyAdjusters(Schema(oneOf = caseSchemas)))
      }
  }
  object Union extends AdtMetadataCompanion[Union]

  sealed trait Case[T] extends TypedMetadata[T] {
    def info: GenCaseInfo[T]
    def restSchema: RestSchema[T]

    def caseNameJson: JsonValue =
      JsonValue(JsonStringOutput.write(info.rawName))
  }
  object Case extends AdtMetadataCompanion[Case]

  /**
    * Will be inferred for case types that already have [[io.udash.rest.openapi.RestSchema RestSchema]] defined directly.
    */
  @positioned(positioned.here) case class CustomCase[T](
    @composite info: GenCaseInfo[T],
    @checked @infer restSchema: RestSchema[T]
  ) extends Case[T]

  /**
    * Will be inferred for types having apply/unapply(Seq) pair in their companion.
    */
  @positioned(positioned.here) case class Record[T](
    @multi @reifyAnnot schemaAdjusters: List[SchemaAdjuster],
    @adtParamMetadata @multi fields: List[Field[_]],
    @composite info: GenCaseInfo[T]
  ) extends RestStructure[T] with Case[T] {

    protected def createSchema(resolver: SchemaResolver): RefOr[Schema] = fields match {
      case single :: Nil if info.transparent =>
        SchemaAdjuster.adjustRef(schemaAdjusters, resolver.resolve(single.restSchema))
      case _ =>
        val properties = fields.iterator.map(f => (f.info.rawName, f.resolveSchema(resolver))).toMap
        val required = fields.iterator.filterNot(_.hasFallbackValue).map(_.info.rawName).toList
        RefOr(applyAdjusters(Schema(`type` = DataType.Object, properties = properties, required = required)))
    }
  }
  object Record extends AdtMetadataCompanion[Record]

  /**
    * Will be inferred for singleton types (objects).
    */
  @positioned(positioned.here) case class Singleton[T](
    @multi @reifyAnnot schemaAdjusters: List[SchemaAdjuster],
    @infer @checked value: ValueOf[T],
    @composite info: GenCaseInfo[T]
  ) extends RestStructure[T] with Case[T] {
    protected def createSchema(resolver: SchemaResolver): RefOr[Schema] =
      RefOr(applyAdjusters(Schema(`type` = DataType.Object)))
  }
  object Singleton extends AdtMetadataCompanion[Singleton]

  case class Field[T](
    @composite info: GenParamInfo[T],
    @infer restSchema: RestSchema[T],
    @optional @composite whenAbsentInfo: Opt[WhenAbsentInfo[T]],
    @optional @composite defaultValueInfo: Opt[DefaultValueInfo[T]],
    @multi @reifyAnnot schemaAdjusters: List[SchemaAdjuster]
  ) extends TypedMetadata[T] {

    val fallbackValue: Opt[JsonValue] =
      (whenAbsentInfo.map(_.fallbackValue) orElse defaultValueInfo.map(_.fallbackValue)).flatten
    val hasFallbackValue: Boolean = fallbackValue.isDefined

    def resolveSchema(resolver: SchemaResolver): RefOr[Schema] = {
      val bareSchema = resolver.resolve(restSchema).withDefaultValue(fallbackValue)
      SchemaAdjuster.adjustRef(schemaAdjusters, bareSchema)
    }
  }

  case class DefaultValueInfo[T](
    @reifyDefaultValue defaultValue: DefaultValue[T],
    @infer("for default value: ") asJson: AsRaw[JsonValue, T]
  ) extends TypedMetadata[T] {
    val fallbackValue: Opt[JsonValue] =
      Try(defaultValue.value).toOpt.map(asJson.asRaw)
  }
}
