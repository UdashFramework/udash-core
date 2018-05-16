package io.udash.properties.model

import io.udash.properties._
import io.udash.properties.seq.SeqProperty
import io.udash.properties.single._

object ModelProperty {
  /** Creates an empty ModelProperty[T].
    * It's not recommended to use this method. Use `apply` with initial value if possible. */
  @deprecated("Use `ModelProperty.blank` instead.", "0.7.0")
  def empty[T: ModelPropertyCreator]: ModelProperty[T] =
    Property.empty.asModel

  /** Creates a blank ModelProperty[T].  */
  def blank[T: ModelPropertyCreator : Blank]: ModelProperty[T] =
    Property.blank.asModel

  /** Creates an empty ModelProperty[T]. */
  @deprecated("Use `ModelProperty.blank` instead.", "0.6.0")
  def apply[T: ModelPropertyCreator]: ModelProperty[T] =
    empty

  /** Creates ModelProperty[T] with initial value. */
  def apply[T: ModelPropertyCreator](init: T): ModelProperty[T] =
    Property[T](init).asModel
}

/** Property based on trait representing data model. */
trait ModelProperty[A] extends AbstractReadableModelProperty[A] with Property[A] {
  /** Returns child ModelProperty[B]. */
  def subModel[B](f: A => B)(implicit ev: ModelPropertyCreator[B]): ModelProperty[B] =
    macro io.udash.macros.PropertyMacros.reifySubModel[A, B]

  /** Returns child DirectProperty[B]. */
  def subProp[B](f: A => B)(implicit ev: PropertyCreator[B]): Property[B] =
    macro io.udash.macros.PropertyMacros.reifySubProp[A, B]

  /** Returns child DirectSeqProperty[B] */
  def subSeq[B](f: A => Seq[B])(implicit ev: SeqPropertyCreator[B]): SeqProperty[B, CastableProperty[B]] =
    macro io.udash.macros.PropertyMacros.reifySubSeq[A, B]
}

