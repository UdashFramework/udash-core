package io.udash.properties.model

import io.udash.properties._
import io.udash.properties.seq.{ReadableSeqProperty, SeqProperty}
import io.udash.properties.single.{CastableProperty, CastableReadableProperty, Property, ReadableProperty}

import scala.concurrent.Future
import scala.scalajs.js

object ModelProperty {
  /** Creates an empty ModelProperty[T]. */
  def empty[T: PropertyCreator : ModelPart]: ModelProperty[T] =
    Property.empty[T].asModel

  /** Creates an empty ModelProperty[T]. */
  def apply[T: PropertyCreator : ModelPart]: ModelProperty[T] =
    empty

  /** Creates ModelProperty[T] with initial value. */
  def apply[T: PropertyCreator : ModelPart](init: T): ModelProperty[T] =
    Property[T](init).asModel
}

/** Property based on trait representing data model. Read only access. */
trait ReadableModelProperty[A] extends ReadableProperty[A] {
  protected val properties: js.Dictionary[Property[_]] = js.Dictionary()

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

    import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
    if (validationResult == null) {
      validationResult = Future.sequence(
        (Iterator(super.isValid) ++ properties.values.iterator.map(p => p.isValid)).toSeq
      ).foldValidationResult
    }
    validationResult
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

