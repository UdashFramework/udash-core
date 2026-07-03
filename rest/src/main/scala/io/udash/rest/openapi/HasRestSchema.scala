package io.udash.rest.openapi

trait HasRestSchema[T] {
  def restSchema: RestSchema[T]
}
object HasRestSchema {
  def apply[T: RestSchema]: HasRestSchema[T] =
    new HasRestSchema[T] {
      override def restSchema: RestSchema[T] = implicitly[RestSchema[T]]
    }
}
