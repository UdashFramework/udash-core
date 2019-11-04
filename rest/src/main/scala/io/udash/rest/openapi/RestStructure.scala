package io.udash
package rest.openapi

import com.avsystem.commons._
import com.avsystem.commons.annotation.positioned
import com.avsystem.commons.meta._
import com.avsystem.commons.misc.ValueOf
import com.avsystem.commons.rpc.AsRaw
import com.avsystem.commons.serialization._
import io.udash.rest.openapi.adjusters._
import io.udash.rest.raw._

sealed trait RestStructure[T] extends TypedMetadata[T] {
  def schemaAdjusters: List[SchemaAdjuster]
  def standaloneSchema: RestSchema[T]
  def info: GenInfo[T]

  protected def applyAdjusters(schema: Schema): Schema =
    schemaAdjusters.foldRight(schema)(_ adjustSchema _)
}
object RestStructure extends AdtMetadataCompanion[RestStructure] {
  @positioned(positioned.here) final case class Union[T](
    @multi @reifyAnnot schemaAdjusters: List[SchemaAdjuster],
    @adtCaseMetadata @multi cases: List[Case[_]],
    @composite info: GenUnionInfo[T]
  ) extends RestStructure[T] {

    def standaloneSchema: RestSchema[T] =
      RestSchema.create(createSchema, info.rawName)

    private def createSchema(resolver: SchemaResolver): RefOr[Schema] = {
      val caseFieldOpt = info.flatten.map(_.caseFieldName)
      val caseSchemas = cases.map { c =>
        val baseSchema = resolver.resolve(c.caseSchema(caseFieldOpt))
        if (caseFieldOpt.nonEmpty) baseSchema
        else RefOr(Schema(
          `type` = DataType.Object,
          properties = IListMap(c.info.rawName -> baseSchema),
          required = List(c.info.rawName)
        ))
      }
      val disc = caseFieldOpt.map { caseFieldName =>
        val mapping = IListMap((cases zip caseSchemas).collect {
          case (c, RefOr.Ref(ref)) => (c.info.rawName, ref)
        }: _*)
        Discriminator(caseFieldName, mapping)
      }
      RefOr(applyAdjusters(Schema(
        `type` = DataType.Object,
        oneOf = caseSchemas,
        discriminator = disc.toOptArg
      )))
    }
  }
  object Union extends AdtMetadataCompanion[Union]

  sealed trait Case[T] extends TypedMetadata[T] {
    def info: GenCaseInfo[T]
    def caseSchema(caseFieldName: Opt[String]): RestSchema[T]
  }
  object Case extends AdtMetadataCompanion[Case]

  /**
   * Will be inferred for case types that already have [[io.udash.rest.openapi.RestSchema RestSchema]] defined directly.
   */
  @positioned(positioned.here) final case class CustomCase[T](
    @checked @infer restSchema: RestSchema[T],
    @composite info: GenCaseInfo[T]
  ) extends Case[T] {
    def caseSchema(caseFieldName: Opt[String]): RestSchema[T] =
      caseFieldName.fold(restSchema) { cfn =>
        val caseFieldSchema = RefOr(Schema.enumOf(List(info.rawName)))
        val taggedName =
          if (restSchema.name.contains(info.rawName)) s"tagged${info.rawName}"
          else info.rawName
        restSchema.map({
          case RefOr.Value(caseSchema) => caseSchema.copy(
            properties = caseSchema.properties + (cfn -> caseFieldSchema),
            required = cfn :: caseSchema.required
          )
          case ref => Schema(allOf = List(RefOr(Schema(
            `type` = DataType.Object,
            properties = IListMap(cfn -> caseFieldSchema),
            required = List(cfn)
          )), ref))
        }, taggedName)
      }
  }

  /**
   * Will be inferred for types having apply/unapply(Seq) pair in their companion.
   */
  @positioned(positioned.here) final case class Record[T](
    @multi @reifyAnnot schemaAdjusters: List[SchemaAdjuster],
    @adtParamMetadata @multi fields: List[Field[_]],
    @composite info: GenCaseInfo[T]
  ) extends RestStructure[T] with Case[T] {

    def standaloneSchema: RestSchema[T] =
      RestSchema.create(createSchema(_, Opt.Empty), info.rawName)

    def caseSchema(caseFieldName: Opt[String]): RestSchema[T] =
      RestSchema.create(createSchema(_, caseFieldName), caseFieldName.map(_ => info.rawName).toOptArg)

    private def createSchema(resolver: SchemaResolver, caseFieldName: Opt[String]): RefOr[Schema] =
      (fields, caseFieldName) match {
        case (single :: Nil, Opt.Empty) if info.transparent =>
          SchemaAdjuster.adjustRef(schemaAdjusters, resolver.resolve(single.restSchema))
        case _ =>
          val props = caseFieldName.map(cfn => (cfn, RefOr(Schema.enumOf(List(info.rawName))))).iterator ++
            fields.iterator.map(f => (f.info.rawName, f.resolveSchema(resolver)))
          val required = caseFieldName.iterator ++
            fields.iterator.filterNot(_.hasFallbackValue).map(_.info.rawName)
          RefOr(applyAdjusters(Schema(`type` = DataType.Object,
            properties = IListMap(props.toList: _*),
            required = required.toList
          )))
      }
  }
  object Record extends AdtMetadataCompanion[Record]

  /**
   * Will be inferred for singleton types (objects).
   */
  @positioned(positioned.here) final case class Singleton[T](
    @multi @reifyAnnot schemaAdjusters: List[SchemaAdjuster],
    @infer @checked value: ValueOf[T],
    @composite info: GenCaseInfo[T]
  ) extends RestStructure[T] with Case[T] {

    def standaloneSchema: RestSchema[T] =
      RestSchema.create(createSchema(_, Opt.Empty))

    def caseSchema(caseFieldName: Opt[String]): RestSchema[T] =
      RestSchema.create(createSchema(_, caseFieldName), caseFieldName.map(_ => info.rawName).toOptArg)

    def createSchema(resolver: SchemaResolver, caseFieldName: Opt[String]): RefOr[Schema] =
      RefOr(applyAdjusters(Schema(`type` = DataType.Object,
        properties = IListMap(caseFieldName.map(cfn => (cfn, RefOr(Schema.enumOf(List(info.rawName))))).toList: _*),
        required = caseFieldName.toList
      )))
  }
  object Singleton extends AdtMetadataCompanion[Singleton]

  final case class Field[T](
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

  final case class DefaultValueInfo[T](
    @reifyDefaultValue defaultValue: DefaultValue[T],
    @infer("Cannot materialize default parameter value:\n") asJson: AsRaw[JsonValue, T]
  ) extends TypedMetadata[T] {
    val fallbackValue: Opt[JsonValue] =
      Try(defaultValue.value).toOpt.map(asJson.asRaw)
  }

  final case class NameAndAdjusters[T](
    @reifyName sourceName: String,
    @optional @reifyAnnot annotName: Opt[name],
    @multi @reifyAnnot schemaAdjusters: List[SchemaAdjuster]
  ) extends TypedMetadata[T] {
    def restSchema(wrappedSchema: RestSchema[_]): RestSchema[T] = RestSchema.create(
      r => SchemaAdjuster.adjustRef(schemaAdjusters, r.resolve(wrappedSchema)),
      annotName.fold(sourceName)(_.name)
    )
  }
  object NameAndAdjusters extends AdtMetadataCompanion[NameAndAdjusters]
}
