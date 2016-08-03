package io.udash.properties

import io.udash.utils.Registration

import scala.collection.mutable

trait ImmutableValue[A]
object ImmutableValue {
  // implement with macro that checks if T is really fully immutable, i.e:
  // * it's a simple type like String, Int, etc
  // * it's a case class whose every field is immutable
  // * it's a sealed trait whose case classes are immutable
  // * it's an immutable collection of immutable element types
  implicit val allowIntTpe: ImmutableValue[Int] = null
  implicit val allowLongTpe: ImmutableValue[Long] = null
  implicit val allowDoubleTpe: ImmutableValue[Double] = null
  implicit val allowFloatTpe: ImmutableValue[Float] = null
  implicit val allowStringTpe: ImmutableValue[String] = null
  implicit val allowCharTpe: ImmutableValue[Char] = null
  implicit val allowBooleanTpe: ImmutableValue[Boolean] = null
  implicit val allowDomElementTpe: ImmutableValue[org.scalajs.dom.Element] = null
  implicit def isImmutable[T]: ImmutableValue[T] = macro io.udash.macros.PropertyMacros.reifyImmutableValue[T]
}

trait ModelPart[T]
object ModelPart {
  // implement with macro that checks if T is a trait that contains only abstract methods that
  // take no parameters and return either immutable value, ModelSeq or another ModelPart
  implicit def isModelPart[T]: ModelPart[T] = macro io.udash.macros.PropertyMacros.reifyModelPart[T]
}

trait ModelSeq[T]
object ModelSeq {
  // implement with macro that checks if T is a scala.collection.Seq (exactly, not a subtype!) whose elements
  // are either immutable values, ModelParts or other ModelSeqs
  implicit def isModelSeq[T <: Seq[_]]: ModelSeq[T] = macro io.udash.macros.PropertyMacros.reifyModelSeq[T]
}

trait ModelValue[T]
object ModelValue {
  // implement with macro that checks if T is ImmutableValue, ModelSeq or ModelPart
  implicit def isModelValue[T]: ModelValue[T] = macro io.udash.macros.PropertyMacros.reifyModelValue[T]
}

class PropertyRegistration[ElementType](s: mutable.Set[ElementType], el: ElementType) extends Registration {
  override def cancel(): Unit =
    s -= el
}