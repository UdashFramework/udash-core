package io.udash
package rest.openapi

import com.avsystem.commons._
import com.avsystem.commons.annotation.bincompat
import com.avsystem.commons.meta._
import com.avsystem.commons.rpc._
import com.avsystem.commons.serialization.optionalParam
import io.udash.macros.RestMacros
import io.udash.rest.openapi.adjusters._
import io.udash.rest.raw._
import io.udash.rest.{Header => HeaderAnnot, _}
import io.udash.utils.URLEncoder

import scala.annotation.implicitNotFound

@implicitNotFound("OpenApiMetadata for ${T} not found, " +
  "is it a valid REST API trait with properly defined companion object?")
@methodTag[RestMethodTag]
@methodTag[BodyTypeTag]
final case class OpenApiMetadata[T](
  @multi @rpcMethodMetadata
  @tagged[Prefix](whenUntagged = new Prefix)
  @tagged[NoBody](whenUntagged = new NoBody)
  @paramTag[RestParamTag](defaultTag = new Path)
  @unmatched(RawRest.NotValidPrefixMethod)
  @unmatchedParam[Body](RawRest.PrefixMethodBodyParam)
  prefixes: List[OpenApiPrefix[_]],

  @multi @rpcMethodMetadata
  @tagged[GET]
  @tagged[NoBody](whenUntagged = new NoBody)
  @paramTag[RestParamTag](defaultTag = new Query)
  @unmatched(RawRest.NotValidGetMethod)
  @unmatchedParam[Body](RawRest.GetMethodBodyParam)
  gets: List[OpenApiGetOperation[_]],

  @multi @rpcMethodMetadata
  @tagged[BodyMethodTag](whenUntagged = new POST)
  @tagged[CustomBody]
  @paramTag[RestParamTag](defaultTag = new Body)
  @unmatched(RawRest.NotValidCustomBodyMethod)
  @unmatchedParam[Body](RawRest.SuperfluousBodyParam)
  customBodyMethods: List[OpenApiCustomBodyOperation[_]],

  @multi @rpcMethodMetadata
  @tagged[BodyMethodTag](whenUntagged = new POST)
  @tagged[SomeBodyTag](whenUntagged = new JsonBody)
  @paramTag[RestParamTag](defaultTag = new Body)
  @unmatched(RawRest.NotValidHttpMethod)
  bodyMethods: List[OpenApiBodyOperation[_]]
) {
  val httpMethods: List[OpenApiOperation[_]] = (gets: List[OpenApiOperation[_]]) ++ customBodyMethods ++ bodyMethods

  // collect all tags
  private lazy val openApiTags: List[Tag] = {
    def createTag(method: OpenApiMethod[_]): Opt[Tag] =
      method.groupAnnot.map { group =>
        method.tagAdjusters.foldLeft(Tag(group.groupName))({ case (tag, adjuster) => adjuster.adjustTag(tag) })
      }

    (prefixes.iterator.flatMap(prefix => createTag(prefix).iterator ++ prefix.result.value.openApiTags.iterator) ++
      httpMethods.iterator.flatMap(createTag)).toList
  }

  def operations(resolver: SchemaResolver): Iterator[PathOperation] =
    prefixes.iterator.flatMap(_.operations(resolver)) ++
      httpMethods.iterator.map(_.pathOperation(resolver))

  def paths(resolver: SchemaResolver): Paths = {
    val operationIds = new MHashSet[String]
    val pathsMap = new MLinkedHashMap[String, MHashMap[HttpMethod, Operation]]
    // linked set to remove possible duplicates from prefix methods but to retain order
    val pathAdjustersMap = new MHashMap[String, MLinkedHashSet[PathItemAdjuster]]
    operations(resolver).foreach {
      case PathOperation(path, httpMethod, operation, pathAdjusters) =>
        val opsMap = pathsMap.getOrElseUpdate(path, new MHashMap)
        pathAdjustersMap.getOrElseUpdate(path, new MLinkedHashSet) ++= pathAdjusters
        opsMap(httpMethod) = operation
        operation.operationId.foreach { opid =>
          if (!operationIds.add(opid)) {
            throw new IllegalArgumentException(s"Duplicate operation ID: $opid. " +
              s"You can disambiguate with @operationIdPrefix and @operationId annotations.")
          }
        }
    }
    Paths(pathsMap.iterator.map { case (path, ops) =>
      val pathItem = PathItem(
        get = ops.getOpt(HttpMethod.GET).toOptArg,
        put = ops.getOpt(HttpMethod.PUT).toOptArg,
        post = ops.getOpt(HttpMethod.POST).toOptArg,
        patch = ops.getOpt(HttpMethod.PATCH).toOptArg,
        delete = ops.getOpt(HttpMethod.DELETE).toOptArg,
      )
      (path, RefOr(pathAdjustersMap(path).foldRight(pathItem)(_ adjustPathItem _)))
    }.intoMap[ITreeMap])
  }

  def openapi(
    info: Info,
    components: Components = Components(),
    servers: List[Server] = Nil,
    security: List[SecurityRequirement] = Nil,
    tags: List[Tag] = Nil,
    externalDocs: OptArg[ExternalDocumentation] = OptArg.Empty
  ): OpenApi = {
    val registry = new SchemaRegistry(initial = components.schemas)
    OpenApi(
      OpenApi.Version,
      info,
      paths(registry),
      components = components.copy(schemas = registry.registeredSchemas),
      servers = servers,
      security = security,
      tags = openApiTags ++ tags,
      externalDocs = externalDocs,
    )
  }
}
object OpenApiMetadata extends RpcMetadataCompanion[OpenApiMetadata] {
  /**
   * Materializes [[OpenApiMetadata]] for an arbitrary type - usually a class that implements REST API directly,
   * without a base trait. All public methods of this type are scanned and interpreted as REST methods
   * (as opposed to REST traits which only have their abstract methods scanned).
   * You can exclude methods using the [[com.avsystem.commons.meta.ignore ignore]] annotation.
   */
  def materializeForImpl[Real]: OpenApiMetadata[Real] = macro RestMacros.materializeImplOpenApiMetadata[Real]
}

final case class PathOperation(
  path: String,
  method: HttpMethod,
  operation: Operation,
  pathAdjusters: List[PathItemAdjuster],
)

sealed trait OpenApiMethod[T] extends TypedMetadata[T] {
  @reifyName(useRawName = true) def name: String
  @reifyAnnot def methodTag: RestMethodTag
  @multi @rpcParamMetadata @tagged[NonBodyTag] @allowOptional def parameters: List[OpenApiParameter[_]]
  @multi @reifyAnnot def operationAdjusters: List[OperationAdjuster]
  @multi @reifyAnnot def pathAdjusters: List[PathItemAdjuster]
  @optional @reifyAnnot def groupAnnot: Opt[group]
  @multi @reifyAnnot def tagAdjusters: List[TagAdjuster]

  val pathPattern: String = {
    val pathParts = methodTag.path :: parameters.flatMap {
      case OpenApiParameter(path: Path, info, _) =>
        s"{${info.name}}" :: path.pathSuffix :: Nil
      case _ => Nil
    }
    pathParts.iterator.map(_.stripPrefix("/").stripSuffix("/")).filter(_.nonEmpty).mkString("/", "/", "")
  }
}

final case class OpenApiPrefix[T](
  name: String,
  methodTag: Prefix,
  parameters: List[OpenApiParameter[_]],
  operationAdjusters: List[OperationAdjuster],
  pathAdjusters: List[PathItemAdjuster],
  groupAnnot: Opt[group],
  tagAdjusters: List[TagAdjuster],
  @optional @reifyAnnot operationIdPrefix: Opt[operationIdPrefix],
  @infer @checked result: OpenApiMetadata.Lazy[T],
) extends OpenApiMethod[T] {

  def operations(resolver: SchemaResolver): Iterator[PathOperation] = {
    val prefixParams = parameters.map(_.parameter(resolver))
    val oidPrefix = operationIdPrefix.fold(s"${name}_")(_.prefix)
    result.value.operations(resolver).map { case PathOperation(path, httpMethod, operation, subAdjusters) =>
      val prefixedOperation = operation.copy(
        operationId = operation.operationId.toOpt.map(oid => s"$oidPrefix$oid").toOptArg,
        parameters = prefixParams ++ operation.parameters
      )
      val adjustedOperation = operationAdjusters.foldRight(prefixedOperation)(_ adjustOperation _)
      val newPath = if (path == "/") pathPattern else if (pathPattern == "/") path else pathPattern + path
      PathOperation(newPath, httpMethod, adjustedOperation, pathAdjusters ++ subAdjusters)
    }
  }
}

sealed trait OpenApiOperation[T] extends OpenApiMethod[T] {
  @infer @checked def resultType: RestResultType[T]
  def methodTag: HttpMethodTag
  def requestBody(resolver: SchemaResolver): Opt[RefOr[RequestBody]]

  def operation(resolver: SchemaResolver): Operation = {
    val op = Operation(
      responses = resultType.responses(resolver),
      operationId = name,
      parameters = parameters.map(_.parameter(resolver)),
      requestBody = requestBody(resolver).toOptArg
    )
    operationAdjusters.foldRight(op)(_ adjustOperation _)
  }

  def pathOperation(resolver: SchemaResolver): PathOperation =
    PathOperation(pathPattern, methodTag.method, operation(resolver), pathAdjusters)
}

final case class OpenApiGetOperation[T](
  name: String,
  methodTag: HttpMethodTag,
  operationAdjusters: List[OperationAdjuster],
  pathAdjusters: List[PathItemAdjuster],
  groupAnnot: Opt[group],
  tagAdjusters: List[TagAdjuster],
  parameters: List[OpenApiParameter[_]],
  resultType: RestResultType[T],
) extends OpenApiOperation[T] {
  def requestBody(resolver: SchemaResolver): Opt[RefOr[RequestBody]] = Opt.Empty
}

final case class OpenApiCustomBodyOperation[T](
  name: String,
  methodTag: HttpMethodTag,
  operationAdjusters: List[OperationAdjuster],
  pathAdjusters: List[PathItemAdjuster],
  groupAnnot: Opt[group],
  tagAdjusters: List[TagAdjuster],
  parameters: List[OpenApiParameter[_]],
  @encoded @rpcParamMetadata @tagged[Body] @unmatched(RawRest.MissingBodyParam) singleBody: OpenApiBody[_],
  resultType: RestResultType[T]
) extends OpenApiOperation[T] {
  def requestBody(resolver: SchemaResolver): Opt[RefOr[RequestBody]] =
    singleBody.requestBody(resolver)
}

final case class OpenApiBodyOperation[T](
  name: String,
  methodTag: HttpMethodTag,
  operationAdjusters: List[OperationAdjuster],
  pathAdjusters: List[PathItemAdjuster],
  groupAnnot: Opt[group],
  tagAdjusters: List[TagAdjuster],
  parameters: List[OpenApiParameter[_]],
  @multi @rpcParamMetadata @tagged[Body] @allowOptional bodyFields: List[OpenApiBodyField[_]],
  @reifyAnnot bodyTypeTag: BodyTypeTag,
  resultType: RestResultType[T],
) extends OpenApiOperation[T] {

  def requestBody(resolver: SchemaResolver): Opt[RefOr[RequestBody]] =
    if (bodyFields.isEmpty) Opt.Empty else {
      val fields = bodyFields.iterator.map(p => (p.info.name, p.schema(resolver))).toList
      val requiredFields = bodyFields.collect { case p if !p.info.isOptional => p.info.name }
      val schema = Schema(`type` = DataType.Object, properties = IListMap(fields: _*), required = requiredFields)
      val mediaType = bodyTypeTag match {
        case _: JsonBody => HttpBody.JsonType
        case _: FormBody => HttpBody.FormType
        case _: CustomBody | _: NoBody =>
          throw new IllegalArgumentException(s"Unexpected body type $bodyTypeTag")
      }
      RestRequestBody.simpleRequestBody(mediaType, RefOr(schema), requiredFields.nonEmpty)
    }
}

final case class OpenApiParamInfo[T](
  @reifyName(useRawName = true) name: String,
  @optional @composite whenAbsentInfo: Opt[WhenAbsentInfo[T]],
  @isAnnotated[optionalParam] optional: Boolean,
  @reifyFlags flags: ParamFlags,
  @infer restSchema: RestSchema[T],
) extends TypedMetadata[T] {
  val whenAbsentValue: Opt[JsonValue] =
    if (optional) Opt.Empty
    else whenAbsentInfo.flatMap(_.fallbackValue)

  val isOptional: Boolean = optional || flags.hasDefaultValue || whenAbsentValue.isDefined

  @bincompat private[rest] def hasFallbackValue: Boolean = isOptional

  def schema(resolver: SchemaResolver, withDefaultValue: Boolean): RefOr[Schema] =
    resolver.resolve(restSchema) |> (s => if (withDefaultValue) s.withDefaultValue(whenAbsentValue) else s)

  @bincompat private[rest] def this(
    name: String, whenAbsentInfo: Opt[WhenAbsentInfo[T]], flags: ParamFlags, restSchema: RestSchema[T]
  ) = this(name, whenAbsentInfo, optional = false, flags, restSchema)
}

final case class OpenApiParameter[T](
  @reifyAnnot paramTag: NonBodyTag,
  @composite info: OpenApiParamInfo[T],
  @multi @reifyAnnot adjusters: List[ParameterAdjuster],
) extends TypedMetadata[T] {

  def parameter(resolver: SchemaResolver): RefOr[Parameter] = {
    val in = paramTag match {
      case _: Path => Location.Path
      case _: HeaderAnnot => Location.Header
      case _: Query => Location.Query
      case _: Cookie => Location.Cookie
    }
    val pathParam = in == Location.Path
    val urlEncodeName = in == Location.Query || in == Location.Cookie
    val name = if(urlEncodeName) URLEncoder.encode(info.name, spaceAsPlus = true) else info.name
    val param = Parameter(name, in,
      required = pathParam || !info.isOptional,
      schema = info.schema(resolver, withDefaultValue = !pathParam),
      // repeated query/cookie/header params are not supported for now so ensure `explode` is never assumed to be true
      explode = if (in.defaultStyle.explodeByDefault) OptArg(false) else OptArg.Empty
    )
    RefOr(adjusters.foldRight(param)(_ adjustParameter _))
  }
}

final case class OpenApiBodyField[T](
  @composite info: OpenApiParamInfo[T],
  @multi @reifyAnnot schemaAdjusters: List[SchemaAdjuster],
) extends TypedMetadata[T] {
  def schema(resolver: SchemaResolver): RefOr[Schema] =
    SchemaAdjuster.adjustRef(schemaAdjusters, info.schema(resolver, withDefaultValue = true))
}

final case class OpenApiBody[T](
  @infer restRequestBody: RestRequestBody[T],
  @multi @reifyAnnot schemaAdjusters: List[SchemaAdjuster],
) extends TypedMetadata[T] {
  def requestBody(resolver: SchemaResolver): Opt[RefOr[RequestBody]] = {
    def transformSchema(schema: RestSchema[_]): RestSchema[_] =
      RestSchema.create(resolver => SchemaAdjuster.adjustRef(schemaAdjusters, resolver.resolve(schema)))
    restRequestBody.requestBody(resolver, transformSchema)
  }
}
