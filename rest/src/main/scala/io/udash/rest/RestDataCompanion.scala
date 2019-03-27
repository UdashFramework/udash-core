package io.udash
package rest

import com.avsystem.commons.meta.MacroInstances
import com.avsystem.commons.misc.Opt
import com.avsystem.commons.rpc.{AsRaw, AsReal}
import com.avsystem.commons.serialization.{GenCodec, TransparentWrapperCompanion}
import io.udash.rest.openapi.RestStructure.NameAndAdjusters
import io.udash.rest.openapi._
import io.udash.rest.raw.{HttpBody, JsonValue, PlainValue, RestResponse}

trait CodecWithStructure[T] {
  def codec: GenCodec[T]
  def structure: RestStructure[T]
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
) extends {
  implicit lazy val codec: GenCodec[T] = instances(DefaultRestImplicits, this).codec
  implicit lazy val restStructure: RestStructure[T] = instances(DefaultRestImplicits, this).structure
  implicit lazy val restSchema: RestSchema[T] = RestSchema.lazySchema(restStructure.standaloneSchema)
}

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

  implicit def restRequestBody(implicit wrappedBody: RestRequestBody[Wrapped]): RestRequestBody[T] =
    new RestRequestBody[T] {
      def requestBody(resolver: SchemaResolver, schemaTransform: RestSchema[T] => RestSchema[_]): Opt[RefOr[RequestBody]] =
        wrappedBody.requestBody(resolver, ws => schemaTransform(nameAndAdjusters.restSchema(ws)))
    }

  implicit def restResponses(implicit wrappedResponses: RestResponses[Wrapped]): RestResponses[T] =
    new RestResponses[T] {
      def responses(resolver: SchemaResolver, schemaTransform: RestSchema[T] => RestSchema[_]): Responses =
        wrappedResponses.responses(resolver, ws => schemaTransform(nameAndAdjusters.restSchema(ws)))
    }
}
