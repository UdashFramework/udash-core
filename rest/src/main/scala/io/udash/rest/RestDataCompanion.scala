package io.udash
package rest

import com.avsystem.commons.meta.MacroInstances
import com.avsystem.commons.serialization.GenCodec
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
  implicit lazy val restSchema: RestSchema[T] = RestSchema.lazySchema(restStructure.restSchema)
}
