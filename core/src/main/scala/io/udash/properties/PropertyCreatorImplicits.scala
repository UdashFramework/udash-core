package io.udash.properties

trait PropertyCreatorImplicits { this: PropertyCreator.type =>
  implicit def materializeSingle[T]: PropertyCreator[T] = new SinglePropertyCreator[T]
}