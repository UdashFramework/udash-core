package io.udash.properties.model

import com.avsystem.commons._
import io.udash.properties._
import io.udash.properties.seq.ReadableSeqProperty
import io.udash.properties.single.{AbstractReadableProperty, CastableReadableProperty, Property, ReadableProperty}
import io.udash.utils.CrossCollections

/** Property based on trait representing data model. Read only access. */
trait ReadableModelProperty[+A] extends ReadableProperty[A] {
  /** Returns child ModelProperty[B]. */
  def roSubModel[B: ModelPropertyCreator](f: A => B): ReadableModelProperty[B] =
    macro io.udash.macros.PropertyMacros.reifyRoSubModel[A, B]

  /** Returns child DirectProperty[B]. */
  def roSubProp[B: PropertyCreator](f: A => B): ReadableProperty[B] =
    macro io.udash.macros.PropertyMacros.reifyRoSubProp[A, B]

  /** Returns child DirectSeqProperty[B] */
  def roSubSeq[B, SeqTpe[T] <: BSeq[T]](f: A => SeqTpe[B])(implicit ev: SeqPropertyCreator[B, SeqTpe]): ReadableSeqProperty[B, CastableReadableProperty[B]] =
    macro io.udash.macros.PropertyMacros.reifyRoSubSeq[A, B]

  /** Ensures read-only access to this property. */
  override def readable: ReadableModelProperty[A]
}

trait ModelPropertyMacroApi[A] extends ReadableModelProperty[A] {
  def getSubProperty[T: PropertyCreator](getter: A => T, key: String): ReadableProperty[T]
  def getSubModel[T: ModelPropertyCreator](getter: A => T, key: String): ReadableModelProperty[T]
}

private[properties] trait AbstractReadableModelProperty[A]
  extends AbstractReadableProperty[A] with ModelPropertyMacroApi[A] {
  protected val properties = CrossCollections.createDictionary[Property[_]]

  override lazy val readable: ReadableModelProperty[A] =
    new ReadableWrapper[A](this)
}
