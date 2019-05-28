package io.udash.properties

import io.udash.properties.seq.DirectSeqPropertyImpl
import io.udash.properties.single.{CastableProperty, DirectPropertyImpl, ReadableProperty}

import scala.annotation.implicitNotFound

trait PropertyCreator[T] {
  def newProperty(parent: ReadableProperty[_])(implicit blank: Blank[T]): CastableProperty[T] =
    newProperty(blank.value, parent)

  def newProperty(value: T, parent: ReadableProperty[_]): CastableProperty[T] = {
    val prop = create(parent)
    prop.setInitValue(value)
    prop
  }

  protected def create(parent: ReadableProperty[_]): CastableProperty[T]
}

object PropertyCreator extends PropertyCreatorImplicits {
  /** Marker trait for macro-materialized ModelProperty instances. Serves to prioritize macro-generated instance over other implicits. */
  trait MacroGeneratedPropertyCreator

  def apply[T](implicit ev: PropertyCreator[T]): PropertyCreator[T] = ev

  def newID(): PropertyId = PropertyIdGenerator.next()

  implicit val Double: PropertyCreator[Double] = new SinglePropertyCreator[Double]
  implicit val Float: PropertyCreator[Float] = new SinglePropertyCreator[Float]
  implicit val Long: PropertyCreator[Long] = new SinglePropertyCreator[Long]
  implicit val Int: PropertyCreator[Int] = new SinglePropertyCreator[Int]
  implicit val Short: PropertyCreator[Short] = new SinglePropertyCreator[Short]
  implicit val Byte: PropertyCreator[Byte] = new SinglePropertyCreator[Byte]
  implicit val Boolean: PropertyCreator[Boolean] = new SinglePropertyCreator[Boolean]
  implicit val String: PropertyCreator[String] = new SinglePropertyCreator[String]
}

class SinglePropertyCreator[T] extends PropertyCreator[T] {
  protected def create(prt: ReadableProperty[_]): CastableProperty[T] =
    new DirectPropertyImpl[T](prt, PropertyCreator.newID())
}

class SeqPropertyCreator[T : PropertyCreator] extends PropertyCreator[Seq[T]] {
  protected def create(prt: ReadableProperty[_]): CastableProperty[Seq[T]] =
    new DirectSeqPropertyImpl[T](prt, PropertyCreator.newID())
}
object SeqPropertyCreator {
  implicit def materializeSeq[T: PropertyCreator]: SeqPropertyCreator[T] = new SeqPropertyCreator[T]
}

@implicitNotFound("Class ${T} cannot be used as ModelProperty template. Add `extends HasModelPropertyCreator[${T}]` to companion object of ${T}.")
abstract class ModelPropertyCreator[T] extends PropertyCreator[T]
object ModelPropertyCreator {
  def materialize[T](implicit ev: IsModelPropertyTemplate[T]): ModelPropertyCreator[T] =
    macro io.udash.macros.PropertyMacros.reifyModelPropertyCreator[T]
}