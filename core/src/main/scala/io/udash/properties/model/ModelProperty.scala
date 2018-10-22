package io.udash.properties.model

import io.udash.properties._
import io.udash.properties.seq.SeqProperty
import io.udash.properties.single._

import scala.language.higherKinds

object ModelProperty {
  /** Creates a blank ModelProperty[T].  */
  def blank[T: ModelPropertyCreator : Blank]: ModelProperty[T] =
    Property.blank.asModel

  /** Creates ModelProperty[T] with initial value. */
  def apply[T: ModelPropertyCreator](init: T): ModelProperty[T] =
    Property[T](init).asModel
}

/** Property based on trait representing data model. */
trait ModelProperty[A] extends AbstractReadableModelProperty[A] with AbstractProperty[A] {
  /** Returns child ModelProperty[B]. */
  def subModel[B](f: A => B)(implicit ev: ModelPropertyCreator[B]): ModelProperty[B] =
    macro io.udash.macros.PropertyMacros.reifySubModel[A, B]

  /** Returns child DirectProperty[B]. */
  def subProp[B](f: A => B)(implicit ev: PropertyCreator[B]): Property[B] =
    macro io.udash.macros.PropertyMacros.reifySubProp[A, B]

  /** Returns child DirectSeqProperty[B] */
  def subSeq[B, S[_]](f: A => S[B])(implicit ev: SeqPropertyCreator[B, S[B]]): SeqProperty[B, CastableProperty[B]] =
    macro io.udash.macros.PropertyMacros.reifySubSeq[A, B]
}

