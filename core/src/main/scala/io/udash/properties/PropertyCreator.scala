package io.udash.properties

import com.avsystem.commons._
import io.udash.properties.seq.DirectSeqPropertyImpl
import io.udash.properties.single.{CastableProperty, DirectPropertyImpl, ReadableProperty}

import scala.annotation.implicitNotFound
import scala.collection.generic.CanBuildFrom

trait PropertyCreator[T] {
  def newProperty(parent: ReadableProperty[_])(implicit blank: Blank[T]): CastableProperty[T] =
    newProperty(blank.value, parent)

  def newProperty(value: T, parent: ReadableProperty[_]): CastableProperty[T] = {
    val prop = create(parent)
    prop.setInitValue(value)
    prop
  }

  def newImmutableProperty(value: T): ImmutableProperty[T]

  protected def create(parent: ReadableProperty[_]): CastableProperty[T]
}

object PropertyCreator extends PropertyCreatorImplicits {
  /** Marker trait for macro-materialized ModelProperty instances. Serves to prioritize macro-generated instance over other implicits. */
  trait MacroGeneratedPropertyCreator

  def apply[T](implicit ev: PropertyCreator[T]): PropertyCreator[T] = ev

  def newID(): PropertyId = PropertyIdGenerator.next()

  implicit final val Double: PropertyCreator[Double] = new SinglePropertyCreator
  implicit final val Float: PropertyCreator[Float] = new SinglePropertyCreator
  implicit final val Long: PropertyCreator[Long] = new SinglePropertyCreator
  implicit final val Int: PropertyCreator[Int] = new SinglePropertyCreator
  implicit final val Short: PropertyCreator[Short] = new SinglePropertyCreator
  implicit final val Byte: PropertyCreator[Byte] = new SinglePropertyCreator
  implicit final val Char: PropertyCreator[Char] = new SinglePropertyCreator
  implicit final val Boolean: PropertyCreator[Boolean] = new SinglePropertyCreator
  implicit final val String: PropertyCreator[String] = new SinglePropertyCreator

  implicit final def materializeBSeq[T: PropertyCreator]: SeqPropertyCreator[T, BSeq] = new SeqPropertyCreator
  implicit final def materializeISeq[T: PropertyCreator]: SeqPropertyCreator[T, ISeq] = new SeqPropertyCreator
  implicit final def materializeMSeq[T: PropertyCreator]: SeqPropertyCreator[T, MSeq] = new SeqPropertyCreator
  implicit final def materializeVector[T: PropertyCreator]: SeqPropertyCreator[T, Vector] = new SeqPropertyCreator
  implicit final def materializeList[T: PropertyCreator]: SeqPropertyCreator[T, List] = new SeqPropertyCreator
}

final class SinglePropertyCreator[T] extends PropertyCreator[T] {
  protected def create(prt: ReadableProperty[_]): CastableProperty[T] =
    new DirectPropertyImpl[T](prt, PropertyCreator.newID())

  override def newImmutableProperty(value: T): ImmutableProperty[T] = new ImmutableProperty[T](value)
}

final class SeqPropertyCreator[A: PropertyCreator, SeqTpe[T] <: Seq[T]](implicit cbf: CanBuildFrom[Nothing, A, SeqTpe[A]])
  extends PropertyCreator[SeqTpe[A]] {
  protected def create(parent: ReadableProperty[_]): CastableProperty[SeqTpe[A]] =
    new DirectSeqPropertyImpl[A, SeqTpe](parent, PropertyCreator.newID()).asInstanceOf[CastableProperty[SeqTpe[A]]]

  override def newImmutableProperty(value: SeqTpe[A]): ImmutableProperty[SeqTpe[A]] =
    new ImmutableSeqProperty[A, SeqTpe](value).asInstanceOf[ImmutableProperty[SeqTpe[A]]]
}

@implicitNotFound("Class ${T} cannot be used as ModelProperty template. Add `extends HasModelPropertyCreator[${T}]` to companion object of ${T}.")
abstract class ModelPropertyCreator[T] extends PropertyCreator[T] {
  override final def newImmutableProperty(value: T): ImmutableModelProperty[T] = new ImmutableModelProperty[T](value)
}

object ModelPropertyCreator {
  def apply[T](implicit ev: ModelPropertyCreator[T]): ModelPropertyCreator[T] = ev

  def materialize[T: IsModelPropertyTemplate]: ModelPropertyCreator[T] =
    macro io.udash.macros.PropertyMacros.reifyModelPropertyCreator[T]
}