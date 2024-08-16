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

  private object ShallowInliningResolver extends SchemaResolver {
    def resolve(schema: RestSchema[_]): RefOr[Schema] =
      schema.name.fold(schema.createSchema(this))(RefOr.ref)
  }

  @positioned(positioned.here) final case class Union[T](
    @multi @reifyAnnot schemaAdjusters: List[SchemaAdjuster],
    @adtCaseMetadata @multi cases: List[Case[_]],
    @infer schemaName: GeneratedSchemaName[T],
    @composite info: GenUnionInfo[T],
  ) extends RestStructure[T] {

    def standaloneSchema: RestSchema[T] =
      RestSchema.create(createSchema, schemaName.name.toOptArg)

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
   *
   * For flat sealed hierarchy, if the existing [[RestSchema]] already has expected discriminator field it will be
   * returned unchanged (in order to preserve original schema name), otherwise new schema instance will be created with
   * additional single-value enum field.
   */
  @positioned(positioned.here) final case class CustomCase[T](
    @checked @infer restSchema: RestSchema[T],
    @infer schemaName: GeneratedSchemaName[T],
    @composite info: GenCaseInfo[T],
  ) extends Case[T] {

    def caseSchema(caseFieldName: Opt[String]): RestSchema[T] =
      caseFieldName.fold(restSchema) { cfn =>
        val caseFieldSchema = RefOr(Schema.enumOf(List(info.rawName)))

        // Creates new schema with additional discriminator field.
        // Used only if case schema does not already contain the discriminator field.
        //
        // If the case schema is unnamed or has different name than specified by its `SchemaName`,
        // it means that someone has manually overridden the schema name directly on `RestSchema` instance.
        // We can take advantage of that situation and reuse the `SchemaName` for the name of the _tagged_
        // case schema, i.e. the schema with added discriminator field.
        // Otherwise we must generate another, unique schema name, which we do by prepending
        // the original name with prefix "tagged".
        //
        def schemaWithDiscriminatorField: RestSchema[T] = {
          val preferredTaggedSchemaName = schemaName.name.getOrElse(info.rawName)
          val taggedName =
            if (restSchema.name.contains(preferredTaggedSchemaName)) s"tagged$preferredTaggedSchemaName"
            else preferredTaggedSchemaName

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

        // `restSchema` needs to be resolved in-place to check for discriminator field
        restSchema.createSchema(ShallowInliningResolver) match {
          case RefOr.Value(caseSchema) =>
            // Check if schema contains discriminator field
            caseSchema.properties.getOpt(cfn) match {
              case Opt(existingDiscriminator) =>
                if (existingDiscriminator != caseFieldSchema) {
                  // If existing field has different schema than expected, report an error
                  throw new IllegalArgumentException(
                    s"Cannot materialize schema for ${info.sourceName}, discriminator field conflict"
                  )
                }
                // When provided `restSchema` already contains the expected discriminator field just return it unchanged
                restSchema
              case Opt.Empty =>
                // If there's no discriminator field, we must manually add it and create a new case schema
                schemaWithDiscriminatorField
            }
          case _ =>
            // Case schema is a hardcoded, opaque reference to an external schema - generate new schema with the
            // discriminator field.
            schemaWithDiscriminatorField
        }
      }
  }

  /**
   * Will be inferred for types having apply/unapply(Seq) pair in their companion.
   */
  @positioned(positioned.here) final case class Record[T](
    @multi @reifyAnnot schemaAdjusters: List[SchemaAdjuster],
    @adtParamMetadata @allowOptional @multi fields: List[Field[_]],
    @infer schemaName: GeneratedSchemaName[T],
    @composite info: GenCaseInfo[T],
  ) extends RestStructure[T] with Case[T] {

    def standaloneSchema: RestSchema[T] =
      RestSchema.create(createSchema(_, Opt.Empty), schemaName.name.toOptArg)

    def caseSchema(caseFieldName: Opt[String]): RestSchema[T] =
      RestSchema.create(createSchema(_, caseFieldName), caseFieldName.flatMap(_ => schemaName.name).toOptArg)

    private def createSchema(resolver: SchemaResolver, caseFieldName: Opt[String]): RefOr[Schema] =
      (fields, caseFieldName) match {
        case (single :: Nil, Opt.Empty) if info.transparent =>
          SchemaAdjuster.adjustRef(schemaAdjusters, resolver.resolve(single.restSchema))
        case _ =>
          val props = caseFieldName.map(cfn => (cfn, RefOr(Schema.enumOf(List(info.rawName))))).iterator ++
            fields.iterator.map(f => (f.info.rawName, f.resolveSchema(resolver)))
          val required = caseFieldName.iterator ++
            fields.iterator.filterNot(_.optional).map(_.info.rawName)
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
    @infer schemaName: GeneratedSchemaName[T],
    @composite info: GenCaseInfo[T],
  ) extends RestStructure[T] with Case[T] {

    def standaloneSchema: RestSchema[T] =
      RestSchema.create(createSchema(_, Opt.Empty))

    def caseSchema(caseFieldName: Opt[String]): RestSchema[T] =
      RestSchema.create(createSchema(_, caseFieldName), caseFieldName.flatMap(_ => schemaName.name).toOptArg)

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
    @multi @reifyAnnot schemaAdjusters: List[SchemaAdjuster],
  ) extends TypedMetadata[T] {

    val fallbackValue: Opt[JsonValue] =
      if (info.optional) Opt.Empty
      else (whenAbsentInfo.map(_.fallbackValue) orElse defaultValueInfo.map(_.fallbackValue)).flatten

    val optional: Boolean = info.optional || fallbackValue.isDefined

    def resolveSchema(resolver: SchemaResolver): RefOr[Schema] = {
      val bareSchema = resolver.resolve(restSchema).withDefaultValue(fallbackValue)
      SchemaAdjuster.adjustRef(schemaAdjusters, bareSchema)
    }
  }

  final case class DefaultValueInfo[T](
    @reifyDefaultValue defaultValue: DefaultValue[T],
    @infer("Cannot materialize default parameter value:\n") asJson: AsRaw[JsonValue, T],
  ) extends TypedMetadata[T] {
    val fallbackValue: Opt[JsonValue] =
      Try(defaultValue.value).toOpt.map(asJson.asRaw)
  }

  final case class NameAndAdjusters[T](
    @reifyName sourceName: String,
    @optional @reifyAnnot annotName: Opt[name],
    @optional @reifyAnnot annotSchemaName: Opt[schemaName],
    @multi @reifyAnnot schemaAdjusters: List[SchemaAdjuster],
  ) extends TypedMetadata[T] {
    def restSchema(wrappedSchema: RestSchema[_]): RestSchema[T] = RestSchema.create(
      r => SchemaAdjuster.adjustRef(schemaAdjusters, r.resolve(wrappedSchema)),
      annotSchemaName.map(_.name).orElse(annotName.map(_.name)).getOrElse[String](sourceName),
    )
  }
  object NameAndAdjusters extends AdtMetadataCompanion[NameAndAdjusters]
}
