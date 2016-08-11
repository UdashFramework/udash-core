package io.udash.properties

import io.udash.utils.Registration

import scala.collection.mutable

class PropertyRegistration[ElementType](s: mutable.Set[ElementType], el: ElementType) extends Registration {
  override def cancel(): Unit =
    s -= el
}