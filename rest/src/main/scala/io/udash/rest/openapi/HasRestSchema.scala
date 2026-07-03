package io.udash.rest.openapi

/**
 * Minimal capability trait for something that provides a [[RestSchema]] for type `T`.
 */
trait HasRestSchema[T] {
  def restSchema: RestSchema[T]
}
object HasRestSchema {
  def apply[T: RestSchema]: HasRestSchema[T] =
    new HasRestSchema[T] {
      override def restSchema: RestSchema[T] = implicitly[RestSchema[T]]
    }
}
