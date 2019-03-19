package io.udash
package rest

import com.avsystem.commons.meta.MacroInstances
import com.avsystem.commons.misc.AnnotationsOf
import com.avsystem.commons.serialization.{GenCodec, SerializationName, TransparentWrapperCompanion}
import io.udash.rest.openapi.adjusters.SchemaAdjuster
import io.udash.rest.openapi.{RestSchema, RestStructure}

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
  * This companion ensures instances of [[com.avsystem.commons.serialization.GenCodec GenCodec]],
  * [[com.avsystem.commons.serialization.GenKeyCodec GenKeyCodec]] and [[io.udash.rest.openapi.RestSchema RestSchema]],
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
  sname: SerializationName[T],
  adjusters: AnnotationsOf[SchemaAdjuster, T]
) extends TransparentWrapperCompanion[Wrapped, T] {
  implicit def restSchema(implicit wrappedSchema: RestSchema[Wrapped]): RestSchema[T] =
    RestSchema.create(r => SchemaAdjuster.adjustRef(adjusters.annots, r.resolve(wrappedSchema)), sname.name)
}
