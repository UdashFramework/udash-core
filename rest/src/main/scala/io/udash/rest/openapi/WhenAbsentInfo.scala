package io.udash
package rest.openapi

import com.avsystem.commons._
import com.avsystem.commons.meta.{TypedMetadata, infer, reifyAnnot}
import com.avsystem.commons.rpc.AsRaw
import com.avsystem.commons.serialization.whenAbsent
import io.udash.rest.JsonValue

import scala.util.Try

case class WhenAbsentInfo[T](
  @reifyAnnot annot: whenAbsent[T],
  @infer("for @whenAbsent value: ") asJson: AsRaw[JsonValue, T]
) extends TypedMetadata[T] {
  val fallbackValue: Opt[JsonValue] =
    Try(annot.value).fold(_ => Opt.Empty, v => asJson.asRaw(v).opt)
}
