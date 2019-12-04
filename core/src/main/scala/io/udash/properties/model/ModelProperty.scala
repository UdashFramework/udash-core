package io.udash.properties.model

import com.avsystem.commons._
import io.udash.properties._
import io.udash.properties.seq.SeqProperty
import io.udash.properties.single._

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
  def subModel[B: ModelPropertyCreator](f: A => B): ModelProperty[B] =
    macro io.udash.macros.PropertyMacros.reifySubModel[A, B]

  /** Returns child DirectProperty[B]. */
  def subProp[B: PropertyCreator](f: A => B): Property[B] =
    macro io.udash.macros.PropertyMacros.reifySubProp[A, B]

  /** Returns child DirectSeqProperty[B] of any Seq-like field. Note that due to SeqProperty mutable nature,
    * there may be performance overhead while calling subSeq on fields of type more specific than scala.collection.Seq,
    * e.g. scala.collection.immutable.List or scala.collection.immutable.Seq */
  def subSeq[B, SeqTpe[T] <: BSeq[T]](f: A => SeqTpe[B])(
    implicit ev: SeqPropertyCreator[B, SeqTpe]
  ): SeqProperty[B, CastableProperty[B]] = macro io.udash.macros.PropertyMacros.reifySubSeq[A, B, SeqTpe]
}

