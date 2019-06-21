package io.udash.properties

trait PropertyCreatorImplicits { this: PropertyCreator.type =>
  implicit final def materializeSingle[T]: PropertyCreator[T] = new SinglePropertyCreator[T]
}