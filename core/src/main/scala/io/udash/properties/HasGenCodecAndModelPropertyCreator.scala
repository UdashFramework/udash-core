package io.udash.properties

import com.avsystem.commons.meta.MacroInstances
import com.avsystem.commons.serialization.GenCodec

trait GenCodecAndModelPropertyCreator[T] {
  def codec: GenCodec[T]
  def modelPropertyCreator: ModelPropertyCreator[T]
}

abstract class HasGenCodecAndModelPropertyCreator[T](implicit
  instances: MacroInstances[Unit, GenCodecAndModelPropertyCreator[T]]
) {
  implicit final lazy val modelPropertyCreator: ModelPropertyCreator[T] = instances((), this).modelPropertyCreator
  implicit final lazy val codec: GenCodec[T] = instances((), this).codec
}
