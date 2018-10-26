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
  /**
    * Use this constructor and pass `ModelPropertyCreator.materialize` and `GenCodec.materialize` explicitly
    * if you're getting the "super constructor cannot be passed a self reference unless parameter is declared by-name" error.
    */
  def this(explicitCreator: => ModelPropertyCreator[T], explicitCodec: => GenCodec[T]) =
    this()(new MacroInstances[Unit, GenCodecAndModelPropertyCreator[T]] {
      def apply(implicits: Unit, companion: Any): GenCodecAndModelPropertyCreator[T] =
        new GenCodecAndModelPropertyCreator[T] {
          def codec: GenCodec[T] = explicitCodec
          def modelPropertyCreator: ModelPropertyCreator[T] = explicitCreator
        }
    })

  implicit lazy val modelPropertyCreator: ModelPropertyCreator[T] =
    instances((), this).modelPropertyCreator
  implicit lazy val codec: GenCodec[T] =
    instances((), this).codec
}
