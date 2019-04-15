package io.udash.properties

trait PropertyCreatorImplicitsLow { this: PropertyCreatorImplicits =>
  implicit def materializeSingle[T]: PropertyCreator[T] = new SinglePropertyCreator[T]
}

trait PropertyCreatorImplicits extends PropertyCreatorImplicitsLow { this: PropertyCreator.type =>
  implicit def materializeSeq[T: PropertyCreator]: SeqPropertyCreator[T] = new SeqPropertyCreator[T]
}