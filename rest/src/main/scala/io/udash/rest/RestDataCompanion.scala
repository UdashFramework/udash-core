package io.udash
package rest

import com.avsystem.commons.annotation.explicitGenerics
import com.avsystem.commons.meta.MacroInstances
import com.avsystem.commons.misc.{AbstractValueEnumCompanion, NamedEnum, NamedEnumCompanion, ValueEnum, ValueOf}
import com.avsystem.commons.rpc.{AsRaw, AsReal}
import com.avsystem.commons.serialization.{GenCodec, TransparentWrapperCompanion}
import io.udash.rest.openapi.RestStructure.NameAndAdjusters
import io.udash.rest.openapi._
import io.udash.rest.raw.{HttpBody, JsonValue, PlainValue, RestResponse}

trait CodecWithStructure[T] {
  def codec: GenCodec[T]
  def structure: RestStructure[T]
}

abstract class AbstractRestDataCompanion[Implicits, T](implicits: Implicits)(implicit
  instances: MacroInstances[Implicits, CodecWithStructure[T]]
) {
  implicit lazy val codec: GenCodec[T] = instances(implicits, this).codec
  implicit lazy val restStructure: RestStructure[T] = instances(implicits, this).structure
  implicit lazy val restSchema: RestSchema[T] = RestSchema.lazySchema(restStructure.standaloneSchema)
}

/**
  * Base class for companion objects of ADTs (case classes, objects, sealed hierarchies) which are used as
  * parameter or result types in REST API traits. Automatically provides instances of
  * `GenCodec` and [[io.udash.rest.openapi.RestSchema RestSchema]].
  *
  * @example
  * {{{
  *   case class User(id: String, name: String, birthYear: Int)
  *   object User extends RestDataCompanion[User]
  * }}}
  */
abstract class RestDataCompanion[T](implicit
  instances: MacroInstances[DefaultRestImplicits, CodecWithStructure[T]]
) extends AbstractRestDataCompanion[DefaultRestImplicits, T](DefaultRestImplicits)

/**
  * A version of [[RestDataCompanion]] which injects additional implicits into macro materialization.
  * Implicits are imported from an object specified with type parameter `D`.
  * It must be a singleton object type, i.e. `SomeObject.type`.
  */
abstract class RestDataCompanionWithDeps[D, T](implicit
  deps: ValueOf[D], instances: MacroInstances[(DefaultRestImplicits, D), CodecWithStructure[T]]
) extends AbstractRestDataCompanion[(DefaultRestImplicits, D), T]((DefaultRestImplicits, deps.value))

/**
  * Base class for companion objects of wrappers over other data types (i.e. case classes with single field).
  * This companion ensures instances of all the REST typeclasses (serialization, schema, etc.) for wrapping type
  * assuming that these instances are available for the wrapped type.
  *
  * Using this base companion class makes the wrapper class effectively "transparent", i.e. as if it was annotated with
  * [[com.avsystem.commons.serialization.transparent transparent]] annotation.
  *
  * @example
  * {{{
  *   case class UserId(id: String) extends AnyVal
  *   object UserId extends RestDataWrapperCompanion[String, UserId]
  * }}}
  */
abstract class RestDataWrapperCompanion[Wrapped, T](implicit
  instances: MacroInstances[DefaultRestImplicits, () => NameAndAdjusters[T]]
) extends TransparentWrapperCompanion[Wrapped, T] {
  private def nameAndAdjusters: NameAndAdjusters[T] = instances(DefaultRestImplicits, this).apply()

  // These implicits must be specialized for every raw type (PlainValue, JsonValue, etc.) because
  // it lifts their priority. Unfortunately, controlling implicit priority is not pretty.
  // Also, it's probably good that we explicitly enable derivation only for REST-related raw types
  // and not for all raw types - this avoids possible interference with other features using RPC.

  implicit def plainAsRaw(implicit wrappedAsRaw: AsRaw[PlainValue, Wrapped]): AsRaw[PlainValue, T] =
    AsRaw.fromTransparentWrapping

  implicit def plainAsReal(implicit wrappedAsRaw: AsReal[PlainValue, Wrapped]): AsReal[PlainValue, T] =
    AsReal.fromTransparentWrapping

  implicit def jsonAsRaw(implicit wrappedAsRaw: AsRaw[JsonValue, Wrapped]): AsRaw[JsonValue, T] =
    AsRaw.fromTransparentWrapping

  implicit def jsonAsReal(implicit wrappedAsRaw: AsReal[JsonValue, Wrapped]): AsReal[JsonValue, T] =
    AsReal.fromTransparentWrapping

  implicit def bodyAsRaw(implicit wrappedAsRaw: AsRaw[HttpBody, Wrapped]): AsRaw[HttpBody, T] =
    AsRaw.fromTransparentWrapping

  implicit def bodyAsReal(implicit wrappedAsRaw: AsReal[HttpBody, Wrapped]): AsReal[HttpBody, T] =
    AsReal.fromTransparentWrapping

  implicit def responseAsRaw(implicit wrappedAsRaw: AsRaw[RestResponse, Wrapped]): AsRaw[RestResponse, T] =
    AsRaw.fromTransparentWrapping

  implicit def responseAsReal(implicit wrappedAsRaw: AsReal[RestResponse, Wrapped]): AsReal[RestResponse, T] =
    AsReal.fromTransparentWrapping

  implicit def restSchema(implicit wrappedSchema: RestSchema[Wrapped]): RestSchema[T] =
    nameAndAdjusters.restSchema(wrappedSchema)

  implicit def restMediaTypes(implicit wrappedMediaTypes: RestMediaTypes[Wrapped]): RestMediaTypes[T] =
    (resolver: SchemaResolver, schemaTransform: RestSchema[_] => RestSchema[_]) =>
      wrappedMediaTypes.mediaTypes(resolver, ws => schemaTransform(nameAndAdjusters.restSchema(ws)))

  implicit def restRequestBody(implicit wrappedBody: RestRequestBody[Wrapped]): RestRequestBody[T] =
    (resolver: SchemaResolver, schemaTransform: RestSchema[_] => RestSchema[_]) =>
      wrappedBody.requestBody(resolver, ws => schemaTransform(nameAndAdjusters.restSchema(ws)))

  implicit def restResponses(implicit wrappedResponses: RestResponses[Wrapped]): RestResponses[T] =
    (resolver: SchemaResolver, schemaTransform: RestSchema[_] => RestSchema[_]) =>
      wrappedResponses.responses(resolver, ws => schemaTransform(nameAndAdjusters.restSchema(ws)))
}

/**
  * Base class for companion objects of enum types [[ValueEnum]] which are used as
  * parameter or result types in REST API traits. Automatically provides instance of
  * [[io.udash.rest.openapi.RestSchema RestSchema]].
  */
abstract class RestValueEnumCompanion[E <: ValueEnum](implicit
  instances: MacroInstances[DefaultRestImplicits, () => NameAndAdjusters[E]]
) extends AbstractValueEnumCompanion[E] {
  implicit lazy val restSchema: RestSchema[E] =
    RestValueEnumCompanion.standaloneNamedEnumSchema[E](this, instances(DefaultRestImplicits, this).apply())
}
object RestValueEnumCompanion {
  /**
   * Creates standalone [[io.udash.rest.openapi.RestSchema RestSchema]] for [[NamedEnum]] enum type [[E]]
   * that will be named and used as a reference in OpenAPI model.
   * {{{
   *   "RestEntityEnumExample": {
   *     "type": "string",
   *     "description": "Example named enum",
   *     "enum": [
   *       "OptionOne",
   *       "OptionTwo"
   *     ],
   *     "example": "OptionOne"
   *   }
   * }}}
   *
   * [[io.udash.rest.openapi.RestSchema RestSchema]] instance generated by default for enums is inlined,
   * see [[RestSchema.namedEnumSchema]]
   */
  @explicitGenerics
  def standaloneNamedEnumSchema[E <: NamedEnum : NamedEnumCompanion: NameAndAdjusters]: RestSchema[E] =
    implicitly[NameAndAdjusters[E]].restSchema(RestSchema.namedEnumSchema)
}
