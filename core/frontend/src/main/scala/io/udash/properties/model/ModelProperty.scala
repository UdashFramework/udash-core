package io.udash.properties.model

import java.util.UUID

import io.udash.properties._
import io.udash.properties.seq.{ReadableSeqProperty, SeqProperty}
import io.udash.properties.single.{CastableProperty, CastableReadableProperty, Property, ReadableProperty}

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

object ModelProperty {
  /** Creates empty ModelProperty[T]. */
  def apply[T](implicit pc: PropertyCreator[T], ev: ModelPart[T], ec: ExecutionContext): ModelProperty[T] =
    Property[T].asModel

  /** Creates ModelProperty[T] with initial value. */
  def apply[T](init: T)(implicit pc: PropertyCreator[T], ev: ModelPart[T], ec: ExecutionContext): ModelProperty[T] =
    Property[T](init).asModel
}

/** Property based on trait representing data model. Read only access. */
trait ReadableModelProperty[A] extends ReadableProperty[A] {
  protected val properties: mutable.Map[String, Property[_]] = mutable.Map()

  /** Returns child ModelProperty[B]. */
  def roSubModel[B](f: A => B)(implicit ev: ModelPart[B]): ReadableModelProperty[B] =
    macro io.udash.macros.PropertyMacros.reifySubProperty[A, B]

  /** Returns child DirectProperty[B]. */
  def roSubProp[B](f: A => B)(implicit ev: ModelValue[B]): ReadableProperty[B] =
    macro io.udash.macros.PropertyMacros.reifySubProperty[A, B]

  /** Returns child DirectSeqProperty[B] */
  def roSubSeq[B](f: A => Seq[B])(implicit ev: ModelSeq[Seq[B]]): ReadableSeqProperty[B, CastableReadableProperty[B]] =
    macro io.udash.macros.PropertyMacros.reifySubProperty[A, B]

  /** ModelProperty is valid if all validators return [[io.udash.properties.Valid]] and all subproperties are valid.
    *
    * @return Validation result as Future, which will be completed on the validation process ending. It can fire validation process if needed. */
  override def isValid: Future[ValidationResult] = {
    import Validator._
    Future.sequence(Seq(super.isValid) ++ properties.values.map(p => p.isValid)).foldValidationResult
  }
}

/** Property based on trait representing data model. */
trait ModelProperty[A] extends ReadableModelProperty[A] with Property[A] {
  /** Returns child ModelProperty[B]. */
  def subModel[B](f: A => B)(implicit ev: ModelPart[B]): ModelProperty[B] =
    macro io.udash.macros.PropertyMacros.reifySubProperty[A, B]

  /** Returns child DirectProperty[B]. */
  def subProp[B](f: A => B)(implicit ev: ModelValue[B]): Property[B] =
    macro io.udash.macros.PropertyMacros.reifySubProperty[A, B]

  /** Returns child DirectSeqProperty[B] */
  def subSeq[B](f: A => Seq[B])(implicit ev: ModelSeq[Seq[B]]): SeqProperty[B, CastableProperty[B]] =
    macro io.udash.macros.PropertyMacros.reifySubProperty[A, B]
}

