package io.udash
package rest.openapi

import com.avsystem.commons._
import com.avsystem.commons.misc.{AbstractValueEnum, AbstractValueEnumCompanion, EnumCtx}
import com.avsystem.commons.serialization.GenCodec.ReadFailure
import com.avsystem.commons.serialization.json.{JsonStringOutput, JsonType}
import com.avsystem.commons.serialization.{transientDefault => td, _}
import io.udash.rest.raw._

/**
  * Representation of
  * [[https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#openapi-object OpenAPI Object]]
  * from [[https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md OpenAPI 3.0 specification]].
  * It may be serialized to OpenAPI 3.0 compliant JSON using `JsonStringOutput`.
  * This JSON can then be consumed by tools that support OpenAPI 3.0, e.g.
  * [[https://swagger.io/tools/swagger-ui/ Swagger UI]].
  */
final case class OpenApi(
  openapi: String = OpenApi.Version,
  info: Info,
  paths: Paths,
  @td servers: List[Server] = Nil,
  @td components: OptArg[Components] = OptArg.Empty,
  @td security: List[SecurityRequirement] = Nil,
  @td tags: List[Tag] = Nil,
  @td externalDocs: OptArg[ExternalDocumentation] = OptArg.Empty
)
object OpenApi extends HasGenObjectCodec[OpenApi] {
  final val Version = "3.0.2"
}

/**
  * Representation of
  * [[https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#infoObject Info Object]]
  */
final case class Info(
  title: String,
  version: String,
  @td license: OptArg[License] = OptArg.Empty,
  @td description: OptArg[String] = OptArg.Empty,
  @td termsOfService: OptArg[String] = OptArg.Empty,
  @td contact: OptArg[Contact] = OptArg.Empty
)
object Info extends HasGenObjectCodec[Info]

/**
  * Representation of
  * [[https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#contactObject Contact Object]]
  */
final case class Contact(
  @td name: OptArg[String] = OptArg.Empty,
  @td url: OptArg[String] = OptArg.Empty,
  @td email: OptArg[String] = OptArg.Empty
)
object Contact extends HasGenObjectCodec[Contact]

/**
  * Representation of
  * [[https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#licenseObject License Object]]
  */
final case class License(
  name: String,
  @td url: OptArg[String] = OptArg.Empty
)
object License extends HasGenObjectCodec[License]

/**
  * Representation of
  * [[https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#serverObject Server Object]]
  */
final case class Server(
  url: String,
  @td description: OptArg[String] = OptArg.Empty,
  @td serverVariables: Map[String, ServerVariable] = Map.empty
)
object Server extends HasGenObjectCodec[Server]

/**
  * Representation of
  * [[https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#serverVariableObject Server Variable Object]]
  */
final case class ServerVariable(
  default: String,
  @td enum: List[String] = Nil,
  @td description: OptArg[String] = OptArg.Empty
)
object ServerVariable extends HasGenObjectCodec[ServerVariable]

/**
  * Representation of
  * [[https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#pathsObject Paths Object]]
  */
@transparent final case class Paths(paths: Map[String, RefOr[PathItem]])
object Paths extends HasGenObjectCodec[Paths]

/**
  * Representation of
  * [[https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#pathItemObject Path Item Object]]
  */
final case class PathItem(
  @td summary: OptArg[String] = OptArg.Empty,
  @td description: OptArg[String] = OptArg.Empty,
  @td get: OptArg[Operation] = OptArg.Empty,
  @td put: OptArg[Operation] = OptArg.Empty,
  @td post: OptArg[Operation] = OptArg.Empty,
  @td delete: OptArg[Operation] = OptArg.Empty,
  @td options: OptArg[Operation] = OptArg.Empty,
  @td head: OptArg[Operation] = OptArg.Empty,
  @td patch: OptArg[Operation] = OptArg.Empty,
  @td trace: OptArg[Operation] = OptArg.Empty,
  @td servers: List[Server] = Nil,
  @td parameters: List[RefOr[Parameter]] = Nil
)
object PathItem extends HasGenObjectCodec[PathItem]

/**
  * Representation of
  * [[https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#operationObject Operation Object]]
  */
final case class Operation(
  @td tags: List[String] = Nil,
  @td summary: OptArg[String] = OptArg.Empty,
  @td description: OptArg[String] = OptArg.Empty,
  @td externalDocs: OptArg[ExternalDocumentation] = OptArg.Empty,
  @td operationId: OptArg[String] = OptArg.Empty,
  @td parameters: List[RefOr[Parameter]] = Nil,
  @td requestBody: OptArg[RefOr[RequestBody]] = OptArg.Empty,
  responses: Responses,
  @td callbacks: Map[String, RefOr[Callback]] = Map.empty,
  @td deprecated: Boolean = false,
  @td security: List[SecurityRequirement] = Nil,
  @td servers: List[Server] = Nil
)
object Operation extends HasGenObjectCodec[Operation]

/**
  * Representation of
  * [[https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#responsesObject Responses Object]]
  */
final case class Responses(
  byStatusCode: Map[Int, RefOr[Response]] = Map.empty,
  default: OptArg[RefOr[Response]] = OptArg.Empty
)
object Responses {
  final val DefaultField = "default"

  implicit val codec: GenObjectCodec[Responses] = GenCodec.nullableObject(
    oi => {
      var default = OptArg.empty[RefOr[Response]]
      val byStatusCode = Map.newBuilder[Int, RefOr[Response]]
      while (oi.hasNext) {
        val fi = oi.nextField()
        fi.fieldName match {
          case DefaultField =>
            default = GenCodec.read[RefOr[Response]](fi)
          case status =>
            byStatusCode += ((status.toInt, GenCodec.read[RefOr[Response]](fi)))
        }
      }
      Responses(byStatusCode.result(), default)
    },
    (oo, v) => {
      v.default.foreach(resp => GenCodec.write[RefOr[Response]](oo.writeField(DefaultField), resp))
      v.byStatusCode.foreach {
        case (status, resp) =>
          GenCodec.write[RefOr[Response]](oo.writeField(status.toString), resp)
      }
    }
  )
}

/**
  * Representation of
  * [[https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#componentsObject Components Object]]
  */
final case class Components(
  @td schemas: Map[String, RefOr[Schema]] = Map.empty,
  @td responses: Map[String, RefOr[Response]] = Map.empty,
  @td parameters: Map[String, RefOr[Parameter]] = Map.empty,
  @td examples: Map[String, RefOr[Example]] = Map.empty,
  @td requestBodies: Map[String, RefOr[RequestBody]] = Map.empty,
  @td headers: Map[String, RefOr[Header]] = Map.empty,
  @td securitySchemes: Map[String, RefOr[SecurityScheme]] = Map.empty,
  @td links: Map[String, RefOr[Link]] = Map.empty,
  @td callbacks: Map[String, RefOr[Callback]] = Map.empty
)
object Components extends HasGenObjectCodec[Components]

/**
  * Representation of
  * [[https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#securityRequirementObject Security Requirement Object]]
  */
@transparent final case class SecurityRequirement(schemes: Map[String, List[String]])
object SecurityRequirement extends HasGenObjectCodec[SecurityRequirement]

/**
  * Representation of
  * [[https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#tagObject Tag Object]]
  */
final case class Tag(
  name: String,
  @td description: OptArg[String] = OptArg.Empty,
  @td externalDocs: OptArg[ExternalDocumentation] = OptArg.Empty
)
object Tag extends HasGenObjectCodec[Tag]

/**
  * Representation of
  * [[https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#externalDocumentationObject External Documentation Object]]
  */
final case class ExternalDocumentation(
  url: String,
  @td description: OptArg[String] = OptArg.Empty
)
object ExternalDocumentation extends HasGenObjectCodec[ExternalDocumentation]

/**
  * Representation of
  * [[https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#schemaObject Schema Object]]
  */
final case class Schema(
  @td `type`: OptArg[DataType] = OptArg.Empty,
  @td format: OptArg[String] = OptArg.Empty,
  @td title: OptArg[String] = OptArg.Empty,
  @td description: OptArg[String] = OptArg.Empty,
  @td nullable: Boolean = false,
  @td readOnly: Boolean = false,
  @td writeOnly: Boolean = false,
  @td xml: OptArg[Xml] = OptArg.Empty,
  @td externalDocs: OptArg[ExternalDocumentation] = OptArg.Empty,
  @td deprecated: Boolean = false,

  @td multipleOf: OptArg[BigDecimal] = OptArg.Empty,
  @td maximum: OptArg[BigDecimal] = OptArg.Empty,
  @td exclusiveMaximum: Boolean = false,
  @td minimum: OptArg[BigDecimal] = OptArg.Empty,
  @td exclusiveMinimum: Boolean = false,

  @td maxLength: OptArg[Int] = OptArg.Empty,
  @td minLength: OptArg[Int] = OptArg.Empty,
  @td pattern: OptArg[String] = OptArg.Empty,

  @td items: OptArg[RefOr[Schema]] = OptArg.Empty,
  @td maxItems: OptArg[Int] = OptArg.Empty,
  @td minItems: OptArg[Int] = OptArg.Empty,
  @td uniqueItems: Boolean = false,

  @td properties: Map[String, RefOr[Schema]] = Map.empty,
  @td additionalProperties: AdditionalProperties = AdditionalProperties.Flag(true),
  @td maxProperties: OptArg[Int] = OptArg.Empty,
  @td minProperties: OptArg[Int] = OptArg.Empty,
  @td required: List[String] = Nil,

  @td allOf: List[RefOr[Schema]] = Nil,
  @td oneOf: List[RefOr[Schema]] = Nil,
  @td anyOf: List[RefOr[Schema]] = Nil,
  @td not: OptArg[RefOr[Schema]] = OptArg.Empty,
  @td discriminator: OptArg[Discriminator] = OptArg.Empty,

  @td enum: List[JsonValue] = Nil,
  @td default: OptArg[JsonValue] = OptArg.Empty,
  @td example: OptArg[JsonValue] = OptArg.Empty
)
object Schema extends HasGenObjectCodec[Schema] {
  final val Boolean = Schema(`type` = DataType.Boolean)
  final val Char = Schema(`type` = DataType.String, minLength = 1, maxLength = 1)
  final val Byte = Schema(`type` = DataType.Integer, format = Format.Int32,
    minimum = BigDecimal(scala.Byte.MinValue), maximum = BigDecimal(scala.Byte.MaxValue))
  final val Short = Schema(`type` = DataType.Integer, format = Format.Int32,
    minimum = BigDecimal(scala.Short.MinValue), maximum = BigDecimal(scala.Short.MaxValue))
  final val Int = Schema(`type` = DataType.Integer, format = Format.Int32)
  final val Long = Schema(`type` = DataType.Integer, format = Format.Int64)
  final val Float = Schema(`type` = DataType.Number, format = Format.Float)
  final val Double = Schema(`type` = DataType.Number, format = Format.Double)
  final val Integer = Schema(`type` = DataType.Integer)
  final val Number = Schema(`type` = DataType.Number)
  final val String = Schema(`type` = DataType.String)
  final val Date = Schema(`type` = DataType.String, format = Format.Date)
  final val DateTime = Schema(`type` = DataType.String, format = Format.DateTime)
  final val Uuid = Schema(`type` = DataType.String, format = Format.Uuid)
  final val Password = Schema(`type` = DataType.String, format = Format.Password)
  final val Binary = Schema(`type` = DataType.String, format = Format.Binary)
  final val Email = Schema(`type` = DataType.String, format = Format.Email)

  def arrayOf(items: RefOr[Schema], uniqueItems: Boolean = false): Schema =
    Schema(`type` = DataType.Array, items = items, uniqueItems = uniqueItems)

  def mapOf(properties: RefOr[Schema]): Schema =
    Schema(`type` = DataType.Object, additionalProperties = AdditionalProperties.SchemaObj(properties))

  def enumOf(values: List[String]): Schema =
    Schema(`type` = DataType.String, enum = values.map(s => JsonValue(JsonStringOutput.write(s))))

  def enumMapOf(keys: List[String], value: RefOr[Schema]): Schema =
    Schema(`type` = DataType.Object,
      properties = keys.iterator.map(k => (k, value)).toMap,
      additionalProperties = AdditionalProperties.Flag(false)
    )

  def nullable(schema: RefOr[Schema]): Schema =
    schema.rewrapRefToAllOf.copy(nullable = true)

  implicit class RefOrOps(private val refOrSchema: RefOr[Schema]) extends AnyVal {
    /**
      * Transforms a potential schema reference into an actual [[Schema]] by wrapping the reference into
      * `allOf` property of the new schema, e.g. `{"$$ref": "#/components/schemas/Entity"}` becomes
      * `{"allOf": [{"$$ref": "#/components/schemas/Entity"}]}`.
      */
    def rewrapRefToAllOf: Schema = refOrSchema match {
      case RefOr.Value(schema) => schema
      case ref => Schema(allOf = List(ref))
    }

    def withDefaultValue(dv: Opt[JsonValue]): RefOr[Schema] =
      dv.fold(refOrSchema)(v => map(_.copy(default = v)))

    def map(f: Schema => Schema): RefOr[Schema] = refOrSchema match {
      case RefOr.Value(schema) => RefOr(f(schema))
      case ref: RefOr.Ref =>
        val wrapped = Schema(allOf = List(ref))
        val mapped = f(wrapped)
        if (mapped == wrapped) ref else RefOr(mapped)
    }
  }
}

sealed trait AdditionalProperties
object AdditionalProperties {
  final case class Flag(value: Boolean) extends AdditionalProperties
  final case class SchemaObj(schema: RefOr[Schema]) extends AdditionalProperties

  private val escapedCodec: GenCodec[AdditionalProperties] = GenCodec.materialize

  implicit val codec: GenCodec[AdditionalProperties] = GenCodec.create(
    input => input.readMetadata(JsonType).fold(escapedCodec.read(input)) {
      case JsonType.`object` => SchemaObj(GenCodec.read[RefOr[Schema]](input))
      case JsonType.boolean => Flag(input.readSimple().readBoolean())
      case t => throw new ReadFailure(s"expected JSON object or boolean, got $t")
    },
    (output, value) =>
      if (!output.keepsMetadata(JsonType)) escapedCodec.write(output, value)
      else value match {
        case Flag(flag) => output.writeSimple().writeBoolean(flag)
        case SchemaObj(schema) => GenCodec.write[RefOr[Schema]](output, schema)
      }
  )
}

object Format {
  final val Int32 = "int32"
  final val Int64 = "int64"
  final val Float = "float"
  final val Double = "double"
  final val Byte = "byte"
  final val Binary = "binary"
  final val Date = "date"
  final val DateTime = "date-time"
  final val Password = "password"
  final val Email = "email"
  final val Uuid = "uuid"
}

final class DataType(implicit enumCtx: EnumCtx) extends AbstractValueEnum {
  override val name: String = enumCtx.valName.uncapitalize
}
object DataType extends AbstractValueEnumCompanion[DataType] {
  final val String, Number, Integer, Boolean, Array, Object: Value = new DataType
}

/**
  * Representation of
  * [[https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#discriminatorObject Discriminator Object]]
  */
final case class Discriminator(
  propertyName: String,
  @td mapping: Map[String, String] = Map.empty
)
object Discriminator extends HasGenObjectCodec[Discriminator]

/**
  * Representation of
  * [[https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#xmlObject Xml Object]]
  */
final case class Xml(
  @td name: OptArg[String] = OptArg.Empty,
  @td namespace: OptArg[String] = OptArg.Empty,
  @td prefix: OptArg[String] = OptArg.Empty,
  @td attribute: Boolean = false,
  @td wrapped: Boolean = false
)
object Xml extends HasGenObjectCodec[Xml]

/**
  * Representation of
  * [[https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#responseObject Response Object]]
  */
final case class Response(
  description: String,
  @td headers: Map[String, RefOr[Header]] = Map.empty,
  @td content: Map[String, MediaType] = Map.empty,
  @td links: Map[String, RefOr[Link]] = Map.empty
)
object Response extends HasGenObjectCodec[Response]

/**
  * Representation of
  * [[https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#parameterObject Parameter Object]]
  */
final case class Parameter(
  name: String,
  in: Location,
  @td description: OptArg[String] = OptArg.Empty,
  @td required: Boolean = false,
  @td deprecated: Boolean = false,
  @td allowEmptyValue: Boolean = false,
  @td style: OptArg[Style] = OptArg.Empty,
  @td explode: OptArg[Boolean] = OptArg.Empty,
  @td allowReserved: Boolean = false,
  @td schema: OptArg[RefOr[Schema]] = OptArg.Empty,
  @td example: OptArg[JsonValue] = OptArg.Empty,
  @td examples: Map[String, RefOr[Example]] = Map.empty,
  @td content: OptArg[Entry[String, MediaType]] = OptArg.Empty
)
object Parameter extends HasGenObjectCodec[Parameter]

final case class Entry[K, V](key: K, value: V)
object Entry {
  implicit def codec[K: GenKeyCodec, V: GenCodec]: GenObjectCodec[Entry[K, V]] =
    GenCodec.nullableObject(
      oi => {
        val fi = oi.nextField()
        Entry(GenKeyCodec.read[K](fi.fieldName), GenCodec.read[V](fi))
      },
      (oo, entry) =>
        GenCodec.write[V](oo.writeField(GenKeyCodec.write[K](entry.key)), entry.value)
    )
}

final class Location(implicit enumCtx: EnumCtx) extends AbstractValueEnum {
  override val name: String = enumCtx.valName.uncapitalize

  def defaultStyle: Style = this match {
    case Location.Query | Location.Cookie => Style.Form
    case Location.Path | Location.Header => Style.Simple
  }
}
object Location extends AbstractValueEnumCompanion[Location] {
  final val Query, Header, Path, Cookie: Value = new Location
}

/**
  * Representation of
  * [[https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#style-values parameter style]]
  */
final class Style(implicit enumCtx: EnumCtx) extends AbstractValueEnum {
  override val name: String = enumCtx.valName.uncapitalize

  def explodeByDefault: Boolean = this == Style.Form
}
object Style extends AbstractValueEnumCompanion[Style] {
  final val Matrix, Label, Form, Simple, SpaceDelimited, PipeDelimited, DeepObject: Value = new Style
}

/**
  * Representation of
  * [[https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#mediaTypeObject Media Type Object]]
  */
final case class MediaType(
  @td schema: OptArg[RefOr[Schema]] = OptArg.Empty,
  @td example: OptArg[JsonValue] = OptArg.Empty,
  @td examples: Map[String, RefOr[Example]] = Map.empty,
  @td encoding: Map[String, Encoding] = Map.empty
)
object MediaType extends HasGenObjectCodec[MediaType]

/**
  * Representation of
  * [[https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#encodingObject Encoding Object]]
  */
final case class Encoding(
  @td contentType: OptArg[String] = OptArg.Empty,
  @td headers: Map[String, RefOr[Header]] = Map.empty,
  @td style: OptArg[Style] = OptArg.Empty,
  @td explode: OptArg[Boolean] = OptArg.Empty,
  @td allowReserved: Boolean = false
)
object Encoding extends HasGenObjectCodec[Encoding]

/**
  * Representation of
  * [[https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#exampleObject Example Object]]
  */
final case class Example(
  @td summary: OptArg[String] = OptArg.Empty,
  @td description: OptArg[String] = OptArg.Empty,
  @td value: OptArg[JsonValue] = OptArg.Empty,
  @td externalValue: OptArg[String] = OptArg.Empty
)
object Example extends HasGenObjectCodec[Example]

/**
  * Representation of
  * [[https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#requestBodyObject Request Body Object]]
  */
final case class RequestBody(
  @td description: OptArg[String] = OptArg.Empty,
  content: Map[String, MediaType],
  @td required: Boolean = false
)
object RequestBody extends HasGenObjectCodec[RequestBody]

/**
  * Representation of
  * [[https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#headerObject Header Object]]
  */
final case class Header(
  @td description: OptArg[String] = OptArg.Empty,
  @td required: Boolean = false,
  @td deprecated: Boolean = false,
  @td allowEmptyValue: Boolean = false,
  @td style: OptArg[Style] = OptArg.Empty,
  @td explode: OptArg[Boolean] = OptArg.Empty,
  @td allowReserved: Boolean = false,
  @td schema: OptArg[RefOr[Schema]] = OptArg.Empty,
  @td example: OptArg[JsonValue] = OptArg.Empty,
  @td examples: Map[String, RefOr[Example]] = Map.empty,
  @td content: OptArg[Entry[String, MediaType]] = OptArg.Empty
)
object Header extends HasGenObjectCodec[Header]

/**
  * Representation of
  * [[https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#securitySchemeObject Security Scheme Object]]
  */
@flatten("type") sealed trait SecurityScheme {
  def description: OptArg[String]
}
object SecurityScheme {
  @name("apiKey") final case class ApiKey(
    name: String,
    in: Location,
    @td description: OptArg[String] = OptArg.Empty
  ) extends SecurityScheme

  @name("http") final case class Http(
    scheme: String,
    @td bearerFormat: OptArg[String] = OptArg.Empty,
    @td description: OptArg[String] = OptArg.Empty
  ) extends SecurityScheme

  @name("oauth2") final case class OAuth2(
    flows: OAuthFlows,
    @td description: OptArg[String] = OptArg.Empty
  ) extends SecurityScheme

  @name("openIdConnect") final case class OpenIdConnect(
    openIdConnectUrl: String,
    @td description: OptArg[String] = OptArg.Empty
  ) extends SecurityScheme

  implicit val codec: GenObjectCodec[SecurityScheme] = GenObjectCodec.materialize
}

/**
  * Representation of
  * [[https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#oauthFlowsObject OAuth Flows Object]]
  */
final case class OAuthFlows(
  @td `implicit`: OptArg[OAuthFlow] = OptArg.Empty,
  @td password: OptArg[OAuthFlow] = OptArg.Empty,
  @td clientCredentials: OptArg[OAuthFlow] = OptArg.Empty,
  @td authorizationCode: OptArg[OAuthFlow] = OptArg.Empty
)
object OAuthFlows extends HasGenObjectCodec[OAuthFlows]

/**
  * Representation of
  * [[https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#oauthFlowObject OAuth Flow Object]]
  */
final case class OAuthFlow(
  scopes: Map[String, String],
  @td authorizationUrl: OptArg[String] = OptArg.Empty,
  @td tokenUrl: OptArg[String] = OptArg.Empty,
  @td refreshUrl: OptArg[String] = OptArg.Empty
)
object OAuthFlow extends HasGenObjectCodec[OAuthFlow]

/**
  * Representation of
  * [[https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#linkObject Link Object]]
  */
final case class Link(
  @td operationRef: OptArg[String] = OptArg.Empty,
  @td operationId: OptArg[String] = OptArg.Empty,
  @td parameters: Map[String, JsonValue] = Map.empty,
  @td requestBody: OptArg[JsonValue] = OptArg.Empty,
  @td description: OptArg[String] = OptArg.Empty,
  @td server: OptArg[Server] = OptArg.Empty
)
object Link extends HasGenObjectCodec[Link]

/**
  * Representation of
  * [[https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#callbackObject Callback Object]]
  */
@transparent final case class Callback(byExpression: Map[String, PathItem])
object Callback extends HasGenObjectCodec[Callback]

/**
  * Represents a value which is either some directly available, inlined value (usually one of the OpenAPI objects,
  * e.g. [[Schema]], [[Parameter]], [[Operation]], etc.) or a
  * [[https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#callbackObject Reference Object]].
  */
sealed trait RefOr[+A]
object RefOr {
  final case class Ref(ref: String) extends RefOr[Nothing]
  final case class Value[+A](value: A) extends RefOr[A]

  final val RefField = "$ref"

  def apply[A](value: A): RefOr[A] = Value(value)
  def ref[A](ref: String): RefOr[A] = Ref(ref)

  implicit def codec[A: GenObjectCodec]: GenObjectCodec[RefOr[A]] =
    GenCodec.nullableObject(
      oi => {
        val poi = new PeekingObjectInput(oi)
        val refFieldInput = poi.peekField(RefField).orElse {
          if (poi.peekNextFieldName.contains(RefField)) poi.nextField().opt
          else Opt.Empty
        }
        val res = refFieldInput.map(fi => Ref(fi.readSimple().readString()))
          .getOrElse(Value(GenObjectCodec.readObject[A](poi)))
        poi.skipRemaining()
        res
      },
      (oo, value) => value match {
        case Ref(refstr) => oo.writeField(RefField).writeSimple().writeString(refstr)
        case Value(v) => GenObjectCodec.writeObject[A](oo, v)
      }
    )
}
