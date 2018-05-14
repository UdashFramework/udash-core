package io.udash.properties

import io.udash.properties.seq.DirectSeqPropertyImpl
import io.udash.properties.single.{CastableProperty, DirectPropertyImpl, ReadableProperty}

import scala.annotation.implicitNotFound

trait PropertyCreator[T] {
  def newProperty(prt: ReadableProperty[_])(implicit blank: Blank[T]): CastableProperty[T] =
    newProperty(blank.value, prt)

  def newProperty(value: T, prt: ReadableProperty[_]): CastableProperty[T] = {
    val prop = create(prt)
    prop.setInitValue(value)
    prop
  }

  protected def create(prt: ReadableProperty[_]): CastableProperty[T]
}

object PropertyCreator extends PropertyCreatorImplicits {
  /** Marker trait for macro-materialized ModelProperty instances. Serves to prioritize macro-generated instance over other implicits. */
  trait MacroGeneratedPropertyCreator

  def propertyCreator[T: PropertyCreator]: PropertyCreator[T] =
    implicitly[PropertyCreator[T]]

  def newID(): PropertyId = PropertyIdGenerator.next()
}

class SinglePropertyCreator[T] extends PropertyCreator[T] {
  protected def create(prt: ReadableProperty[_]): CastableProperty[T] =
    new DirectPropertyImpl[T](prt, PropertyCreator.newID())
}

class SeqPropertyCreator[T : PropertyCreator] extends PropertyCreator[Seq[T]] {
  protected def create(prt: ReadableProperty[_]): CastableProperty[Seq[T]] =
    new DirectSeqPropertyImpl[T](prt, PropertyCreator.newID())
}

@implicitNotFound("Class ${T} cannot be used as ModelProperty template. Add `extends HasModelPropertyCreator[${T}]` to companion object of ${T}.")
abstract class ModelPropertyCreator[T] extends PropertyCreator[T]
object ModelPropertyCreator {
  def materialize[T](implicit ev: IsModelPropertyTemplate[T]): ModelPropertyCreator[T] =
    macro io.udash.macros.PropertyMacros.reifyModelPropertyCreator[T]
}

@implicitNotFound("Class ${T} cannot be used as ModelProperty template. Add `extends HasModelPropertyCreator[${T}]` to companion object of ${T}.")
case class MacroModelPropertyCreator[T](pc: ModelPropertyCreator[T]) extends AnyVal
object MacroModelPropertyCreator {
  implicit def materialize[T](implicit ev: IsModelPropertyTemplate[T]): MacroModelPropertyCreator[T] =
    macro io.udash.macros.PropertyMacros.reifyMacroModelPropertyCreator[T]
}